(ns kixi.clifton.cassandra
    (require [com.stuartsierra.component :as component]
             [clojure.tools.logging :refer :all]
             [clojurewerkz.cassaforte.client :as cassaclient]
             [clojurewerkz.cassaforte.query :as cassaquery]
             [clojurewerkz.cassaforte.cql :as cql]
             [kixi.hecuba.hash :refer (sha1)]
             [schema.core :as s]
             [kixi.hecuba.protocols :as kh]
             [camel-snake-kebab :refer (->snake_case_keyword ->kebab-case-keyword)]))

(defn sha1-keyfn
  "From a given payload, compute an id that is a SHA1 dependent on the given types."
  [& types]
  (fn [payload]
    (assert (every? payload (set types))
            (format "Cannot form a SHA1 because required types (%s) are missing from the payload: %s"
                    (apply str (interpose "," (clojure.set/difference (set types) (set (keys payload)))))
                    payload))
    (-> payload ((apply juxt types)) pr-str sha1)))

(defmulti gen-key (fn [typ payload] typ))
(defmethod gen-key :programme [typ payload] ((sha1-keyfn :name) payload))
(defmethod gen-key :project [typ payload] ((sha1-keyfn :name) payload))
(defmethod gen-key :entity [typ payload] (:id payload))
(defmethod gen-key :device [typ payload] (:id payload))
(defmethod gen-key :sensor [typ payload] nil)
(defmethod gen-key :measurement [typ payload] nil)

(defmulti get-table identity)
(defmethod get-table :programme [_] "programmes")
(defmethod get-table :project [_] "projects")
(defmethod get-table :property [_] "entities")
(defmethod get-table :device [_] "devices")
(defmethod get-table :entity [_] "entities")
(defmethod get-table :sensor [_] "sensors")
(defmethod get-table :measurement [_] "measurements")

(defn cassandraify
  "Cassandra has various conventions, such as forbidding hyphens in
  keywords (error) and our current design decision to use varchars for
  fields (for the sake of simplicity)"
  [payload]
  (reduce-kv (fn [s k v] (conj s [(->snake_case_keyword k)
                                  v])) {} payload))


(defn create-db-session
  "Connects to a cluster, creates a session and binds it to system."
  [config]
  (let [host        (:hosts config)
        ks          (:keyspace config)
        port        (:port config)
        credentials (:credentials config)
        cluster     (cassaclient/build-cluster
                     {:contact-points host
                      :port port
                      :credentials credentials})]
    (cassaclient/connect cluster ks)))

(defrecord CassandraComponent [config]
  component/Lifecycle
  (start [this]
    (println "Starting CassandraComponent")
    (-> this
        (assoc :session (create-db-session config))))
  (stop [this]
    (println "Stopping CassandraComponent")
    this)
  kh/Commander
  (kh/upsert! [{:keys [session]} typ payload]
    (assert session "No session!")
    (assert typ "No type!")
    (debugf "type is %s, payload %s" typ payload)
    (binding [cassaclient/*default-session* session]
      (let [id (gen-key typ payload)]
        (cql/insert (get-table typ)
             (let [id-payload (if id (assoc payload :id id) payload)]
               (-> id-payload cassandraify)))
        id)))
  (kh/delete! [{:keys [session]} typ id]
    (assert id "No id!")
    (binding [cassaclient/*default-session* session]
      (cql/delete
       (get-table typ)
       (cassaquery/where :hecuba/id id)))))

(def ^:private Config
  {:hosts [String]
   :keyspace String
   :port Number
   :credentials {:username String 
                 :password String}})

(defn new-component [config]
;  (s/validate Config config)
  (->CassandraComponent config))

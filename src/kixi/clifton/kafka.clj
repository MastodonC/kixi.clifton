(ns kixi.clifton.kafka
  (require [cheshire.core              :as json]
           [com.stuartsierra.component :as component]
           [schema.core                :as s]
           [clj-kafka.zk               :as zk]
           [clj-kafka.consumer.zk      :as kc]
           [clojure.walk               :as walk]
           [clojure.pprint             :refer [print-table]]))

(defn info-table [k xs]
  (print-table [(name k)] (map (partial hash-map (name k)) xs)))

(defn to-zk-config-map [config zk-url]
  (-> config 
      walk/stringify-keys
      (assoc "zookeeper.connect" zk-url)))

(defrecord KafkaSession [config topics]
  component/Lifecycle
  (start [this]
    (println "Starting KafkaComponent")
    (if-let [zk-url (-> this :zookeeper :url)]
      (let [zkm      (to-zk-config-map config zk-url)
            brokers  (or (zk/brokers zkm) nil)
            topics   (or (zk/topics zkm) nil)]
        (info-table :topic topics)
        (info-table :broker brokers)
        (-> this 
            (assoc :zk-config zkm)
            (assoc :metadata.broker-list brokers)
            (assoc :topics topics)))
      (throw (ex-info "No Zookeeper Url" this))))
  (stop [this]
    (println "Stopping KafkaComponent")
    this))

(def ^:private Config {:group.id String
                       (s/optional-key :auto.offset.reset) (s/pred #{"smallest" "largest" "anything else"})
                       (s/optional-key :auto.commit.enable) (s/pred #{"true" "false"})}) 

(defn new-session [config topics]
  (s/validate Config config)
  (->KafkaSession config topics))

(defn consumer [{:keys [zk-config]}]
  (println "><><" (pr-str zk-config))
  (kc/consumer zk-config))

(ns kixi.clifton.system
    (require [com.stuartsierra.component :as component]
             [kixi.clifton.kafka :as kafka]
             [kixi.clifton.cassandra :as cassandra]
             [kixi.clifton.bridge :as bridge]
             [kixi.clifton.zookeeper :as zookeeper]
             [kixi.clifton.config :as config]))

(defn system [] 
  (let [config (config/load-config :prod)]
       (-> (component/system-map 
            :kafka     (kafka/new-session (-> config :kafka :config) 
                                          (-> config :kafka :topics))
            :cassandra (cassandra/new-component (:cassandra config))
            ;; :bridge    (bridge/new-component (:bridge config))
            :zookeeper (zookeeper/new-component (:zookeeper config)))
           (component/system-using 
            {:kafka {:zookeeper :zookeeper}
             ;; :bridge [:cassandra :kafka]
             }))))

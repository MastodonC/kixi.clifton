(ns kixi.clifton.zookeeper
    (require [com.stuartsierra.component :as component]
             [zookeeper :as zk]
             [schema.core :as s]))

(defrecord ZookeeperComponent [url]
  component/Lifecycle
  (start [this]
    (println "Starting ZookeeperComponent")
    this)
  (stop [this]
    (println "Stopping ZookeeperComponent")
    this))

(def ^:private Config {:url String})

(defn new-component [config]
  (s/validate Config config)
  (->ZookeeperComponent (:url config)))



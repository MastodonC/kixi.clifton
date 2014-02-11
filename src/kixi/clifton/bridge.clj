(ns kixi.clifton.bridge
    (require [com.stuartsierra.component :as component]
             [clojure.core.async         :refer [go-loop <! alts! timeout chan close!]]
             [clj-kafka.core             :refer [with-resource]]
             [kixi.clifton.kafka          :as k]
             [clj-kafka.consumer.zk      :as kc]))

(defmacro catching-error [& body]
  `(try
    ~@body
    (catch Exception e#
      (println e#))))

(defn process-messages [kafka]
  (with-resource [c (k/consumer kafka)]
    kc/shutdown
    (println ">>>><><><>" (kc/messages c ["devices"]))
    ;; (doseq [m  (kc/messages c (:topics kafka))]
    ;;   (println m))
    ))

(defrecord BridgeComponent [config running?]
  component/Lifecycle
  (start [this]
    (println "Starting BridgeComponent")
    (reset! (:running? this) true)
    (go-loop []
      (<! (timeout 1000))
      (catching-error

       (println "Gone!")
       (when @running? (recur))))

    this)
  (stop [this]
    (println "Stopping BridgeComponent")
    (reset! (:running? this) false)
    this))

(defn new-component [config]
  (->BridgeComponent config (atom false)))

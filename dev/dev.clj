(ns dev
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.namespace.repl :refer [refresh refresh-all]]
            [kixi.clifton.system :as system]
            [kixipipe.application :as kixi]))

(defn init
  "Constructs the current development system."
  []
  (alter-var-root #'kixi/system
    (constantly (system/system))))

(defn start
  "Starts the current development system."
  []
  (alter-var-root #'kixi/system component/start))

(defn stop
  "Shuts down and destroys the current development system."
  []
  (alter-var-root #'kixi/system
                  (fn [s] (when s (component/stop s)))))

(defn go
  "Initializes the current development system and starts it running."
  []
  (init)
  (start))

(defn reset []
  (stop)
  (refresh :after 'dev/go))

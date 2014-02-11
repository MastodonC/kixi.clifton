(ns kixi.clifton.main
  (:gen-class)
    (require [com.stuartsierra.component :as component]
             [kixi.clifton.config        :as config]
             [kixi.clifton.system]
             [kixipipe.application       :as kixi]
             [clojure.tools.cli          :refer [cli]]))

(defn build-application [opts]
  (let [system (kixi.clifton.system/system)]
    system))

(defn -main [& args]

  (org.slf4j.MDC/put "pipejine.q" "main")

  (let [[opts args banner]
        (cli args
             ["-h" "--help" "Show help"
              :flag true :default false]
             ["-c" "--config" (str "Select a config, one of " (keys config/available-configs))
              :default :prod])]

    (when (:help opts)
      (println banner)
      (System/exit 0))
    (alter-var-root #'kixi/system (fn [_] (component/start (build-application opts))))))

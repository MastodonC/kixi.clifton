(ns kixi.clifton.config
  (require [clojure.edn     :as edn]
           [clojure.java.io :as io]))

(def available-configs {:sandbox (str (System/getProperty "user.home") "/.dev-clifton.edn")
                        :prod  (str (System/getProperty "user.home") "/.clifton.edn")})

(defn combine
  "Merge maps, recursively merging nested maps whose keys collide."
  ([] {})
  ([m] m)
  ([m1 m2]
    (reduce (fn [m1 [k2 v2]]
              (if-let [v1 (get m1 k2)]
                (if (and (map? v1) (map? v2))
                  (assoc m1 k2 (combine v1 v2))
                  (assoc m1 k2 v2))
                (assoc m1 k2 v2)))
            m1 m2))
  ([m1 m2 & more]
    (apply combine (combine m1 m2) more)))

(defn load-config [arg]
  (let [id (if (keyword? arg) arg (keyword arg))
        src (get available-configs id)]
    (combine (edn/read-string (slurp (io/resource "default.clifton.edn")))
             (try (edn/read-string (slurp src)) (catch java.io.FileNotFoundException e)))))


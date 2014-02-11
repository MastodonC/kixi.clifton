;; why ? This means we can always start a repl even with compilation errors.
(defn dev []
  (require 'dev)
  (in-ns 'dev))

(def go #'dev)

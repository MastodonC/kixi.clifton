(def slf4j-version "1.7.6")
(defproject kixi.clifton "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [jline "2.11"]
                 [org.jboss.netty/netty "3.2.10.Final"]
                 [org.apache.zookeeper/zookeeper "3.4.5" :exclusions [[log4j]
                                                                      [org.slf4j/slf4j-api]
                                                                      [jline]]]
                 [org.xerial.snappy/snappy-java "1.1.0.1"]
                 [com.datastax.cassandra/cassandra-driver-core "1.0.5" :exclusions [[org.xerial.snappy/snappy-java]
                                                                                    [org.jboss/netty]]]
                                  
                 [zookeeper-clj "0.9.3"]
                 
                 ;; Logging
                 [org.clojure/tools.logging      "0.2.6"]
                 [ch.qos.logback/logback-classic "1.1.1"]
                 [org.slf4j/jul-to-slf4j         ~slf4j-version]
                 [org.slf4j/jcl-over-slf4j       ~slf4j-version]
                 [org.slf4j/log4j-over-slf4j     ~slf4j-version]

                 [clj-kafka                      "0.1.2-0.8" :exclusions []]
                 [camel-snake-kebab              "0.1.4"]
                 [clojurewerkz/cassaforte        "1.3.0-beta9" :exclusions [[com.datastax.cassandra/cassandra-driver-core]]]

                 [prismatic/schema               "0.2.1"]
                 [com.stuartsierra/component     "0.2.1"]
                 [kixipipe                       "0.14.4"]
                 [org.clojure/tools.cli          "0.3.1"]
                 [cheshire                       "5.3.1"]
                 [org.clojure/core.async         "0.1.267.0-0d7780-alpha"]]
  
  ;; ;; Inconcievably zookeeper refs the duff version of log4j 1.2.15 which
  ;; ;; pulls these in.
  :exclusions [[com.sun.jdmk/jmxtools]
               [com.sun.jmx/jmxri]
               [org.slf4j/slf4j-simple]
               [org.apache.zookeeper/zookeeper]
               [org.slf4j/slf4j-log4j12]
               ]
  
  :profiles {:dev {:dependencies [[lein-marginalia "0.7.1"]
                                  [org.clojure/tools.namespace "0.2.4"]]
                   :source-paths ["dev" "src"]}
             :uberjar {:main kixi.clifton.main
                       :aot [kixi.clifton.main]}})



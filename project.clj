(defproject goldblum "0.1.0-SNAPSHOT"
  :description "Hello World deployment demo for fly.io"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring "1.9.6"]
                 [ring/ring-json "0.5.1"]
                 [ring-cors "0.1.13"]
                 [compojure "1.7.0"]
                 [metosin/reitit "0.7.0"]
                 [metosin/reitit-ring "0.7.0"]
                 [metosin/reitit-swagger "0.7.0"]
                 [metosin/reitit-swagger-ui "0.7.0"]
                 [cheshire "5.11.0"]
                 [org.clojure/tools.logging "1.2.4"]
                 [org.slf4j/slf4j-api "2.0.7"]
                 [ch.qos.logback/logback-classic "1.4.8"]]
  :main ^:skip-aot goldblum.core
  :target-path "target/%s"
  :profiles {:test {:dependencies [[ring/ring-mock "0.4.0"]]}
             :uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})

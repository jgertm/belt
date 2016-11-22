(defproject belt "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [prismatic/schema "1.1.3"]
                 [danlentz/clj-uuid "0.1.6"]
                 [mvxcvi/clj-pgp "0.8.3"]
                 [org.clojure/core.async "0.2.395"]]
  :main ^:skip-aot belt.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})

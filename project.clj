(def crux-version (or (System/getenv "CRUX_VERSION") "19.12-1.6.0-alpha"))
(defproject crux-docker "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :repositories [["releases" {:url "https://repo.clojars.org" :creds :gpg}]]
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.apache.logging.log4j/log4j-api "2.11.0"]
                 [org.apache.logging.log4j/log4j-core "2.11.0"]
                 [org.apache.logging.log4j/log4j-1.2-api "2.11.0"]
                 [org.apache.logging.log4j/log4j-slf4j-impl "2.11.0"]
                 [org.clojure/tools.logging "0.4.1"]
                 [juxt/crux-core ~crux-version]
                 [juxt/crux-rocksdb ~crux-version]
                 [juxt/crux-kafka ~crux-version]
                 [juxt/crux-kafka-connect ~crux-version]
                 [juxt/crux-http-server ~crux-version]
                 [juxt/crux-rdf ~crux-version]
                 [juxt/crux-core ~crux-version]
                 [juxt/crux-http-client ~crux-version]]
  :repl-options {:init-ns crux-docker.core}
  :jar-name "crux-docker.jar"
  :uberjar-name "crux-docker-standalone.jar"
  :main crux-docker.core
  :aot [crux-docker.core])

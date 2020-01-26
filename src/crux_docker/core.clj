(ns crux-docker.core
  "Internal development namespace for Crux. For end-user usage, see
  examples.clj"
  (:require [clojure.java.io :as io]
            [clojure.tools.namespace.repl :as tn]
            [clojure.tools.logging :as log]
            [clojure.spec.alpha :as s]
            [crux.node :as n]
            [crux.standalone :as standalone]
            [crux.db :as db]
            [crux.api :as crux]
            [crux.index :as idx]
            [crux.kafka.embedded :as ek]
            [crux.kv :as kv]
            [crux.http-server :as srv]
            [crux.codec :as c]
            [crux.io :as cio]
            [crux.kafka :as k]
            [crux.memory :as mem]
            [crux.rdf :as rdf]
            [crux.query :as q])
  (:import [crux.api Crux ICruxAPI]
           [ch.qos.logback.classic Level Logger]
           org.slf4j.LoggerFactory
           java.io.Closeable
           java.util.Date)
  (:gen-class))

(def storage-dir "dev-storage")

(defn get-env
  [key default parse]
  (if-let [env (System/getenv key)]
    (parse env)
    default))

(defn parse-int
  [i]
  (Integer/parseInt i))

(defn dev-node-option-defaults []
  {:crux.node/topology                    'crux.kafka/topology
   :crux.node/kv-store                    'crux.kv.rocksdb/kv
   ;:crux.node/kv-store                    'crux.kv.memdb/kv
   :crux.kv/db-dir                        (get-env "STORAGE_DIR" (str storage-dir "/data") str)
   :crux.kv.memdb/persist-on-close?       true
   ;:crux.kv/sync?                         true
   ;:crux.kv/check-and-store-index-version true
   :crux.kafka/bootstrap-servers          (get-env "KAFKA_BOOTSTRAP_SERVER" "10.0.127.51:9092" str)})

(defn dev-http-option-defaults []
  {:server-port (get-env "SERVER_PORT" 3000 parse-int)
   :cors-access-control
                [:access-control-allow-origin [#".*"]
                 :access-control-allow-headers ["X-Requested-With"
                                                "Content-Type"
                                                "Cache-Control"
                                                "Origin"
                                                "Accept"
                                                "Authorization"
                                                "X-Custom-Header"]
                 :access-control-allow-methods [:get :post]]})

(def node-options (dev-node-option-defaults))

(def http-options (dev-http-option-defaults))

(def ^ICruxAPI node)

(defn start-dev-node ^crux.api.ICruxAPI [node-options http-options]
  (let [started (atom [])]
    (try
      (let [cluster-node (doto (crux/start-node node-options)
                           (->> (swap! started conj)))
            http-server (srv/start-http-server cluster-node http-options)]
        (assoc cluster-node
          :http-server http-server))
      (catch Throwable t
        (doseq [c (reverse @started)]
          (cio/try-close c))
        (throw t)))))

(defn stop-dev-node ^crux.api.ICruxAPI [{:keys [http-server embedded-kafka] :as node}]
  (doseq [c [http-server node embedded-kafka]]
    (cio/try-close c)))

(defn start []
  (alter-var-root #'node (fn [_] (start-dev-node node-options http-options)))
  :started)

(defn stop []
  (when (and (bound? #'node)
             (not (nil? node)))
    (alter-var-root #'node stop-dev-node))
  :stopped)

(defn clear []
  (alter-var-root #'node (constantly nil)))

(defn reset []
  (stop)
  (let [result (tn/refresh :after 'dev/start)]
    (if (instance? Throwable result)
      (throw result)
      result)))

(defn delete-storage []
  (stop)
  (cio/delete-dir storage-dir)
  :ok)

(defn set-log-level! [ns level]
  (.setLevel ^Logger (LoggerFactory/getLogger (name ns))
             (when level
               (Level/valueOf (name level)))))

(defn get-log-level! [ns]
  (some->> (.getLevel ^Logger (LoggerFactory/getLogger (name ns)))
           (str)
           (.toLowerCase)
           (keyword)))

(defmacro with-log-level [ns level & body]
  (let [level# (get-log-level! ~ns)]
    (try
      (set-log-level! ~ns ~level)
      ~@body
      (finally
        (set-log-level! ~ns level#)))))

(n/install-uncaught-exception-handler!)

(defn- health-check-internal
  [node]
  (log/info "Getting status")
  (try {:healthy (crux/status node)}
       (catch Exception e {:unhealthy e})))

(defn health-check
  [node]
  (if node
    (let [status (health-check-internal node)]
      (log/infof "Got Status: %s" status)
      status)
    (do
      (log/info "Node is not defied")
      :unhealthy (IllegalStateException. "Node is not defined"))))

(def health-check-threshold
  (get-env "HEALTH_CHECK_THRESHOLD" 3 parse-int))

(def health-check-interval
  (* (get-env "HEALTH_CHECK_INTERVAL" 5 parse-int) 1000))

(def health-check-wait-time
  (* (get-env "HEALTH_CHECK_WAIT_TIME" 5 parse-int) 1000))

(def heal-check-failures (atom 0))

(defn -main
  [& args]
  (log/info "Starting crux node")
  (start)
  (log/infof "Waiting for node to start (%s sec)" health-check-wait-time)
  (Thread/sleep health-check-wait-time)
  (log/infof "Starting health checks  Threshold=%s Interval=%s" health-check-threshold health-check-interval)
  (while (<= @heal-check-failures health-check-threshold)
    (do
      (if (contains? (health-check node) :healthy)
        (swap! heal-check-failures #(max 0 (dec %)))
        (swap! heal-check-failures inc))
      (log/infof "Health check failures %s" @heal-check-failures)
      (Thread/sleep health-check-interval)))
  (log/fatal "Shutting down; health checks failed")
  (stop)
  (System/exit 1))
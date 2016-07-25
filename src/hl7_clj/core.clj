(ns hl7-clj.core
  (:require [clojure.tools.logging :as log]
            [clojure.java.io :as io])
  (:import [java.net ServerSocket Socket]))

(defn receive [sock]
  (.readLine (io/reader sock)))

(defn send [sock msg]
  (let [writer (io/writer sock)]
    (.write writer msg)
    (.flush writer)))

(defonce server (atom nil))

(defn start [port handle]
  (future
    (with-open [server-sock (ServerSocket. port 5)]
      (try
        (reset! server server-sock)
        (log/info "Server started on " port)
        (while true
          (let [sock (.accept server-sock)]
            (log/info "Incomming connection")
            (future (handle sock))))
        (catch Exception e
          (log/error e))))))

(defn handle [sock]
  (log/info "Incomming")
  (let [res (receive sock)]
    (log/info "Message:" res)
    (send sock res)))


(defn open [host port]
  (let [sock (Socket. host port)]
    (log/info "Connecting to server" host ":" port)
    sock))



(comment
  (start 4700 handle)
  (.close @server)

  @server

  (def client (open "localhost" 4700))

  (send client "Hello\n")

  (.close client)

  )



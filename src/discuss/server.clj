(ns discuss.server
  (:require [clojure.java.io :as io]))

(def home (slurp (io/resource "public/index.html")))

(defn handler [request]
  (if (and (= :get (:request-method request))
           (= "/"  (:uri request)))
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body home}
    {:status 404
     :headers {"Content-Type" "text/plain"}
     :body "Not Found"}))

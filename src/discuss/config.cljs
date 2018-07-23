(ns discuss.config
  (:require [clojure.string :as str]))

(goog-define version "0.2.1")
(goog-define remote-host "http://localhost:4284/api")
(goog-define remote-search "http://localhost:9200/")

(def project "discuss")

(def log-level
  "Available log-levels: :severe :warning :info :config :fine :finer :finest."
  :fine)

(defn- get-protocol [url]
  (first (str/split url #":")))

(defn- make-host [host]
  (str (get-protocol (.. js/window -location -href)) "://" host))

(def api {:host  remote-host
          :init  "/town-has-to-cut-spending"
          :base  "/"
          :login "/login"
          :add   {:add-start-statement "/add/start_statement"
                  :add-start-premise   "/add/start_premise"
                  :add-justify-premise "/add/justify_premise"}
          :get   {:references       "/references"
                  :reference-usages "/reference/usages"
                  :statements       "/statements"
                  :statement-url    "/statement/url"}})


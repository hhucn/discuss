(ns discuss.config
  (:require [clojure.string :as str]))

(goog-define version "0.2.1")
(goog-define remote-host "http://localhost:4284/api")

;; Optional
(goog-define search-host "http://muenchhausen.cn.uni-duesseldorf.de:8888")


(def project "discuss")

(def log-level
  "Available log-levels: :severe :warning :info :config :fine :finer :finest."
  :fine)

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


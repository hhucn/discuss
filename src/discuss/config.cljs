(ns discuss.config
  (:require [clojure.string :as str]))

(goog-define version "0.4.0")
(goog-define remote-host "http://muenchhausen.cn.uni-duesseldorf.de:4284/api")

;; Optional
(goog-define search-host "http://muenchhausen.cn.uni-duesseldorf.de:8888")

;; For demo session
(def demo-servers
  [{:name "muenchhausen"
    :dbas "http://muenchhausen.cn.uni-duesseldorf.de:4284/api"
    :eden "http://muenchhausen.cn.uni-duesseldorf.de:8888"}
   {:name "slurpy"
    :dbas "http://slurpy.cn.uni-duesseldorf.de:4284/api"
    :eden "http://slurpy.cn.uni-duesseldorf.de:8888"}
   {:name "localhost without eden"
    :dbas "http://localhost:4284/api"
    :eden nil}])

;; Common
(def project "discuss")

(def log-level
  "Available log-levels: :severe :warning :info :config :fine :finer :finest."
  :fine)

(def api {:init  "/cat-or-dog"
          :base  "/"
          :login "/login"
          :add   {:add-start-statement "/add/start_statement"
                  :add-start-premise   "/add/start_premise"
                  :add-justify-premise "/add/justify_premise"}
          :get   {:references       "/references"
                  :reference-usages "/reference/usages"
                  :statements       "/statements"
                  :statement-url    "/statement/url"}})


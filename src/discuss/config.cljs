(ns discuss.config
  (:require [discuss.config-helper :refer [default-slug]]))

(goog-define version "0.4.0")
(goog-define build-commit "dev")
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

(def initial-discussions
  [{:slug "brexit"}
   {:slug "finanzen"}
   {:slug "umwelt"}])

(def project "discuss")

(def log-level
  "Available log-levels: :severe :warning :info :config :fine :finer :finest."
  :fine)

(def api {:init (default-slug initial-discussions)
          :base  "/"
          :login "/login"
          :logout "/logout"
          :add   {:add-start-statement "/add/start_statement"
                  :add-start-premise   "/add/start_premise"
                  :add-justify-premise "/add/justify_premise"}
          :get   {:references       "/references"
                  :reference-usages "/reference/usages"
                  :statements       "/statements"
                  :statement-url    "/statement/url"}
          :jump  "/:slug/jump/:argument-id"})

(def eden {:add/argument               "/argument"
           :search/arguments-by-author "/arguments/by-author"})

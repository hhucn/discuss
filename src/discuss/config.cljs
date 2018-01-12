(ns discuss.config
  (:require [clojure.string :as str]))

(goog-define version "0.2.1")
(goog-define remote-host "http://localhost:4284/")
(goog-define remote-search "http://muenchhausen.cn.uni-duesseldorf.de:9201/")

(def project "discuss")

(defn- get-protocol [url]
  (first (str/split url #":")))

(defn- make-host [host]
  (str (get-protocol (.. js/window -location -href)) "://" host))

(def api {:host  remote-host
          :init  "api/town-has-to-cut-spending"
          :base  "api/"
          :login "api/login"
          :add   {:add-start-statement "api/add/start_statement"
                  :add-start-premise   "api/add/start_premise"
                  :add-justify-premise "api/add/justify_premise"}
          :get   {:references       "api/references"
                  :reference-usages "api/reference/usages"
                  :statements       "api/statements"
                  :statement-url    "api/statement/url"}})

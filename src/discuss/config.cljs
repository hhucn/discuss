(ns discuss.config)

(goog-define version "0.2.1")
(goog-define remote-host "http://localhost:4284/")
(def project "discuss")

(defn- get-protocol [url]
  (first (clojure.string/split url #":")))

(defn- make-host [host]
  (str (get-protocol (.. js/window -location -href)) "://" host))

(def api {:host  remote-host
          :init  "api/town-has-to-cut-spending"
          :base  "api/"
          :login "api/login"
          :add   {:add-start-statement "api/add/start_statement"
                  :add-start-premise   "api/add/start_premise"
                  :add-justify-premise "api/add/justify_premise"}
          :get   {:references       "api/get/references"
                  :reference-usages "api/get/reference/usages"
                  :statements       "api/get/statements"
                  :statement-url    "api/get/statement/url"}})

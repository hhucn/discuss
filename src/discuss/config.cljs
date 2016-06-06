(ns discuss.config)

(def project "discuss")

(def user "Q2hyaXN0aWFu")

(def api {:host "http://localhost:4284/"
          :init "api/"
          :base "api/"
          :add  {:add-start-statement "add/start_statement"
                 :add-start-premise   "add/start_premise"
                 :add-justify-premise "add/justify_premise"}
          :get  {:reference-usages "get/reference/usages"
                 :statements       "get/statements"}})
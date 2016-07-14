(ns discuss.config)

(def project "discuss")

(def user "Q2hyaXN0aWFu")

(def api {:host "http://dbas/"
          :init "api/elektroautos"
          :base "api/"
          :add  {:add-start-statement "add/start_statement"
                 :add-start-premise   "add/start_premise"
                 :add-justify-premise "add/justify_premise"}
          :get  {:references       "get/references"
                 :reference-usages "get/reference/usages"
                 :statements       "get/statements"
                 :statement-url    "api/get/statement/url"}})
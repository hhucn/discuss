(ns discuss.config)

(def project "discuss")

(def version "0.2.1")

(def user "Q2hyaXN0aWFu")

(def api {:host  "http://localhost:4284/"
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
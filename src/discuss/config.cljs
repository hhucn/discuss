(ns discuss.config)

(def project "discuss")

(def api {:host "http://localhost:4284/"
          :init "api/"
          :base "api/"
          :add  {:add-start-statement "add/start_statement"
                 :add-start-premise   "add/start_premise"
                 :add-justify-premise "add/justify_premise"}})
(ns discuss.config)

(def project "discuss")

(def api {:host "http://localhost:4284/"
          :init "api/"
          :base "api/"
          :add {:start_statement "add/start_statement"
                :start_premise   "add/start_premise"}})
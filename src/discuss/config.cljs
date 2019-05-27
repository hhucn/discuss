(ns discuss.config)

(def project "discuss")
(goog-define version "x.y.z")
(goog-define build-commit "dev")
(goog-define experimental-features? true)
(goog-define generative-tests? false)

(def remote-configuration "/services.edn")

(def host-dbas "http://localhost:4284/api")
;; Optional
(def host-eden nil)

;; For demo session
(def demo-servers
  [{:name "muenchhausen"
    :dbas "http://muenchhausen.cn.uni-duesseldorf.de:4284/api"
    :eden "http://muenchhausen.cn.uni-duesseldorf.de:8888"}
   {:name "slurpy"
    :dbas "http://slurpy.cn.uni-duesseldorf.de:4284/api"
    :eden "http://slurpy.cn.uni-duesseldorf.de:8888"}
   {:name "discuss"
    :dbas "http://discuss.cn.uni-duesseldorf.de:4284/api"
    :eden "http://discuss.cn.uni-duesseldorf.de:8888"}
   {:name "localhost without eden"
    :dbas "http://localhost:4284/api"
    :eden nil}])

(def initial-discussions
  [{:slug "public"}])

(def log-level
  "Available log-levels: :severe :warning :info :config :fine :finer :finest."
  :fine)

(def api {:test "/hello"
          :base  "/"
          :graphql "/v2/query"
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

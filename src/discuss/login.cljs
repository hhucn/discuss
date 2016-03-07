(ns discuss.login
  (:require [ajax.core :refer [GET POST]]
            [discuss.communication :as com]
            [discuss.config :as config]
            [discuss.lib :as lib]))

(defn success
  "Callback function when login was successful"
  [token])

(defn ajax-login [nickname password]
  (lib/update-state-item! :debug :last-api (fn [_] (com/make-url "login")))
  (POST (com/make-url (str (:base config/api) "login"))
        {:body            (lib/clj->json {:nickname nickname
                                      :password password})
         :handler         (fn [msg] (println msg))
         :error-handler   com/error-handler
         :response-format :json
         :headers         {"Content-Type" "application/json"}
         :keywords?       true}))

(defn login [nickname password]
  (println "Nickname:" nickname)
  (println "Password:" password)
  (when (and
          (> (count nickname) 0)
          (> (count password) 0))
    (ajax-login nickname password)))
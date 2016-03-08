(ns discuss.auth
  (:require [ajax.core :refer [POST]]
            [discuss.communication :as com]
            [discuss.config :as config]
            [discuss.debug :as debug]
            [discuss.lib :as lib]))

(defn success-login
  "Callback function when login was successful. Set attributes of user."
  [response]
  (let [nickname (first (clojure.string/split (:token response) "-"))
        token    (:token response)]
    (lib/update-state-map! :user {:nickname nickname
                                  :token token
                                  :logged-in? true})
    (lib/change-view! :discussion)))

(defn ajax-login
  "Get cleaned data and send ajax request."
  [nickname password]
  (let [url (str (:base config/api) "login")]
    (debug/update :last-api url)
    (POST (com/make-url url)
          {:body            (lib/clj->json {:nickname nickname
                                            :password password})
           :handler         success-login
           :error-handler   com/error-handler
           :response-format :json
           :headers         {"Content-Type" "application/json"}
           :keywords?       true})))

(defn login
  "Use login form data, validate it and send ajax request."
  [nickname password]
  (when (and
          (> (count nickname) 0)
          (> (count password) 0))
    (ajax-login nickname password)))

(defn logout
  "Reset user credentials."
  []
  (lib/update-state-map! :user {:nickname ""
                                :token ""
                                :logged-in? false}))
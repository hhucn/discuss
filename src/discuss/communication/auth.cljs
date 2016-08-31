(ns discuss.communication.auth
  (:require [ajax.core :refer [POST]]
            [goog.crypt.base64 :as gtfo]
            [discuss.communication.main :as com]
            [discuss.config :as config]
            [discuss.utils.common :as lib]))

(defn success-login
  "Callback function when login was successful. Set attributes of user."
  [response]
  (let [res (com/process-response response)
        nickname (first (clojure.string/split (:token res) "-"))
        token (:token res)]
    (lib/log "jep")
    (lib/update-user-map! {:nickname   nickname
                           :token      token
                           :logged-in? true})
    (com/ajax-get-and-change-view (lib/get-last-api) :discussion)))

(defn ajax-login
  "Get cleaned data and send ajax request."
  [nickname password]
  (let [url (str (:base config/api) "login")]
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
          (pos? (count nickname))
          (pos? (count password)))
    (ajax-login nickname password)))

(defn one-click-login
  "Directly log-in with my personal user-account."
  []
  (let [magic (gtfo/decodeString config/user)]
    (login magic (clojure.string/lower-case magic))))

(defn logout
  "Reset user credentials."
  []
  (lib/update-user-map! {:nickname   ""
                         :token      ""
                         :logged-in? false}))
(ns discuss.communication.auth
  (:require [discuss.communication.lib :as comlib]
            [discuss.config :as config]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.common :as lib]))

(defn- success-login
  "Callback function when login was successful. Set attributes of user."
  [response]
  (let [{:keys [nickname uid token]} (lib/process-response response)]
     (lib/store-multiple-values-to-app-state!
     [['user/nickname nickname]
      ['user/token token]
      ['user/id uid]
      ['user/logged-in? true]
      ['layout/view :default]])
    (comlib/ajax-get (lib/get-last-api))))

(defn- wrong-login
  "Callback function for invalid credentials."
  [_]
  (lib/error! (t :errors :login))
  (lib/loading? false))

(defn ajax-login
  "Get cleaned data and send ajax request."
  [nickname password]
  (let [url (comlib/make-url (:login config/api))
        body {:nickname nickname
              :password password}]
    (comlib/do-post url body success-login wrong-login)))

(defn login
  "Use login form data, validate it and send ajax request."
  [nickname password]
  (when (and (pos? (count nickname))
             (pos? (count password)))
    (ajax-login nickname password)))

(defn success-logout [response]
  (lib/store-multiple-values-to-app-state!
   [['user/nickname nil]
    ['user/token nil]
    ['user/id nil]
    ['user/logged-in? false]])
  (comlib/ajax-get (lib/get-last-api)))

(defn logout
  "Reset user credentials."
  []
  (when (lib/logged-in?)
    (let [url (comlib/make-url (:logout config/api))]
      (comlib/do-post url nil success-logout comlib/error-handler))))

(ns discuss.communication.auth
  (:require [discuss.communication.lib :as comlib]
            [discuss.config :as config]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.common :as lib]))

(defn- success-login
  "Callback function when login was successful. Set attributes of user."
  [response]
  (let [{:keys [nickname uid token]} (lib/process-response response)
        last-api-call (lib/get-last-api)]
     (lib/store-multiple-values-to-app-state!
      [['user/nickname nickname]
       ['user/token token]
       ['user/id uid]
       ['user/logged-in? true]
       ['layout/view :default]])
     (cond
       (lib/next-view?) (lib/change-to-next-view!)
       (seq last-api-call) (comlib/ajax-get last-api-call))))

(defn- wrong-login
  "Callback function for invalid credentials."
  [_]
  (lib/error! (t :errors :login)))

(defn- ajax-login
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

(defn logout
  "Reset user credentials."
  []
  (when (lib/logged-in?)
    (comlib/do-post (comlib/make-url (:logout config/api)) nil nil nil)
    (lib/store-multiple-values-to-app-state!
     [['user/nickname nil]
      ['user/token nil]
      ['user/id nil]
      ['user/logged-in? false]])
    (comlib/ajax-get (lib/get-last-api))))

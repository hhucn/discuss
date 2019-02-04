(ns discuss.communication.auth
  (:require [goog.string :refer [format]]
            [goog.string.format]
            [discuss.communication.lib :as comlib]
            [discuss.config :as config]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.common :as lib]))

(defn- handle-profile-picture
  "Extract profile-picture and store it in the app-state."
  [response]
  (let [profile-picture (get-in (lib/process-response response) [:user :profilePicture])]
    (lib/store-to-app-state! 'user/avatar profile-picture)))

(defn- get-profile-picture
  "Query graphql and receive the url to the user's profile picture."
  [user-id]
  (let [query (format "{user(uid: %d) {profilePicture(size: 36)}}" user-id)]
    (comlib/ajax-get (:graphql config/api) nil handle-profile-picture {:q query})))

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
       (seq last-api-call) (comlib/ajax-get last-api-call))
     (get-profile-picture uid)))

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
      ['user/logged-in? false]
      ['layout/add? false]])
    (let [last-api-call (lib/get-last-api)]
      (when (seq last-api-call)
        (comlib/ajax-get last-api-call)))))

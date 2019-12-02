(ns discuss.communication.auth
  (:require [goog.string :refer [format]]
            [goog.string.format]
            [discuss.communication.lib :as comlib]
            [discuss.config :as config]
            [discuss.history.discussion :as hdis]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.common :as lib]
            [discuss.utils.localstorage :as store]))

(defn- handle-user-login!
  "Receives user-related login information and stores it to the app-state."
  [nickname uid token]
  (let [last-api-call (hdis/get-last-discussion-url)]
    (lib/store-multiple-values-to-app-state!
      [['user/nickname nickname]
       ['user/token token]
       ['user/id uid]
       ['user/logged-in? true]
       ['layout/view :default]])
    (cond
      (lib/next-view?) (lib/change-to-next-view!)
      (seq last-api-call) (comlib/ajax-get last-api-call))
    (comlib/get-profile-picture uid)))

(defn- success-login
  "Callback function when login was successful. Set attributes of user."
  [response]
  (let [{:keys [nickname uid token]} (lib/process-response response)]
    (store/set-item! :user/nickname nickname)
    (store/set-item! :user/uid uid)
    (store/set-item! :user/token token)
    (handle-user-login! nickname uid token)))

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


;; -----------------------------------------------------------------------------

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
    (store/remove-item! :user/nickname)
    (store/remove-item! :user/uid)
    (store/remove-item! :user/token)
    (let [last-api-call (hdis/get-last-discussion-url)]
      (when (seq last-api-call)
        (comlib/ajax-get last-api-call)))))

(defn load-credentials-from-localstorage!
  "Read user-related login information from localstorage and pass it to the
  app-state"
  []
  (when-not (lib/persist-login-credentials?)
    (let [nickname (store/get-item :user/nickname)
          uid (store/get-item :user/uid)
          token (store/get-item :user/token)]
      (when (every? not-empty [nickname uid token])
        (handle-user-login! nickname uid token)))))
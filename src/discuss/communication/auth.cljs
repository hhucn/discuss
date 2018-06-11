(ns discuss.communication.auth
  (:require [ajax.core :refer [POST]]
            [discuss.communication.lib :as comlib]
            [discuss.config :as config]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.common :as lib]
            [om.next :as om]
            [discuss.parser :as parser]))

(defn- success-login
  "Callback function when login was successful. Set attributes of user."
  [response]
  (let [{:keys [nickname token]} (lib/process-response response)]
    (om/transact! parser/reconciler `[(user/nickname {:value ~nickname})
                                      (user/token {:value ~token})
                                      (user/logged-in? {:value true})
                                      (layout/view {:value :default})])
    ;; TODO resend the last API call again
    ))

(defn- wrong-login
  "Callback function for invalid credentials."
  [_]
  (lib/error-msg! (t :errors :login))
  (lib/loading? false))

(defn ajax-login
  "Get cleaned data and send ajax request."
  [nickname password]
  (let [url (:login config/api)]
    (POST (comlib/make-url url)
          {:body            (lib/clj->json {:nickname nickname
                                            :password password})
           :handler         success-login
           :error-handler   wrong-login
           :response-format :json
           :headers         {"Content-Type" "application/json"}
           :keywords?       true})))

(defn login
  "Use login form data, validate it and send ajax request."
  [nickname password]
  (when (and (pos? (count nickname))
             (pos? (count password)))
    (ajax-login nickname password)))

(defn logout
  "Reset user credentials."
  []
  (om/transact! parser/reconciler `[(user/nickname {:value nil})
                                    (user/token {:value nil})
                                    (user/logged-in? {:value false})]))

(ns discuss.communication.lib
  "Helper-functions for the communication component."
  (:require [ajax.core :refer [GET POST]]
            [discuss.config :as config]
            [clojure.string :as str]
            [discuss.utils.common :as lib]
            [om.next :as nom]
            [discuss.parser :as parser]))

;;;; Auxiliary functions
(defn make-url
  "Prefix url with host."
  [url]
  (str (:host config/api) url))

(defn token-header
  "Return token header for ajax request if user is logged in."
  []
  (when (lib/logged-in?)
    {"X-Authentication" (lib/clj->json {:type "user" :token (lib/get-token)})}))


;;;; Handlers
(defn error-handler
  "Generic error handler for ajax requests."
  [{:keys [status status-text]}]
  (lib/log (str "I feel a disturbance in the Force... " status " " status-text))
  (lib/loading? false))

(defn success-handler-next-view
  "After the successful ajax call, change the view to the previously saved next
  view."
  [response]
  (lib/change-to-next-view!)
  (lib/update-all-states! response))

(defn index-handler
  [response]
  (let [{:keys [items bubbles]} (lib/process-response response)]
    (lib/change-to-next-view!)
    (nom/transact! parser/reconciler `[(discussion/items {:items ~items})])
    (nom/transact! parser/reconciler `[(discussion/bubbles {:bubbles ~bubbles})])))

(nom/app-state parser/reconciler)

(defn ajax-get
  "Make ajax call to dialog based argumentation system."
  ([url headers handler params]
   (lib/no-error!)
   (lib/last-api! url)
   (lib/loading? true)
   (println "Request to:" (make-url url))
   (GET (make-url url)
        {:handler       handler
         :headers       (merge (token-header) headers)
         :params        params
         :error-handler error-handler}))
  ([url headers handler] (ajax-get url headers handler nil))
  ([url headers] (ajax-get url headers lib/update-all-states!))
  ([url] (ajax-get url {})))

(defn ajax-get-and-change-view
  "Make ajax call to jump right into the discussion and change to discussion
  view."
  ([url view handler]
   (lib/next-view! view)
   (ajax-get url {} handler))
  ([url view]
   (ajax-get-and-change-view url view success-handler-next-view)))


(defn jump-to-argument
  "Jump directly into the discussion to let the user argue about the given argument.

   ** TODO: Update route **"
  [slug arg-id]
  (let [url (str/join "/" ["api" slug "jump" arg-id])]
    (ajax-get-and-change-view url :discussion)))

(defn init!
  "Request initial data from API."
  []
  (let [url (:init config/api)]
    (lib/update-state-item! :layout :add? (fn [_] false))
    (ajax-get-and-change-view url :default index-handler)))

(ns discuss.communication.lib
  "Helper-functions for the communication component."
  (:require [ajax.core :refer [GET POST]]
            [discuss.config :as config]
            [discuss.utils.common :as lib]))

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


;;;; Generic handlers
(defn error-handler
  "Generic error handler for ajax requests."
  [{:keys [status status-text]}]
  (lib/log (str "I feel a disturbance in the Force... " status " " status-text))
  (lib/loading? false))


(defn ajax-get
  "Make ajax call to dialog based argumentation system."
  ([url headers handler params]
   (lib/no-error!)
   (lib/last-api! url)
   (lib/loading? true)
   (GET (make-url url)
        {:handler       handler
         :headers       (merge (token-header) headers)
         :params        params
         :error-handler error-handler}))
  ([url headers handler] (ajax-get url headers handler nil))
  ([url headers] (ajax-get url headers lib/update-all-states!))
  ([url] (ajax-get url {})))

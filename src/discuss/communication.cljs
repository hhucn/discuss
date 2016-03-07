(ns discuss.communication
  (:require [ajax.core :refer [GET POST]]
            [discuss.config :as config]
            [discuss.lib :as lib]))

(defn make-url
  "Add suffix if not provided."
  [url]
  (str (:host config/api) url))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

(defn ajax-get [url]
  (lib/update-state-item! :debug :last-api (fn [_] url))
  (GET (make-url url)
       {:handler lib/update-all-states!
        :error-handler error-handler}))
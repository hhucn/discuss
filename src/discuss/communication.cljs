(ns discuss.communication
  (:require [om.core :as om :include-macros true]
            [ajax.core :refer [GET POST]]
            [discuss.lib :as lib]
            [clojure.walk :refer [keywordize-keys]]))

(def api-host "http://localhost:4284/")

(defn- make-url
  "Add suffix if not provided."
  [url]
  (str api-host url))

(defn- update-items! [response]
  (let [res (keywordize-keys response)
        items (:items res)]
    (lib/update-state! :items items)))

(defn- error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

(defn ajax-get [url]
  (GET (make-url url)
       {:handler update-items!
        :error-handler error-handler}))

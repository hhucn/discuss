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

(defn- update-item-list! [items]
  (let [items-state (lib/items)]
    (om/transact! items-state (fn [] (keywordize-keys items)))))

(defn- handler [response]
  (let [discussion (get response "discussion")
        issues (get response "issues")
        items (get response "items")]
    (update-item-list! items)))

(defn- error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

(defn ajax-get [url]
  (GET (make-url url)
       {:handler handler
        :error-handler error-handler}))

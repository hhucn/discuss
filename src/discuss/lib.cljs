(ns discuss.lib
  (:require [om.core :as om :include-macros true]
            [clojure.walk :refer [keywordize-keys]]
            [discuss.config :as config]))

(defn prefix-name
  "Create unique id for DOM elements."
  [name]
  (str config/project "-" name))

(def app-state
  (atom {:discussion {}
         :issues {}
         :items {}
         :layout {:title "discuss"
                  :intro "The current discussion is about"
                  :template :discussion}
         :debug {:last-api ""}
         }))

(defn init!
  "Initialize initial data from API."
  []
  (let [url (:init config/api)]
    (discuss.communication/ajax-get url)))

(defn get-cursor
  "Return a cursor to the corresponding keys in the app-state."
  [key]
  (om/ref-cursor (key (om/root-cursor app-state))))

(defn update-state-item!
  "Get the cursor for given key and select a field to apply the function to it."
  [col key f]
  (om/transact! (get-cursor col) key f))

(defn update-state-map!
  "Get the cursor for given key and update it with the new collection of data."
  [key col]
  (let [state (get-cursor key)]
    (om/transact! state (fn [] col))))

(defn- update-all-states!
  "Update item list with the data provided by the API."
  [response]
  (let [res (keywordize-keys response)
        items (:items res)
        discussion (:discussion res)
        issues (:issues res)]
    (update-state-map! :items items)
    (update-state-map! :discussion discussion)
    (update-state-map! :issues issues)
    (update-state-item! :debug :response (fn [_] res))
    ))

 (defn change-view!
   "Switch to a different view."
   [view]
   (fn []
     (update-state-item! :layout :template (fn [_] view))))

(defn get-value-by-id
  "Return value of element matching the id."
  [id]
  (.-value (. js/document (getElementById (prefix-name id)))))
(ns discuss.lib
  (:require [om.core :as om :include-macros true]
            [clojure.walk :refer [keywordize-keys]]
            [discuss.config :as config]))

(defn prefix-name [name]
  (str config/project "-" name))

(def app-state
  (atom {:title "discuss"
         :discussion {}
         :issues {}
         :items []
         :layout {:intro "The current discussion is about"}
         :debug-last-api ""
         }))

(defn get-cursor
  "Return a cursor to the corresponding keys in the app-state."
  [key]
  (om/ref-cursor (key (om/root-cursor app-state))))

(defn update-state!
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
    (. js/console (log res))
    (update-state! :items items)
    (update-state! :discussion discussion)
    (update-state! :issues issues)))
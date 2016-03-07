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
                  :template :discussion
                  :add? false
                  :add-text "Let me enter my reason!"}
         :debug {:last-api ""}
         :user {:nickname ""
                :token ""
                :logged-in? false}
         }))

(defn init!
  "Initialize initial data from API."
  []
  (let [url (:init config/api)]
    (discuss.communication/ajax-get url)))

;; Get
(defn get-cursor
  "Return a cursor to the corresponding keys in the app-state."
  [key]
  (om/ref-cursor (key (om/root-cursor app-state))))

(defn get-token
  "Return the user's token for discussion system."
  []
  (get-in @app-state [:user :token]))

(defn logged-in?
  "Return true if user is logged in."
  []
  (get-in @app-state [:user :logged-in?]))

;; State changing
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
    (println "Premise text")
    (println (:add_premise_text discussion))
    (update-state-item! :debug :response (fn [_] res))))


;; Change views
(defn change-view!
  "Switch to a different view."
  [view]
  (update-state-item! :layout :template (fn [_] view)))

(defn show-add-form
  "Shows a form to enable user-added content."
  []
  (println (get-in @app-state [:user :logged-in?]))
  (when (get-in @app-state [:user :logged-in?])
    (update-state-item! :layout :add? (fn [_] true))))

(defn hide-add-form
  "Hide the user form."
  []
  (update-state-item! :layout :add? (fn [_] false)))

;; Other
(defn get-value-by-id
  "Return value of element matching the id."
  [id]
  (.-value (. js/document (getElementById (prefix-name id)))))


;; CLJS to JS
(defn clj->json
  "Convert CLJS to valid JSON."
  [col]
  (.stringify js/JSON (clj->js col)))
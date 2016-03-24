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
                  :intro "The current discussion is about:"
                  :template :discussion
                  :add? false
                  :add-text "Let me enter my reason!"
                  :add-type nil
                  :loading? true}
         :debug {:last-api ""}
         :user {:nickname ""
                :token ""
                :selection nil
                :logged-in? false}
         :sidebar {:show? false}
         }))

;; Get
(defn get-cursor
  "Return a cursor to the corresponding keys in the app-state."
  [key]
  (om/ref-cursor (key (om/root-cursor app-state))))

(defn get-token
  "Return the user's token for discussion system."
  []
  (get-in @app-state [:user :token]))

(defn get-bubbles
  "Return message bubbles from DBAS."
  []
  (get-in @app-state [:discussion :bubbles]))


;; Booleans
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

(defn loading?
  ([]
   (get-in @app-state [:layout :loading?]))
  ([bool]
   (update-state-item! :layout :loading? (fn [_] bool))))

(defn update-all-states!
  "Update item list with the data provided by the API."
  [response]
  (let [res (keywordize-keys response)
        items (:items res)
        discussion (:discussion res)
        issues (:issues res)]
    ;; OPTIMIZE
    (update-state-map! :items items)
    (update-state-map! :discussion discussion)
    (update-state-map! :issues issues)
    (update-state-item! :debug :response (fn [_] res))
    (loading? false)))


;; Change views
(defn change-view!
  "Switch to a different view."
  [view]
  (update-state-item! :layout :template (fn [_] view)))

(defn show-add-form
  "Shows a form to enable user-added content."
  []
  (when (logged-in?)
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

(defn log [arg]
  (.log js/console arg))


;; CLJS to JS
(defn clj->json
  "Convert CLJS to valid JSON."
  [col]
  (.stringify js/JSON (clj->js col)))
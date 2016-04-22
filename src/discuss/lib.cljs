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
         :issues     {}
         :items      {}
         :layout     {:title     "discuss"
                      :intro     "The current discussion is about:"
                      :template  :discussion
                      :add?      false
                      :add-text  "Let me enter my reason!"
                      :add-type  nil
                      :loading?  true
                      :reference ""
                      :error?    false
                      :error-msg nil}
         :debug      {:last-api ""}
         :user       {:nickname   ""
                      :token      ""
                      :statement  ""
                      :selection  nil
                      :logged-in? false}
         :sidebar    {:show? false}
         }))

;; Get
(defn get-cursor
  "Return a cursor to the corresponding key in the app-state."
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

(defn get-add-text
  "Return message for adding new statements."
  []
  (get-in @app-state [:layout :add-text]))

(defn get-add-premise-text
  "Return text for adding new premise."
  []
  (get-in @app-state [:discussion :add_premise_text]))

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
  "Return boolean if app is currently loading content. Provide a boolean to change the app-state."
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
    ;; @OPTIMIZE
    (update-state-map! :items items)
    (update-state-map! :discussion discussion)
    (update-state-map! :issues issues)
    (update-state-item! :debug :response (fn [_] res))
    (loading? false)))

;; Show error messages
(defn error?
  "Return boolean indicating if there are errors or not. Provide a boolean to change the app-state."
  ([]
   (get-in @app-state [:layout :error?]))
  ([bool]
   (update-state-item! :layout :error? (fn [_] bool))))

(defn error-msg!
  "Set error message."
  [msg]
  (when (< 0 (count msg))
    (error? true))
  (update-state-item! :layout :error-msg (fn [_] msg)))

(defn no-error!
  "Macro to remove all error warnings."
  []
  (error? false)
  (error-msg! nil))

(defn get-error
  "Return error message."
  []
  (get-in @app-state [:layout :error-msg]))


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


;; Mouse interaction
(defn get-selection
  "Return the stored selection of the user."
  []
  (get-in @app-state [:user :selection]))

(defn save-selected-text
  "Get the users selection and save it."
  []
  (let [selection (str (.getSelection js/window))]
    (when (and (> (count selection) 0)
               (not= selection (get-selection)))
      ;; TODO hier gehts weiter
      ; (println (sel/setCursorPosition))
      ; (println (sel/setCursorPosition 100 200))
      (update-state-item! :user :selection (fn [_] selection)))))

(defn save-mouse-position
  "Store mouse position."
  [[x y]]
  (update-state-item! :user :mouse-x (fn [_] x))
  (update-state-item! :user :mouse-y (fn [_] y)))


;;;; Tooltip
(defn calc-tooltip-position
  "Create a new tooltip at given selection. Creates a rectangle around the selection,
   which has position-properties and which are useful for positioning of the tooltip."
  []
  (let [selection (.getSelection js/window)
        range (.getRangeAt selection 0)
        rect (.getBoundingClientRect range)
        top (.-top rect)
        left (.-left rect)
        width (.-width rect)]
    [top left width]))


(defn move-tooltip
  "Sets CSS position of tooltip."
  []
  (let [tooltip (.getElementById js/document (prefix-name "tooltip"))
        [top left width] (calc-tooltip-position)]
    (set! (.. tooltip -style -top) (str top "px"))
    (set! (.. tooltip -style -left) (str left "px"))
    ;(set! (.. tooltip -style -display) "none")
    ))

;;;; Other
(defn get-value-by-id
  "Return value of element matching the id."
  [id]
  (let [element (.getElementById js/document (prefix-name id))]
    (when element (.-value element))))

(defn log
  "Print argument as JS object to be accessible from the console."
  [arg]
  (.log js/console arg))

(defn substring?
  "Evaluates if a substring is contained in the given string."
  [sub st]
  (not= (.indexOf st sub) -1))


;; CLJS to JS
(defn clj->json
  "Convert CLJS to valid JSON."
  [col]
  (.stringify js/JSON (clj->js col)))
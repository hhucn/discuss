(ns discuss.utils.common
  (:require [om.core :as om :include-macros true]
            [clojure.walk :refer [keywordize-keys]]
            [cognitect.transit :as transit]
            [discuss.config :as config]
            [goog.fx.dom :as fx]))

(defn prefix-name
  "Create unique id for DOM elements."
  [name]
  (str config/project "-" name))

(defonce app-state
  (atom {:discussion {}
         :issues     {}
         :items      {}
         :layout     {:title     "discuss"
                      :intro     "The current discussion is about:"
                      :template  :discussion
                      :add?      false
                      :add-text  "Let me enter my reason!"
                      :add-type  nil
                      :loading?  false
                      :reference ""
                      :error?    false
                      :error-msg nil}
         :debug      {:last-api ""}
         :user       {:nickname   "kangaroo"
                      :token      "razupaltuff"
                      :csrf       nil
                      :statement  ""
                      :selection  nil
                      :logged-in? false}
         :clipboard  {:selections nil
                      :current    nil}
         :sidebar    {:show? true}
         }))

;; Get
(defn get-cursor
  "Return a cursor to the corresponding key in the app-state."
  [key]
  (om/ref-cursor (key (om/root-cursor app-state))))

(defn get-nickname
  "Return the user's nickname, with whom she logged in."
  []
  (get-in @app-state [:user :nickname]))

(defn get-token
  "Return the user's token for discussion system."
  []
  (get-in @app-state [:user :token]))

(defn get-issues
  "Returns list of dictionaries with all available issues."
  []
  (get-in @app-state [:issues :all]))

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


;;;; CSRF Token
(defn get-csrf
  "Return the user's csrf token for discussion system."
  []
  (get-in @app-state [:user :csrf]))

(defn set-csrf!
  "Set the newly received CSRF token."
  [csrf]
  (update-state-item! :user :csrf (fn [_] csrf)))

(defn loading?
  "Return boolean if app is currently loading content. Provide a boolean to change the app-state."
  ([]
   (get-in @app-state [:layout :loading?]))
  ([bool]
   (update-state-item! :layout :loading? (fn [_] bool))))

(defn update-all-states!
  "Update item list with the data provided by the API.

  ** Needs optimizations **"
  [response]
  (let [res (keywordize-keys response)
        items (:items res)
        discussion (:discussion res)
        issues (:issues res)]
    (loading? false)
    (update-state-map! :items items)
    (update-state-map! :discussion discussion)
    (update-state-map! :issues issues)
    (update-state-item! :debug :response (fn [_] res))))

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
  (when (pos? (count msg))
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

(defn save-mouse-position
  "Store mouse position."
  [[x y]]
  (update-state-item! :user :mouse-x (fn [_] x))
  (update-state-item! :user :mouse-y (fn [_] y)))


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

(defn str->int
  "Convert String to Integer."
  [s]
  (js/parseInt s))


;;;; CSS modifications
(defn toggle-class
  "Toggle CSS class of provided DOM element. A third paramenter as boolean can be provided to
   force removing or adding the class."
  ([dom-element class]
   (.classList/toggle dom-element class))
  ([dom-element class bool]
   (.classList/toggle dom-element class bool)))

(defn remove-class
  "Remove a specific class of a DOM element."
  [dom-element class]
  (toggle-class dom-element class false))

(defn add-class
  "Add a specific class to a DOM element."
  [dom-element class]
  (toggle-class dom-element class true))


;;;; CLJS to JS
(defn clj->json
  "Convert CLJS to valid JSON."
  [col]
  (.stringify js/JSON (clj->js col)))

(defn json->clj
  "Use cognitec's transit reader for json to convert it to proper Clojure datastructures."
  [response]
  (let [r (transit/reader :json)]
    (keywordize-keys (transit/read r response))))
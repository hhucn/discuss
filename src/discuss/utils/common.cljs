(ns discuss.utils.common
  (:require [om.core :as om :include-macros true]
            [om.next :as nom :refer-macros [defui]]
            [clojure.walk :refer [keywordize-keys]]
            [clojure.string :refer [trim trim-newline]]
            [cljs.spec.alpha :as s]
            [goog.string :as gstring]
            [cognitect.transit :as transit]
            [inflections.core :refer [plural]]
            [discuss.config :as config]
            [discuss.specs :as specs]
            [discuss.parser :as parser]))

(defn prefix-name
  "Create unique id for DOM elements."
  [name]
  (str config/project "-" name))

(defonce app-state
         (atom {:discussion {}
                :issues     {}
                :items      {}
                :search     {:results []}
                :layout     {:title         "discuss"
                             :template      :discussion
                             :next-template :discussion
                             :add?          false
                             :add-type      nil
                             :loading?      false
                             :error?        false
                             :error-msg     nil
                             :language      :en}
                :user       {:nickname   "kangaroo"
                             :token      "razupaltuff"
                             :avatar     ""
                             :csrf       nil
                             :statement  ""
                             :selection  nil
                             :logged-in? false}
                :references {:selected nil
                             :highlighted #{}}
                :origin     {}
                :clipboard  {:selections nil
                             :current    nil}
                :sidebar    {:show? true}
                :common     {:last-api ""}}))

;;;; CLJS <--> JS
(defn clj->json
  "Convert CLJS to valid JSON."
  [col]
  (.stringify js/JSON (clj->js col)))

(defn json->clj
  "Use cognitect's transit reader for json to convert it to proper Clojure data
  structures."
  [response]
  (cond
    (string? response) (let [r (transit/reader :json)]
                         (keywordize-keys (transit/read r response)))
    :default (keywordize-keys response)))

(defn str->int
  "Convert String to Integer."
  [s]
  (let [converted (js/parseInt s)]
    (when-not (js/isNaN converted)
      converted)))


;;;; React Key Generation
(defn get-unique-key
  "Return unique react-key."
  []
  (str (prefix-name "unique-react-key-") (random-uuid)))

(defn unique-key-dict
  "Generate a dictionary with unique key."
  []
  {:key (get-unique-key)})

(defn unique-react-key-dict
  "Generate a dictionary with unique react-key."
  []
  {:react-key (get-unique-key)})

(defn merge-react-key
  "Get a unique key, create a small map with :react-key property and merge it
  with the given collection."
  [col]
  (merge (unique-key-dict) col))


;;;; Getter
(defn get-cursor
  "Return a cursor to the corresponding key in the app-state."
  {:deprecated 0.4}
  [key]
  (om/ref-cursor (key (om/root-cursor app-state))))

(defn get-nickname
  "Return the user's nickname, with whom she logged in."
  []
  (:user/nickname @(nom/app-state parser/reconciler)))

(defn get-avatar
  "Return the URL of the user's avatar."
  []
  (:user/avatar @(nom/app-state parser/reconciler)))

(defn get-token
  "Return the user's token for discussion system."
  []
  (:user/token @(nom/app-state parser/reconciler)))

(defn get-issues
  "Returns list of dictionaries with all available issues."
  []
  (:issue/list @(nom/app-state parser/reconciler)))

(defn get-issue
  "Return specific issue, matching by id or title."
  [issue]
  (cond
    (number? issue) (first (filter #(= (str->int (:uid %)) issue) (get-issues)))
    (string? issue) (first (filter #(= (:title %) issue) (get-issues)))))

(defn get-items
  "Returns list of items from the discussion."
  []
  (:discussion/items @(nom/app-state parser/reconciler)))

(defn get-bubbles
  "Return message bubbles from DBAS."
  []
  (:discussion/bubbles @(nom/app-state parser/reconciler)))

(defn get-add-premise-text
  "Return text for adding new premise."
  []
  (get-in @app-state [:discussion :add_premise_text]))


;;;; Booleans
(defn logged-in?
  "Return true if user is logged in."
  []
  (:user/logged-in? @(nom/app-state parser/reconciler)))


;;;; State changing
(defn update-state-item!
  "Get the cursor for given key and select a field to apply the function to it."
  [col key f]
  (om/transact! (get-cursor col) key f))

(defn update-state-map!
  "Get the cursor for given key and update it with the new collection of data."
  [key col]
  (let [state (get-cursor key)]
    (om/transact! state (fn [] col))))


;;;; References
;; TODO Move to references/lib
(defn get-references
  "Returns a list of references which were received from the discussion system."
  []
  (get-in @app-state [:common :references]))

(defn get-reference
  "Returns a map matching a specific id. This id must be a number."
  ([id col] (first (filter #(= (str->int id) (:uid %)) col)))
  ([id] (get-reference id (get-references))))


;;;; CSRF Token
(defn get-csrf
  "Return the user's csrf token for discussion system."
  []
  (get-in @app-state [:user :csrf]))

(defn set-csrf!
  "Set the newly received CSRF token."
  [csrf]
  (update-state-item! :user :csrf (fn [_] csrf)))


;; Show error messages
(defn error?
  "Return boolean indicating if there are errors or not. Provide a boolean to
  change the app-state."
  ([] (get-in @app-state [:layout :error?]))
  ([bool] (update-state-item! :layout :error? (fn [_] bool))))

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
(defn hide-add-form!
  "Hide the user form."
  {:deprecated 0.4}
  []
  (update-state-item! :layout :add? (fn [_] false)))

(defn hide-add-form-next!
  "Hide the user form."
  []
  (nom/transact! parser/reconciler `[(layout/add? {:value false})]))

(defn show-add-form!
  "Shows a form to enable user-added content."
  []
  (when (logged-in?)
    (update-state-item! :layout :add? (fn [_] true))))

(defn current-view
  "Returns the current selected template, which should be visible in the
  main-content-view."
  []
  (get-in @app-state [:layout :template]))

(defn change-view!
  "Switch to a different view."
  {:deprecated 0.4}
  [view]
  (hide-add-form!)
  (update-state-item! :layout :template (fn [_] view)))

(defn change-view-next!
  [view]
  (nom/transact! parser/reconciler `[(layout/view {:value ~view})
                                     (layout/add? {:value false})]))

(defn next-view!
  "Set the next view, which should be loaded after the ajax call has finished."
  [view]
  (hide-add-form!)
  (update-state-item! :layout :next-template (fn [_] view)))

(defn change-to-next-view!
  "Set next view to current view. Falls back to default if there is no different
  next view."
  []
  (let [current-view (current-view)
        next-view (get-in @app-state [:layout :next-template])]
    (if (= current-view next-view)
      (change-view! :default)
      (change-view! next-view!))))

(defn save-current-and-change-view!
  "Saves the current view and changes to the next specified view. Used for the
  'close' button in some views."
  [view]
  (next-view! (current-view))
  (change-view! view))


;;;; Last-api
(defn last-api!
  "Keep last-api call. Useful to login and then re-request the url to jump to
  the same position in the discussion, but this time as a logged in user."
  [url]
  (update-state-item! :common :last-api (fn [_] url)))

(defn get-last-api
  "Return url of last API call."
  []
  (get-in @app-state [:common :last-api]))


;;;; Generic Handlers
(defn loading?
  "Return boolean if app is currently loading content. Provide a boolean to
  change the app-state."
  ([] (get-in @app-state [:layout :loading?]))
  ([bool] (update-state-item! :layout :loading? (fn [_] bool))))

(defn process-response
  "Generic success handler, which sets error handling and returns a cljs-compatible response."
  [response]
  (let [res (json->clj response)
        error (:error res)]
    (loading? false)
    (if (pos? (count error))
      (error-msg! error)
      (do (no-error!)
          res))))

(defn update-all-states!
  "Update item list with the data provided by the API.

  ** Needs optimizations **"
  [response]
  (let [res (process-response response)
        items (:items res)
        discussion (:discussion res)
        issues (:issues res)]
    (update-state-map! :items items)
    (update-state-map! :discussion discussion)
    (update-state-map! :issues issues)
    (update-state-item! :user :avatar (fn [_] (get-in res [:extras :users_avatar])))))


;;;; Selections
(defn get-selection
  "Return the stored selection of the user."
  []
  (get-in @app-state [:user :selection]))

(defn remove-selection!
  "Remove current selection for a 'clean' statement."
  []
  (update-state-item! :user :selection (fn [_] nil)))


;;;; Origins
(defn store-origin!
  "Stores an origin into the app-state."
  [origin]
  (update-state-map! :origin origin))

(defn get-origin
  "Return the origin of a statement if a statement has a different origin."
  []
  (get @app-state :origin))

(defn remove-origin!
  "Remove currently stored origin."
  []
  (update-state-map! :origin {}))

(s/fdef store-origin!
        :args (s/cat :origin ::specs/origin))
(s/fdef get-origin
        :ret ::specs/origin)


;;;; String Stuff
(defn substring?
  "Evaluates if a substring is contained in the given string."
  [sub st]
  (not= (.indexOf st sub) -1))

(defn singular->plural
  "Return pluralized string of word if number is greater than one."
  [number word]
  (when (and (string? word)
             (or (pos? number)
                 (zero? number)))
    (if (not= 1 number)
      (plural word)
      word)))

(defn trim-and-normalize
  "Remove all surrounding whitespaces and reduce all 'inner' whitespaces to a
  single space."
  [str]
  (gstring/unescapeEntities
   (clojure.string/replace (trim-newline (trim str)) #"\s+" " ")))


;;;; CSS modifications
(defn toggle-class
  "Toggle CSS class of provided DOM element. A third paramenter as boolean can
  be provided to force removing or adding the class."
  ([dom-element class] (.classList/toggle dom-element class))
  ([dom-element class bool] (.classList/toggle dom-element class bool)))

(defn remove-class
  "Remove a specific class of a DOM element."
  [dom-element class]
  (toggle-class dom-element class false))

(defn add-class
  "Add a specific class to a DOM element."
  [dom-element class]
  (toggle-class dom-element class true))


;;;; Language
(defn language
  "Returns currently selected language."
  []
  (get-in @app-state [:layout :language]))

(defn language!
  "Set new language. Should be a keyword."
  {:deprecated 0.4}
  [lang]
  (update-state-item! :layout :language (fn [] lang)))

(defn language-next!
  "Set new language. Should be a keyword."
  [lang]
  (nom/transact! parser/reconciler `[(layout/lang {:value ~lang})]))


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


;; -----------------------------------------------------------------------------
;; Specs

(s/fdef change-view-next!
        :args (s/cat :view keyword?))

(s/fdef trim-and-normalize
        :args (s/cat :str string?)
        :ret string?
        :fn #(<= (-> % :ret count) (-> % :args :str count)))

(s/fdef language-next!
        :args (s/cat :lang keyword?))

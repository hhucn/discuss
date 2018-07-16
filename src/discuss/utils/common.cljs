(ns discuss.utils.common
  (:require [om.next :as om :refer-macros [defui]]
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

(defn store-to-app-state!
  "Use reconciler to do a transaction on the app-state.

  Usage: (store-to-app-state! 'foo \"bar\")"
  [field value]
  (om/transact! parser/reconciler `[(~field {:value ~value})]))

(s/fdef store-to-app-state!
  :args (s/cat :field symbol? :value any?))

(defn load-from-app-state
  "Load data from application state."
  [field]
  (field @(om/app-state parser/reconciler)))

(s/fdef load-from-app-state
  :args (s/cat :field keyword?))

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
(defn get-nickname
  "Return the user's nickname, with whom she logged in."
  []
  (load-from-app-state :user/nickname))

(defn get-avatar
  "Return the URL of the user's avatar."
  []
  (load-from-app-state :user/avatar))

(defn get-token
  "Return the user's token for discussion system."
  []
  (load-from-app-state :user/token))

(defn get-issues
  "Returns list of dictionaries with all available issues."
  []
  (load-from-app-state :issue/list))

(defn get-issue
  "Return specific issue, matching by id or title."
  [issue]
  (cond
    (number? issue) (first (filter #(= (str->int (:uid %)) issue) (get-issues)))
    (string? issue) (first (filter #(= (:title %) issue) (get-issues)))))

(defn get-items
  "Returns list of items from the discussion."
  []
  (load-from-app-state :discussion/items))

(defn get-bubbles
  "Return message bubbles from DBAS."
  []
  (load-from-app-state :discussion/bubbles))

(defn get-add-premise-text
  "Return text for adding new premise."
  []
  (get-in @app-state [:discussion :add_premise_text]))


;;;; Booleans
(defn logged-in?
  "Return true if user is logged in."
  []
  (load-from-app-state :user/logged-in?))


;;;; State changing
(defn update-state-item!
  "Get the cursor for given key and select a field to apply the function to it."
  {:deprecated 0.4}
  [col key f]
  )

(defn update-state-map!
  "Get the cursor for given key and update it with the new collection of data."
  {:deprecated 0.4}
  [key col]
  )


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


;; Show error messages
(defn error?
  "Return boolean indicating if there are errors or not. Provide a boolean to
  change the app-state."
  ([] (load-from-app-state :layout/error?))
  ([bool] (store-to-app-state! 'layout/error? bool)))

(defn error-msg!
  "Set error message."
  [msg]
  (when (pos? (count msg))
    (error? true))
  (store-to-app-state! 'layout/error msg))

(defn no-error!
  "Macro to remove all error warnings."
  []
  (error? false)
  (error-msg! nil))

(defn get-error
  "Return error message."
  []
  (load-from-app-state :layout/error))


;; Change views
(defn hide-add-form!
  "Hide the form which allows to add new content."
  []
  (store-to-app-state! 'layout/add? false))

(defn show-add-form!
  "Shows a form to enable user-added content."
  []
  (when (logged-in?)
    (store-to-app-state! 'layout/add? true)))

(defn current-view
  "Returns the current selected template, which should be visible in the
  main-content-view."
  []
  (load-from-app-state :layout/view))

(defn change-view-next!
  [view]
  (om/transact! parser/reconciler `[(layout/view {:value ~view})
                                     (layout/add? {:value false})]))

(defn next-view!
  "Set the next view, which should be loaded after the ajax call has finished."
  [view]
  (hide-add-form!)
  (store-to-app-state! 'layout/view-next view))

(defn change-to-next-view!
  "Set next view to current view. Falls back to default if there is no different
  next view."
  []
  (let [current-view (current-view)
        next-view (load-from-app-state :layout/next-template)]
    (if (= current-view next-view)
      (change-view-next! :default)
      (change-view-next! next-view))))

(defn save-current-and-change-view!
  "Saves the current view and changes to the next specified view. Used for the
  'close' button in some views."
  [view]
  (next-view! (current-view))
  (change-view-next! view))


;;;; Last-api
(defn last-api!
  "Keep last-api call. Useful to login and then re-request the url to jump to
  the same position in the discussion, but this time as a logged in user."
  [url]
  (store-to-app-state! 'api/last-call url))

(defn get-last-api
  "Return url of last API call."
  []
  (load-from-app-state :api/last-call))


;;;; Generic Handlers
(defn loading?
  "Return boolean if app is currently loading content. Provide a boolean to
  change the app-state."
  ([] (load-from-app-state :layout/loading?))
  ([bool] (store-to-app-state! 'layout/loading? bool)))

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
  (load-from-app-state :selection/current))

(defn remove-selection!
  "Remove current selection for a 'clean' statement."
  []
  (store-to-app-state! 'selection/current nil))


;;;; Origins
(defn store-origin!
  "Stores an origin into the app-state."
  [origin]
  (store-to-app-state! 'site/origin origin))

(defn get-origin
  "Return the origin of a statement if a statement has a different origin."
  []
  (load-from-app-state :site/origin))

(defn remove-origin!
  "Remove currently stored origin."
  []
  (store-to-app-state! 'site/origin nil))

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
  (load-from-app-state :layout/lang))

(defn language-next!
  "Set new language. Should be a keyword."
  [lang]
  (store-to-app-state! 'layout/lang lang))


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

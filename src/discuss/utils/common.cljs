(ns discuss.utils.common
  (:require [om.next :as om :refer-macros [defui]]
            [clojure.walk :refer [keywordize-keys]]
            [clojure.string :as string :refer [trim trim-newline]]
            [cljs.spec.alpha :as s]
            [goog.string :as gstring :refer [format]]
            [goog.string.format]
            [cognitect.transit :as transit]
            [inflections.core :refer [plural]]
            [discuss.config :as config]
            [discuss.parser :as parser]
            [discuss.utils.logging :as log]))

(defn prefix-name
  "Create unique id for DOM elements."
  [name]
  (str config/project "-" name))

(defn project-version
  "Return the project's current version."
  []
  (format "%s-%s" (string/replace config/version "\"" "") config/build-commit))
(s/fdef project-version
  :ret string?)

(s/def ::fn-and-val (s/tuple symbol? any?))
(s/def ::col-of-fn-and-vals (s/coll-of ::fn-and-val))

(defn build-transactions
  "Takes a list of vectors containing fns and values, which should be transacted with the reconciler.

  Example: (build-transactions [['discussion/items items] ['discussion/bubbles bubbles]])
  => ((discussion/items {:value [...]})
      (discussion/bubbles {:value [...]}))"
  [col-of-fn-and-vals]
  (vec (for [[f value] col-of-fn-and-vals]
         `(~f {:value ~value}))))
(s/fdef build-transactions
  :args (s/cat :col-of-vectors ::col-of-fn-and-vals)
  :ret (s/coll-of list?))

(defn store-multiple-values-to-app-state!
  "Creates one big transaction of multiple mutation functions and the new values,
  which should be assigned to them.

  Example: (store-multiple-values-to-app-state! [['discussion/items items] ['discussion/bubbles bubbles]])"
  [col-of-fn-and-vals]
  (om/transact! parser/reconciler (build-transactions col-of-fn-and-vals)))
(s/fdef store-multiple-values-to-app-state!
  :args (s/cat :col-of-vectors ::col-of-fn-and-vals))

(defn store-to-app-state!
  "Use reconciler to do a transaction on the app-state.

  Example: (store-to-app-state! 'foo \"bar\")"
  [field value]
  (store-multiple-values-to-app-state! [[field value]]))
(s/fdef store-to-app-state!
  :args (s/cat :field symbol? :value any?))

(defn load-from-app-state
  "Load data from application state."
  [field]
  (field @(om/app-state parser/reconciler)))
(s/fdef load-from-app-state
  :args (s/cat :field keyword?))

;; -----------------------------------------------------------------------------
;; CLJS <--> JS
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

;;;;

(defn get-items
  "Returns list of items from the discussion."
  []
  (load-from-app-state :discussion/items))

(defn get-bubbles
  "Return message bubbles from DBAS."
  []
  (load-from-app-state :discussion/bubbles))


;; Set discussion topic
(defn set-slug!
  "Set new discussion slug."
  [slug]
  (store-to-app-state! 'issue/current-slug slug))

(defn get-slug
  "Get current discussion slug."
  []
  (load-from-app-state :issue/current-slug))

;;;; Getter
(defn get-nickname
  "Return the user's nickname, with whom she logged in."
  []
  (load-from-app-state :user/nickname))

(defn get-user-id
  "Return the user's nickname, with whom she logged in."
  []
  (load-from-app-state :user/id))

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

(defn get-current-slug
  "Return current slug of issue."
  []
  (load-from-app-state :issue/current-slug))


;;;; Booleans
(defn logged-in?
  "Return true if user is logged in."
  []
  (load-from-app-state :user/logged-in?))


;;;; References
;; TODO Move to references/lib
(defn get-references
  "Returns a list of references which were received from the discussion system."
  []
  (load-from-app-state :references/all))

(defn get-reference
  "Returns a map matching a specific id. This id must be a number."
  ([id col] (first (filter #(= (str->int id) (:uid %)) col)))
  ([id] (get-reference id (get-references))))


;; Show error messages
(defn error?
  "Return boolean indicating if there are errors or not."
  []
  (seq (load-from-app-state :layout/error)))

(defn error!
  "Set error message."
  ([msg]
   (store-to-app-state! 'layout/error msg))
  ([]
   (error! nil)))

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

(defn change-view!
  "Change view to the provided one."
  [view]
  (store-multiple-values-to-app-state!
   [['layout/view view] ['layout/add? false]]))

(defn next-view!
  "Set the next view, which should be loaded after the ajax call has finished."
  [view]
  (hide-add-form!)
  (store-to-app-state! 'layout/view-next view))

(defn next-view?
  "Check whether a defined next-view exists."
  []
  (not (nil? (load-from-app-state :layout/view-next))))

(defn change-to-next-view!
  "Set next view to current view. Falls back to default if there is no different
  next view."
  []
  (let [current-view (current-view)
        next-view (load-from-app-state :layout/view-next)]
    (if (and (not (nil? next-view!))
             (= current-view next-view))
      (change-view! :default)
      (do (change-view! next-view)
          (next-view! nil)))))

(defn save-current-and-change-view!
  "Saves the current view and changes to the next specified view. Used for the
  'close' button in some views."
  [view]
  (next-view! (current-view))
  (change-view! view))

(defn add-step!
  "Sets the current add-step, e.g. :add/position or :add/statement."
  [kw]
  (store-to-app-state! 'discussion/add-step kw))
(s/fdef add-step!
  :args (s/cat :kw #{:add/position :add/statement}))


;;;; Generic Handlers
(defn process-response
  "Generic success handler, which sets error handling and returns a cljs-compatible response."
  [response]
  (let [res (json->clj response)
        error (:error res)]
    (if (pos? (count error))
      (error! error)
      (do (error! nil)
          res))))


;;;; Selections
(defn get-selection
  "Return the stored selection of the user."
  []
  (load-from-app-state :selection/current))

(defn save-selection!
  "Store current selection to app-state."
  [selection]
  (store-to-app-state! 'selection/current selection))
(s/fdef save-selection!
  :args (s/cat :selection string?))

(defn remove-selection!
  "Remove current selection for a 'clean' statement."
  []
  (save-selection! nil))


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

(defn remove-trailing-slash
  "Remove trailing slash if it exists."
  [s]
  (if (= (last s) \/) (subs s 0 (dec (count s))) s))
(s/fdef remove-trailing-slash
  :args (s/cat :s string?)
  :ret string?)

(defn prepend-slash
  "Prepend slash to string if none found."
  [s]
  (if (string/starts-with? s "/") s (str "/" s)))
(s/fdef prepend-slash
  :args (s/cat :s string?)
  :ret string?)

(defn drop-words
  "Drop n words of string s."
  [n s]
  (->> (string/split s #" ")
       (drop n)
       (string/join " ")))
(s/fdef drop-words
  :args (s/cat :n pos-int? :s string?)
  :ret string?)


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


;;;; DOM Query
(defn one-of-parent-has-class?
  "Traverse all the parents of the node and return true, if there is one parent,
  who has the desired class, id, or whatever.

  Example: (one-of-parent-has-class? \"#discuss-text\" \".modal\")"
  [selector class]
  (pos-int? (.-length (.closest ((js* "$") selector) class))))

(defn inside-overlay?
  "Test if provided node is rendered inside a modal."
  [query-selector]
  (one-of-parent-has-class? query-selector "#discuss-overlay"))


;;;; DOM Modifications
(defn show-overlay
  "Show discuss in an overlay."
  []
  (.modal ((js* "$") "#discuss-overlay")))

(defn hide-overlay
  "Hide the overlay."
  []
  (.modal ((js* "$") "#discuss-overlay") "hide"))


;;;; Language
(defn language
  "Returns currently selected language."
  []
  (load-from-app-state :layout/lang))

(defn language-next!
  "Set new language. Should be a keyword."
  [lang]
  (store-to-app-state! 'layout/lang lang))

(defn language-locale
  "Return String as a locale for DateTime instances etc. Defaults to en-US."
  []
  (let [lang (language)]
    (case lang
      :de "de-DE"
      "en-US")))
(s/fdef language-locale
  :ret string?)


;;;; Other
(defn get-value-by-id
  "Return value of element matching the id."
  [id]
  (let [element (.getElementById js/document (prefix-name id))]
    (when element (.-value element))))

(defn filter-keys-by-namespace
  "Filter a collection of vectors by their namespaces.

  Example: (filter-keys-by-namespace [:foo/bar :bar/foo] \"foo\")
  => (:foo/bar)"
  [col keyword-namespace]
  (filter #(= keyword-namespace (namespace %)) col))

(s/fdef filter-keys-by-namespace
  :args (s/cat :col coll? :namespace any?)
  :ret coll?)


;; -----------------------------------------------------------------------------
;; Configuration Settings

(defn host-dbas!
  "Set host to API of a dbas instance."
  [host]
  (store-to-app-state! 'host/dbas (remove-trailing-slash host)))
(s/fdef host-dbas!
  :args (s/cat :host string?))

(defn host-dbas
  "Return current address to dbas instance."
  []
  (load-from-app-state :host/dbas))

(defn host-dbas-reset!
  "Reset dbas host to defaults, defined in discuss.config."
  []
  (store-to-app-state! 'host/dbas config/host-dbas))

(defn host-dbas-is-up?
  "Return result of the connectivity-check."
  []
  (load-from-app-state :host/dbas-is-up?))

(defn host-eden!
  "Set host to API of a eden instance."
  [host]
  (store-to-app-state! 'host/eden (remove-trailing-slash host)))
(s/fdef host-eden!
  :args (s/cat :host string?))

(defn host-eden
  "Return current address to eden instance."
  []
  (load-from-app-state :host/eden))

(defn host-eden-reset!
  "Reset eden host to defaults, defined in discuss.config."
  []
  (store-to-app-state! 'host/eden config/host-eden))

(defn host-eden-is-up?
  "Return result of the connectivity-check."
  []
  (load-from-app-state :host/eden-is-up?))

;; -----------------------------------------------------------------------------
;; Specs

(s/fdef change-view!
        :args (s/cat :view keyword?))

(s/fdef trim-and-normalize
        :args (s/cat :str string?)
        :ret string?
        :fn #(<= (-> % :ret count) (-> % :args :str count)))

(s/fdef language-next!
        :args (s/cat :lang keyword?))

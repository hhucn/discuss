(ns discuss.utils.views
  (:require [sablono.core :as html :refer-macros [html]]
            [goog.dom :as gdom]
            [goog.string :as gstring]
            [om.core :as om]
            [om.dom :as dom]
            [discuss.utils.common :as lib]
            [discuss.references.lib :as rlib]
            [discuss.translations :refer [translate] :rename {translate t}]))

(defn button [f display-text]
  [:div.btn.btn-primary {:style {:marginRight "0.5em"}
                         :onClick f}
   display-text])

(defn fa-icon
  "Wrapper for font-awesome icons."
  ([class]
   (dom/i #js {:key       (lib/get-unique-key)
               :className (str "fa " class)}))
  ([class f]
   (dom/i #js {:key       (lib/get-unique-key)
               :className (str "pointer fa " class)
               :onClick   f})))

(defn logo
  "If no function is provided, show logo as is. Else bind function to onClick-event and add pointer-class."
  ([] (fa-icon "fa-comments"))
  ([f] (fa-icon "fa-comments" f)))

(defn safe-html
  "Creates DOM element with interpreted HTML."
  [string]
  (dom/span #js {:className               (lib/prefix-name "converted-bubbles")
                 :dangerouslySetInnerHTML #js {:__html string}
                 :key                     (lib/get-unique-key)}))

(defn safe-space
  "Create a safed spacer."
  []
  (safe-html "&nbsp;"))

(defn html->str
  "Unescape HTML entities and return a string."
  [escaped]
  (when (string? escaped)
    (lib/trim-and-normalize (gstring/unescapeEntities escaped))))

(defn display
  "Toggle display view."
  [show]
  (if show
    #js {}
    #js {:display "none"}))

(defn toggle-show [show] (if show false true))

(defn scroll-divs-to-bottom
  "Align divs to bottom. Scrolls down the complete content of each div."
  [class]
  (let [divs (gdom/getElementsByClass (lib/prefix-name class))]
    (doall (map #(set! (.. % -scrollTop) (.. % -scrollHeight)) divs))))

(defn view-header
  "Function to create a header for the views."
  [heading]
  (dom/h4 #js {:className "text-center"
               :style #js {:paddingBottom "0.3em"}} heading))

(defn- remove-selection-then-reference!
  "Remove selection on first click, then the reference if available."
  []
  (let [selection (lib/get-selection)
        sel-ref (rlib/get-selected-reference)]
    (cond
      selection (lib/remove-selection!)
      sel-ref (rlib/remove-selected-reference!))))

(defn show-selection
  "Shows selected text from website if available."
  []
  (let [selection (or (lib/get-selection) (:text (rlib/get-selected-reference)) "")]
    (html
     (if (> (count selection) 1)
       [:div.input-group
        [:span.input-group-addon.input-group-addon-left
         (fa-icon "fa-quote-left")]
        [:textarea.form-control {:style {:backgroundColor "rgb(250,250,250)"}
                                 :title (t :references :disabled/tooltip)
                                 :disabled true
                                 :value selection}]
        [:span.input-group-addon
         (fa-icon "fa-quote-right")]
        [:span.input-group-addon.pointer {:onClick remove-selection-then-reference!}
         (fa-icon "fa-times")]]
       [:div.text-center {:style {:paddingBottom "1em"}}
        (if (= :create/argument (lib/current-view))
          (t :references :has-to-add)
          (t :references :ask-to-add))]))))

(defn remaining-characters
  "Show remaining characters needed to submit a post."
  [statement selected-search-results]
  (if (nil? selected-search-results)
    (let [remaining (- 10 (count statement))]
      (if (pos? remaining)
        (str remaining " " (t :common :chars-remaining))
        (t :discussion :submit)))
    (t :discussion :submit)))

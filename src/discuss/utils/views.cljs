(ns discuss.utils.views
  (:require [sablono.core :as html :refer-macros [html]]
            [goog.dom :as gdom]
            [goog.string :as gstring]
            [om.core :as om]
            [om.dom :as dom]
            [discuss.utils.common :as lib]
            [discuss.references.lib :as rlib]
            [discuss.translations :refer [translate] :rename {translate t}]))

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

(defn commit-component-state
  "Set local state of view, parse the value of the target of val."
  [key val owner]
  (cond
    (= (type val) js/Event) (om/set-state! owner key (.. val -target -value))
    (= (type val) js/String) (om/set-state! owner key val)
    :else (om/set-state! owner key (.. val -target -value))))

(defn display
  "Toggle display view."
  [show]
  (if show
    #js {}
    #js {:display "none"}))

(defn toggle-show [show] (if show false true))

(defn loading-element
  "Show spinning loading icon when app is loading."
  []
  (when (lib/loading?)
    (dom/div #js {:className "loader"}
             (dom/svg #js {:className "circular" :viewBox "25 25 50 50"}
                      (dom/circle #js {:className "path" :cx "50" :cy "50" :r "20" :fill "none" :strokeWidth "5" :strokeMiterlimit "10"})))))

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
        [:input.form-control {:style {:backgroundColor "rgb(250,250,250)"}
                              :value selection
                              :title (t :references :disabled/tooltip)
                              :disabled true}]
        [:span.input-group-addon
         (fa-icon "fa-quote-right")]
        [:span.input-group-addon.pointer {:onClick remove-selection-then-reference!}
         (fa-icon "fa-times")]]
       [:div.text-center {:style {:paddingBottom "1em"}}
        (t :references :ask-to-add)]))))

(defn remaining-characters
  "Show remaining characters needed to submit a post."
  [statement selected-search-results]
  (if (nil? selected-search-results)
    (let [remaining (- 10 (count statement))]
      (if (pos? remaining)
        (str remaining " " (t :common :chars-remaining))
        (t :discussion :submit)))
    (t :discussion :submit)))

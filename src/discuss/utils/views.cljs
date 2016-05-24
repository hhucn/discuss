(ns discuss.utils.views
  (:require [goog.string :as gstring]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(defn fa-icon
  "Wrapper for font-awesome icons."
  ([class]
   (dom/i #js {:className (str "fa " class)}))
  ([class f]
   (dom/i #js {:className (str "pointer fa " class)
               :onClick   f})))

(defn logo
  "If no function is provided, show logo as is. Else bind function to onClick-event and add
   pointer-class."
  ([]
   (fa-icon "fa-comments"))
  ([f]
   (fa-icon "fa-comments" f)))

(defn safe-html
  "Creates DOM element with interpreted HTML."
  [string]
  (dom/span #js {:dangerouslySetInnerHTML #js {:__html string}}))

(defn html->str
  "Unescape HTML entities and return a string."
  [escaped]
  (when (string? escaped)
    (gstring/unescapeEntities escaped)))

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
  (when (discuss.utils.common/loading?)
    (dom/div #js {:className "loader"}
             (dom/svg #js {:className "circular" :viewBox "25 25 50 50"}
                      (dom/circle #js {:className "path" :cx "50" :cy "50" :r "20" :fill "none" :strokeWidth "5" :strokeMiterlimit "10"})))))
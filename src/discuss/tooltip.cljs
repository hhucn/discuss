(ns discuss.tooltip
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [discuss.clipboard :as clipboard]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]
            [discuss.sidebar :as sidebar]))

(defn show
  "Show tooltip by removing a class."
  []
  (let [tooltip (.getElementById js/document (lib/prefix-name "tooltip"))]
    ;(utils/remove-class tooltip "hidden")
    (set! (.. tooltip -style -visibility) "visible")))

(defn hide
  "Hide tooltip by adding a class."
  []
  (let [tooltip (.getElementById js/document (lib/prefix-name "tooltip"))]
    ;(utils/add-class tooltip "hidden")
    (set! (.. tooltip -style -visibility) "hidden")))

(defn x-position
  "Center tooltip at mouse selection."
  [left twidth ewidth]
  (+ left js/window.scrollX (/ (- ewidth twidth) 2)))

(defn y-position
  "Move tooltip a bit above the mouse selection."
  [top theight]
  (let [offset 5]
    (+ (- top theight offset) js/window.scrollY)))

(defn calc-position
  "Create a new tooltip at given selection. Creates a rectangle around the selection,
   which has position-properties and which are useful for positioning of the tooltip."
  [tooltip-width tooltip-height]
  (let [selection (.getSelection js/window)
        range (.getRangeAt selection 0)
        rect (.getBoundingClientRect range)
        top (.-top rect)
        left (.-left rect)
        width (.-width rect)
        positioned-top (y-position top tooltip-height)
        positioned-left (x-position left tooltip-width width)]
    [positioned-top positioned-left]))

(defn move-to-selection
  "Sets CSS position of tooltip and move it to the mouse selection."
  []
  (let [tooltip (.getElementById js/document (lib/prefix-name "tooltip"))
        [top left] (calc-position tooltip.offsetWidth tooltip.offsetHeight)]
    (set! (.. tooltip -style -top) (str top "px"))
    (set! (.. tooltip -style -left) (str left "px"))
    (show)))


;;;; Creating the view
(defn view [data owner]
  (reify om/IRender
    (render [_]
      (dom/div nil
               (vlib/logo)
               (vlib/safe-space) " | " (vlib/safe-space)
               (dom/span #js {:className "pointer"
<<<<<<< Updated upstream:src/discuss/tooltip.cljs
                              :onClick clipboard/add-selection}
=======
                              :onClick   (fn [] (clipboard/add-item!) (sidebar/show) (hide))}
>>>>>>> Stashed changes:src/discuss/components/tooltip.cljs
                         (vlib/fa-icon "fa-bookmark-o")
                         " Save")
               (vlib/safe-space) "  " (vlib/safe-space)
               (dom/span #js {:className "pointer"
                              :onClick   sidebar/show}
                         (vlib/fa-icon "fa-comment")
                         " Discuss")))))
(ns discuss.components.tooltip
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! chan <!]]
            [goog.dom :as gdom]
            [goog.events :as events]
            [discuss.components.clipboard :as clipboard]
            [discuss.translations :refer [translate]]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]
            [discuss.components.sidebar :as sidebar]))

(defn- get-tooltip
  "Return DOM element of tooltip"
  []
  (gdom/getElement (lib/prefix-name "tooltip")))

(defn show
  "Show tooltip by removing a class."
  []
  (let [tooltip (get-tooltip)]
    (lib/add-class tooltip "discuss-tooltip-active")))

(defn hide
  "Hide tooltip by adding a class."
  []
  (let [tooltip (get-tooltip)]
    (lib/remove-class tooltip "discuss-tooltip-active")))

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
  (let [tooltip (get-tooltip)
        [top left] (calc-position tooltip.offsetWidth tooltip.offsetHeight)]
    (set! (.. tooltip -style -top) (str top "px"))
    (set! (.. tooltip -style -left) (str left "px"))
    (show)))


;;;; Include listener for tooltips
(defn- listen
  "Helper function for mouse-click events."
  [el type]
  (let [out (chan)]
    (events/listen el type (fn [e] (put! out e)))
    out))

(defn- save-selected-text
  "Get the users selection and save it."
  []
  (let [selection (str (.getSelection js/window))]
    (if (and (pos? (count selection))
             (not= selection (lib/get-selection)))
      (do (move-to-selection)
          (lib/store-to-app-state! :selection/current))
      (hide))))

(let [clicks (listen (.getElementById js/document "discuss-text") "click")]
  (go (while true
        (<! clicks)
        (save-selected-text))))


;;;; Creating the view
(defn view []
  (reify om/IRender
    (render [_]
      (dom/div nil
               (vlib/logo)
               (vlib/safe-space) " | " (vlib/safe-space)
               (dom/span #js {:className "pointer"
                              :onClick (fn [] (clipboard/add-item!) (sidebar/show) (hide))}
                         (vlib/fa-icon "fa-bookmark-o")
                         (translate :common :save :space))
               (vlib/safe-space) "  " (vlib/safe-space)
               (dom/span #js {:className "pointer"
                              :onClick (fn [] (sidebar/show) (hide))}
                         (vlib/fa-icon "fa-comment")
                         (translate :common :show-discuss :space))))))

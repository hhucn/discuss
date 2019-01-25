(ns discuss.components.tooltip
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [put! chan <!]]
            [goog.dom :as gdom]
            [goog.events :as events]
            [goog.string :refer [format]]
            [goog.string.format]
            [om.next :as om :refer-macros [defui]]
            [sablono.core :as html :refer-macros [html]]
            [discuss.components.clipboard :as clipboard]
            [discuss.translations :refer [translate]]
            [discuss.utils.logging :as log]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]))

(defn- get-tooltip
  "Return DOM element of tooltip"
  []
  (gdom/getElement (lib/prefix-name "tooltip")))

(defn show
  "Show tooltip by removing a class."
  []
  (when-let [tooltip (get-tooltip)]
    (lib/add-class tooltip (lib/prefix-name "tooltip-active"))))

(defn hide
  "Hide tooltip by adding a class."
  []
  (when-let [tooltip (get-tooltip)]
    (lib/remove-class tooltip  (lib/prefix-name "tooltip-active"))))

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
    (log/fine (format "Tooltip position: top %f, left %f" positioned-top positioned-left))
    [positioned-top positioned-left]))

(defn move-to-selection
  "Sets CSS position of tooltip and move it to the mouse selection."
  []
  (when-let [tooltip (get-tooltip)]
    (let [[top left] (calc-position tooltip.offsetWidth tooltip.offsetHeight)]
      (set! (.. tooltip -style -top) (str top "px"))
      (set! (.. tooltip -style -left) (str left "px"))
      (show))))


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
          (lib/save-selection! selection))
      (hide))))

(defn track-user-selection
  "Listen to clicks on the websites' text to store it in the app-state."
  []
  (when-let [discuss-text-dom (gdom/getElement (lib/prefix-name "text"))]
    (let [clicks (listen discuss-text-dom "click")]
      (go (while true
            (<! clicks)
            (save-selected-text))))))

;;;; Creating the view
(defui Tooltip
  Object
  (componentDidMount
   [this]
   (track-user-selection))
  (render [this]
          (html [:div#discuss-tooltip
                 (vlib/logo)
                 (vlib/safe-space) " | " (vlib/safe-space)
                 [:span.pointer {:onClick (fn [] (clipboard/add-item!) #_(sidebar/show) (hide))}
                  (vlib/fa-icon "fa-bookmark-o")
                  (translate :common :save :space)]
                 #_(vlib/safe-space) "  " (vlib/safe-space)
                 #_[:span.pointer {:onClick (fn [] #_(sidebar/show) (hide))}
                  (vlib/fa-icon "fa-comment")
                  (translate :common :show-discuss :space)]])))
(def tooltip (om/factory Tooltip))

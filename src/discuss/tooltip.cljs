(ns discuss.tooltip
  (:require [discuss.lib :as lib]))

(defn show
  "Show tooltip by removing a class."
  []
  (let [tooltip (.getElementById js/document (lib/prefix-name "tooltip"))]
    ;(lib/remove-class tooltip "hidden")
    (set! (.. tooltip -style -visibility) "visible")))

(defn hide
  "Hide tooltip by adding a class."
  []
  (let [tooltip (.getElementById js/document (lib/prefix-name "tooltip"))]
    ;(lib/add-class tooltip "hidden")
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
    (println "tooltip height:" tooltip.offsetHeight)
    (set! (.. tooltip -style -top) (str top "px"))
    (set! (.. tooltip -style -left) (str left "px"))
    (show)))
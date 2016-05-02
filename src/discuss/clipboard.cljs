(ns discuss.clipboard
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [discuss.lib :as lib]))

(def counter (atom 0))

(defn get-stored-selections
  "Return all stored selections."
  []
  (get-in @lib/app-state [:clipboard :selections]))

(defn add-selection
  "Store current selection in clipboard."
  []
  (let [selections (get-stored-selections)
        current (lib/get-selection)
        with-current (distinct (conj selections current))]
    (lib/update-state-item! :clipboard :selections (fn [_] with-current))))


;;;; Drag n Drop stuff
; http://www.w3schools.com/html/html5_draganddrop.asp

(defn update-reference-drop
  "Use text from clipboard item as reference for own statement."
  [_ev]
  (let [clipboard-item (get-in @lib/app-state [:clipboard :current])]
    (lib/remove-class clipboard-item "bs-callout-info")
    (lib/add-class clipboard-item "bs-callout-success")
    (lib/update-state-item! :user :selection (fn [_] (.. clipboard-item -innerText)))))

(defn allow-drop [ev]
  (println "fn: allow-drop")
  (.preventDefault ev))

(defn drag-event [ev]
  (let [target (.. ev -target)]
    (lib/update-state-item! :clipboard :current (fn [_] target))))

(defn drop-event [ev]
  (.preventDefault ev)
  (println "fn: drop-event"))

(defn item-view [data owner]
  (reify om/IRender
    (render [_]
      (dom/div #js {:react-key   (swap! counter inc)
                    :className   "bs-callout bs-callout-info"
                    :draggable   true
                    :onDragStart drag-event}
               data))))


(defn view [data owner]
  (reify om/IRender
    (render [_]
      (dom/div nil
               (dom/h5 nil "Clipboard")
               (om/build-all item-view (get-stored-selections))))))
(ns discuss.clipboard
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [discuss.lib :as lib]))

(defn get-stored-selections
  "Return all stored selections."
  []
  (get-in @lib/app-state [:clipboard :selections]))

(defn add-selection
  "Store current selection in clipboard."
  []
  (let [selections (get-stored-selections)
        current (lib/get-selection)
        with-current (conj selections current)]
    (lib/update-state-item! :clipboard :selections (fn [_] with-current))))


;;;; Drag n Drop stuff
; http://www.w3schools.com/html/html5_draganddrop.asp

(defn allow-drop [ev]
  (println "fn: allow-drop")
  (.preventDefault ev))

(defn drag-event [ev]
  (println "fn: drag-event")
  (.setData ev.dataTransfer "text" (.. ev -target -id)))

(defn drop-event [ev]
  (.preventDefault ev)
  (println "fn: drop-event"))

(defn item-view [data owner]
  (reify om/IRender
    (render [_]
      (dom/div #js {:className "bs-callout bs-callout-info"
                    :draggable true
                    :onDragStart drag-event}
               data))))

(defn view [data owner]
  (reify om/IRender
    (render [_]
      (dom/div nil
               (dom/div #js {:className "panel panel-body"
                             :onDragOver allow-drop
                             :onDrop drop-event}
                        (lib/get-selection))
               (dom/h5 nil "Clipboard")
               (om/build-all item-view (get-stored-selections))))))
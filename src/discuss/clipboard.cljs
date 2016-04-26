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

(defn item-view [data owner]
  (reify om/IRender
    (render [_]
      (dom/div nil
               (dom/div #js {:className "well well-sm"}
                         data)))))

(defn view [data owner]
  (reify om/IRender
    (render [_]
      (dom/div nil
               (dom/h5 nil "Clipboard")
               (om/build-all item-view (get-stored-selections))))))
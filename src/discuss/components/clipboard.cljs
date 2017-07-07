(ns discuss.components.clipboard
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]))

(defn get-stored-selections
  "Return all stored selections."
  []
  (let [selections (get-in @lib/app-state [:clipboard :selections])]
    (or selections [])))

(defn remove-item!
  "Removes clicked selection."
  [title]
  (let [rcol (remove #(= (:title %) title) (get-stored-selections))]
    (lib/update-state-item! :clipboard :selections (fn [] rcol))))

(defn add-item!
  "Store current selection in clipboard."
  ([current]
   (let [selections (get-stored-selections)
         current current
         with-current (distinct (merge selections {:title current}))]
     (lib/update-state-item! :clipboard :selections (fn [_] with-current))))
  ([] (add-item! (lib/get-selection))))


;;;; Drag n Drop stuff
; http://www.w3schools.com/html/html5_draganddrop.asp

(defn update-reference-drop
  "Use text from clipboard item as reference for own statement."
  [_ev]
  (let [clipboard-item (get-in @lib/app-state [:clipboard :current])]
    (lib/update-state-item! :user :selection (fn [_] (.. clipboard-item -innerText)))))

(defn allow-drop [ev]
  (.preventDefault ev))

(defn drag-event [ev]
  (let [target (.. ev -target)]
    (lib/update-state-item! :clipboard :current (fn [_] target))))


;;;; Views
(defn clipboard-item [data]
  (reify
    om/IInitState
    (init-state [_]
      {:selected? false})
    om/IRenderState
    (render-state [_ {:keys [selected?]}]
      (dom/div #js {:className   "bs-callout bs-callout-info"
                    :draggable   true
                    :onDragStart drag-event}
               (dom/div nil (:title data))))))

(defn view []
  (reify om/IRender
    (render [_]
      (when (pos? (count (get-stored-selections)))
        (dom/div #js {:style #js {:paddingTop "3em"}}
                 (dom/h5 nil (t :clipboard :heading))
                 (dom/p nil (t :clipboard :instruction))
                 (apply dom/div nil
                        (map #(om/build clipboard-item (lib/merge-react-key %)) (get-stored-selections))))))))

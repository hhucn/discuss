(ns discuss.components.clipboard
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
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
    #_(lib/remove-class clipboard-item "bs-callout-info")
    #_(lib/add-class clipboard-item "bs-callout-success")
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
               (dom/div nil (:title data))
               #_(dom/button #js {:className "btn btn-sm btn-default"
                                  :onClick   #(discuss.communication.main/ajax-get "api/cat-or-dog")
                                  :title     "Select this reference for your statement"}
                             (vlib/fa-icon "fa-check"))))))

(defn view []
  (reify om/IRender
    (render [_]
      (when (pos? (count (get-stored-selections)))
        (dom/div nil
                 (dom/h5 nil "Clipboard")
                 (dom/p nil "Ziehe diese Referenzen in das Textfeld beim Erzeugen eines neuen Arguments, um die Referenz zu
                         nutzen.")
                 (apply dom/div nil
                        (map #(om/build clipboard-item (lib/merge-react-key %)) (get-stored-selections))))))))
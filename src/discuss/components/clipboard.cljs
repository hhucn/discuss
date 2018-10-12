(ns discuss.components.clipboard
  (:require [om.next :as om :refer-macros [defui]]
            [sablono.core :as html :refer-macros [html]]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.common :as lib]
            [discuss.utils.logging :as log]))

(defn get-stored-selections
  "Return all stored selections."
  []
  (log/info "Deprecated call to get-stored-selections")
  #_(let [selections (get-in @lib/app-state [:clipboard :selections])]
    (or selections [])))

(defn remove-item!
  "Removes clicked selection."
  [title]
  (log/info "Deprecated call to remove-item!")
  #_(let [rcol (remove #(= (:title %) title) (get-stored-selections))]
    (lib/update-state-item! :clipboard :selections (fn [] rcol))))

(defn add-item!
  "Store current selection in clipboard."
  ([current]
   (log/info "Deprecated call to add-item!")
   #_(let [selections (get-stored-selections)
         current current
         with-current (distinct (merge selections {:title current}))]
     #_(lib/update-state-item! :clipboard :selections (fn [_] with-current))))
  ([] (add-item! (lib/get-selection))))


;;;; Drag n Drop stuff
; http://www.w3schools.com/html/html5_draganddrop.asp

(defn update-reference-drop
  "Use text from clipboard item as reference for own statement."
  [_ev]
  (log/info "Deprecated call to update-reference-drop")
  #_(let [clipboard-item (get-in @lib/app-state [:clipboard :current])]
    (lib/update-state-item! :user :selection (fn [_] (.. clipboard-item -innerText)))))

(defn allow-drop [ev]
  (.preventDefault ev))

(defn- drag-event [ev]
  (let [target (.. ev -target)]
    #_(lib/update-state-item! :clipboard :current (fn [_] target))))


;; -----------------------------------------------------------------------------

(defui ClipboardItem
  static om/IQuery
  (query [this] [:title])
  Object
  (render [this]
          (let [{:keys [title]} (om/props this)]
            (html
             [:div {:className "bs-callout bs-callout-info"
                    :draggable true
                    :onDragStart drag-event}
              title]))))
(def clipboard-item-next (om/factory ClipboardItem {:keyfn :title}))

(defui Clipboard
  static om/IQuery
  (query [this]
         [{:clipboard/items (om/get-query ClipboardItem)}])
  Object
  (render [this]
          (let [{:keys [clipboard/items]} (om/props this)]
            (when (pos? (count items))
              (html [:div {:style {:paddingTop "rem"}}
                     [:h5 (t :clipboard :heading)]
                     [:p (t :clipboard :instruction)]
                     (map clipboard-item-next items)])))))
(def clipboard (om/factory Clipboard))

(ns discuss.components.clipboard
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [om.next :as nom :refer-macros [defui]]
            [sablono.core :as html :refer-macros [html]]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.common :as lib]))

(defn get-stored-selections
  "Return all stored selections."
  {:deprecated 0.4}
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

(defn- drag-event [ev]
  (let [target (.. ev -target)]
    (lib/update-state-item! :clipboard :current (fn [_] target))))


;;;; Views
(defn clipboard-item [data]
  {:deprecated 0.4}
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

(defn view
  {:deprecated 0.4}
  []
  (reify om/IRender
    (render [_]
      (when (pos? (count (get-stored-selections)))
        (dom/div #js {:style #js {:paddingTop "3em"}}
                 (dom/h5 nil (t :clipboard :heading))
                 (dom/p nil (t :clipboard :instruction))
                 (apply dom/div nil
                        (map #(om/build clipboard-item (lib/merge-react-key %)) (get-stored-selections))))))))


;; -----------------------------------------------------------------------------

(defui ClipboardItem
  static nom/IQuery
  (query [this] [:title])
  Object
  (render [this]
          (let [{:keys [title]} (nom/props this)]
            (html
             [:div {:className "bs-callout bs-callout-info"
                    :draggable true
                    :onDragStart drag-event}
              title]))))
(def clipboard-item-next (nom/factory ClipboardItem {:keyfn :title}))

(defui Clipboard
  static nom/IQuery
  (query [this] [:clipboard/items])
  Object
  (render [this]
          (let [{:keys [clipboard/items]} (nom/props this)]
            (when (pos? (count items))
              (html [:div {:style {:paddingTop "rem"}}
                     [:h5 (t :clipboard :heading)]
                     [:p (t :clipboard :instruction)]
                     (map clipboard-item-next items)])))))

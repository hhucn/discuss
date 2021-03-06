(ns discuss.components.clipboard
  (:require [om.next :as om :refer-macros [defui]]
            [sablono.core :refer-macros [html]]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]))
(declare Clipboard ClipboardItem)

(defn get-items
  "Return collection of previously stored text-passages."
  []
  (lib/load-from-app-state :clipboard/items))

(defn remove-item!
  "Removes clicked selection."
  [item]
  (let [rcol (remove #(= % item) (get-items))]
    (lib/store-to-app-state! 'clipboard/items rcol)))

(defn add-item!
  "Store current selection in clipboard."
  ([current]
   (lib/store-to-app-state! 'clipboard/items (conj (get-items) current)))
  ([] (add-item! (lib/get-selection))))


;;;; Drag n Drop stuff
; http://www.w3schools.com/html/html5_draganddrop.asp

(defn update-reference-drop
  "Use text from clipboard item as reference for own statement."
  [e]
  (let [clipboard-item (.getData (.. e -dataTransfer) "reference")]
    (lib/save-selection! clipboard-item)))

(defn allow-drop [ev]
  (.preventDefault ev))

;; (defn- drag-event [ev]
;;   (.setData (.. ev -dataTransfer) "reference" (.. ev -target -innerText)))

;; -----------------------------------------------------------------------------

(defui ClipboardItem
  Object
  (render [this]
    (let [item (om/props this)]
      (html
        [:div {:className "bs-callout bs-callout-info"}
         item
         [:div.pull-right
          [:div.btn.btn-default.btn-xs
           {:onClick #(lib/save-selection! item)}
           (t :common :select) " " (vlib/fa-icon "fa-check-square-o")]
          " "
          [:div.btn.btn-default.btn-xs
           {:onClick #(remove-item! item)}
           (t :common :delete) " " (vlib/fa-icon "fa-times")]]]))))
(def clipboard-item (om/factory ClipboardItem {:keyfn identity}))

(defui Clipboard
  static om/IQuery
  (query [this] [:clipboard/items :discuss/clipboard?])
  Object
  (render [this]
    (let [{:keys [clipboard/items discuss/clipboard?]} (om/props this)]
      (when (and (pos? (count items)) clipboard?)
        (html [:div {:style {:paddingTop "rem"}}
               [:h5 (t :clipboard :heading)]
               [:p (t :clipboard :instruction)]
               (map clipboard-item items)])))))
(def clipboard (om/factory Clipboard))

(ns discuss.components.sidebar
  "Controlling the sidebar."
  (:require [om.next :as om :refer-macros [defui]]
            [sablono.core :refer-macros [html]]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]
            [goog.dom :as gdom]))

(def id (lib/prefix-name "sidebar"))

(defn toggle
  "Toggle visibility of sidebar."
  ([]
   (let [sidebar-dom (gdom/getElement id)
         main-wrapper (first (gdom/getElementsByTagName "body"))]
     (lib/toggle-class sidebar-dom "active")
     (lib/toggle-class main-wrapper "active")))
  ([bool]
   (let [sidebar-dom (gdom/getElement id)
         main-wrapper (first (gdom/getElementsByTagName "body"))]
     (lib/toggle-class sidebar-dom "active" bool)
     (lib/toggle-class main-wrapper "active" bool))))

(defn show
  "Show sidebar."
  []
  (lib/remove-class (gdom/getElement (lib/prefix-name "dialog-collapse")) "in")
  (toggle true))

(defn hide
  "Hide sidebar."
  []
  (lib/add-class (gdom/getElement (lib/prefix-name "dialog-collapse")) "in")
  (toggle false))


;;;; Sidebar
(defui Sidebar
  Object
  (render [this]
          (html
           [:div
            [:div.discuss-sidebar
             (vlib/fa-icon "fa-bars" hide)
             ((resolve 'discuss.views/main-view) (om/props this))]])))
(def sidebar (om/factory Sidebar {:keyfn identity}))

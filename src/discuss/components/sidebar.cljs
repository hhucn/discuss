(ns discuss.components.sidebar
  "Controlling the sidebar."
  (:require [om.core :as om]
            [om.dom :as dom]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]
            [discuss.components.clipboard :as clipboard]))

(def id (lib/prefix-name "sidebar"))

(defn toggle
  "Toggle visibility of sidebar."
  ([]
   (let [sidebar-dom (.getElementById js/document id)
         main-wrapper (.getElementById js/document "main-wrapper")]
     (lib/toggle-class sidebar-dom "active")
     (lib/toggle-class main-wrapper "active")))
  ([bool]
   (let [sidebar-dom (.getElementById js/document id)
         main-wrapper (.getElementById js/document "main-wrapper")]
     (lib/toggle-class sidebar-dom "active" bool)
     (lib/toggle-class main-wrapper "active" bool))))

(defn show
  "Show sidebar."
  []
  (toggle true))

(defn hide
  "Hide sidebar."
  []
  (toggle false))


;;;; Sidebar
(defn view [data]
  (reify om/IRender
    (render [_]
      (dom/div nil
               (vlib/fa-icon "fa-bars" toggle)
               (om/build discuss.views/main-view data)
               (clipboard/view)))))
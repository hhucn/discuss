(ns discuss.components.sidebar
  "Controlling the sidebar."
  (:require [om.core :as om]
            [om.dom :as dom]
            [goog.dom :as gdom]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]))

(def id (lib/prefix-name "sidebar"))

(defn toggle
  "Toggle visibility of sidebar."
  ([]
   (let [sidebar-dom (gdom/getElement id)
         main-wrapper (gdom/getElement "main-wrapper")]
     (lib/toggle-class sidebar-dom "active")
     (lib/toggle-class main-wrapper "active")))
  ([bool]
   (let [sidebar-dom (gdom/getElement id)
         main-wrapper (gdom/getElement "main-wrapper")]
     (lib/toggle-class sidebar-dom "active" bool)
     (lib/toggle-class main-wrapper "active" bool))))

(defn show
  "Show sidebar."
  []
  (lib/remove-class (gdom/getElement (lib/prefix-name "dialogue-collapse")) "in")
  (toggle true))

(defn hide
  "Hide sidebar."
  []
  (lib/add-class (gdom/getElement (lib/prefix-name "dialogue-collapse")) "in")
  (toggle false))


;;;; Sidebar
(defn view [data]
  (reify om/IRender
    (render [_]
      (dom/div nil
               (vlib/fa-icon "fa-bars" hide)
               (om/build discuss.views/main-view data)))))
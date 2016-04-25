(ns discuss.sidebar
  "Controlling the sidebar."
  (:require [discuss.lib :as lib]))

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
  "Show sidebar, switch app-state."
  []
  (toggle true))

(defn hide
  "Hide sidebar, switch app-state."
  []
  (toggle false))
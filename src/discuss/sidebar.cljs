(ns discuss.sidebar
  "Controlling the sidebar."
  (:require [discuss.lib :as lib]))

(def id (lib/prefix-name "sidebar"))

(defn show?
  "Return boolean which indicates if the sidebar is visible or not."
  []
  (get-in @lib/app-state [:sidebar :show?]))

(defn toggle!
  "Toggle visibility of sidebar."
  ([]
   (let [sidebar-dom (.getElementById js/document id)
         main-wrapper (.getElementById js/document "main-wrapper")]
     (.classList/toggle sidebar-dom "active")
     (.classList/toggle main-wrapper "active")))
  ([bool]
   (let [sidebar-dom (.getElementById js/document id)
         main-wrapper (.getElementById js/document "main-wrapper")]
     (.classList/toggle sidebar-dom "active" bool)
     (.classList/toggle main-wrapper "active" bool))))

(defn show!
  "Show sidebar, switch app-state."
  []
  (toggle! true))

(defn hide!
  "Hide sidebar, switch app-state."
  []
  (toggle! false))
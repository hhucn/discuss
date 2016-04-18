(ns discuss.sidebar
  "Controlling the sidebar."
  (:require [discuss.lib :as lib]))

(def id (lib/prefix-name "sidebar"))

(defn show?
  "Return boolean which indicates if the sidebar is visible or not."
  []
  (get-in @lib/app-state [:sidebar :show?]))

(defn show!
  "Show sidebar, switch app-state."
  []
  (let [sidebar-dom (.getElementById js/document id)]
    (.classList/toggle sidebar-dom "active" true)))

(defn hide!
  "Hide sidebar, switch app-state."
  []
  (let [sidebar-dom (.getElementById js/document id)]
    (.classList/toggle sidebar-dom "active" false)))

(defn toggle!
  "Toggle visibility of sidebar."
  []
  (let [sidebar-dom (.getElementById js/document id)]
    (.classList/toggle sidebar-dom "active")))
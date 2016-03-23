(ns discuss.sidebar
  "Controlling the sidebar."
  (:require [discuss.lib :as lib]))

(def id (lib/prefix-name "sidebar"))

;(println (.. (.getElementById js/document "mySidenav") -style))
(defn- set-sidebar-width!
  "Get sidebar DOM element and set its width."
  [width]
  (set! (.-width (.-style (.getElementById js/document id))) width))

(defn- get-sidebar-width
  "Look in app-state for sidebar width."
  []
  (get-in @lib/app-state [:sidebar :width]))

(defn show!
  "Show sidebar, switch app-state."
  []
  (set-sidebar-width! (get-sidebar-width))
  (lib/update-state-item! :sidebar :show? (fn [_] true)))

(defn hide!
  "Hide sidebar."
  []
  (set-sidebar-width! "0"))

(defn show?
  "Return boolean which indicates if the sidebar is visible or not."
  []
  (get-in @lib/app-state [:sidebar :show?]))
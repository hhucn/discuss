(ns discuss.sidebar
  "Controlling the sidebar."
  (:require [discuss.lib :as lib]))

(def id (lib/prefix-name "sidebar"))
(def width "200px")

;(println (.. (.getElementById js/document "mySidenav") -style))
(defn- set-sidebar-width!
  "Get sidebar DOM element and set its width."
  [w]
  (set! (.-width (.-style (.getElementById js/document id))) w))

(defn show?
  "Return boolean which indicates if the sidebar is visible or not."
  []
  (get-in @lib/app-state [:sidebar :show?]))

(defn show!
  "Show sidebar, switch app-state."
  []
  (when-not (show?)
    (set-sidebar-width! width)
    (lib/update-state-item! :sidebar :show? (fn [_] true))))

(defn hide!
  "Hide sidebar, switch app-state"
  []
  (when (show?)
    (set-sidebar-width! "39px")
    (lib/update-state-item! :sidebar :show? (fn [_] false))))

(defn toggle!
  "Toggle visibility of sidebar."
  []
  (if (show?)
    (hide!)
    (show!)))
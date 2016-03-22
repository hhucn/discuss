(ns discuss.sidebar
  "Controlling the sidebar."
  (:require [discuss.lib :as lib]))

(def id (lib/prefix-name "sidebar"))

;(println (.. (.getElementById js/document "mySidenav") -style))
(defn- set-sidebar-width! [width]
  (set! (.-width (.-style (.getElementById js/document id))) width))

(defn show
  "Show sidebar."
  []
  (set-sidebar-width! "200px"))

(defn hide
  "Hide sidebar."
  []
  (set-sidebar-width! "0"))
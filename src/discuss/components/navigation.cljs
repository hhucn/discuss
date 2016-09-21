(ns discuss.components.navigation
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [discuss.communication.main :as com]
            [discuss.translations :refer [translate]]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]))

(defn- element
  "Create one element for the navigation."
  [icon [group key] fn]
  (dom/span #js {:className "pointer"
                :onClick   fn}
           (vlib/fa-icon icon) " " (translate group key)))

(def home
  "Show home screen and initialize discussion."
  (element "fa-home" [:nav :home] com/init!))

(def find-arg
  "Open view to find statements inside of the discussion."
  (element "fa-search" [:nav :find] nil))

(def options
  "Entrypoint to show options. Should be something like a onclick handler."
  (element "fa-cog" [:options :heading] (fn []
                                          (lib/next-view! (lib/current-view))
                                          (lib/change-view! :options))))

(defn main
  "Create main navigation with general elements."
  [data owner]
  (reify om/IRender
    (render [_]
      (dom/div #js {:className "text-muted"}
               home
               find-arg
               options))))
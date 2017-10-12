(ns discuss.components.navigation
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [discuss.communication.lib :as comlib]
            [discuss.translations :refer [translate]]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]
            [discuss.communication.auth :as auth]))

(defn- element
  "Create one element for the navigation."
  ([icon [group key] fn left?]
   (dom/span #js {:className "pointer"
                  :onClick   fn
                  :style     (if left? #js {:paddingLeft "1em"} #js {:paddingRight "1em"})}
             (vlib/fa-icon icon)
             " "
             (dom/span #js {:className "hover-underline"}
                       (translate group key))))
  ([icon [group key] fn] (element icon [group key] fn false)))


;;;; Elements
(defn- home
  "Show home screen and initialize discussion."
  []
  (element "fa-home" [:nav :home] comlib/init!))

(defn- find-arg
  "Open view to find statements inside of the discussion."
  []
  (element "fa-search" [:nav :find] #(lib/save-current-and-change-view! :find)))

(defn- login
  "Login switch."
  []
  (element "fa-sign-in" [:common :login] #(lib/save-current-and-change-view! :login) true))

(defn- logout
  "Login switch."
  []
  (element "fa-sign-out" [:common :logout] auth/logout true))

(defn- options
  "Entrypoint to show options. Should be something like a onclick handler."
  []
  (element "fa-cog" [:options :heading] #(lib/save-current-and-change-view! :options)))


;;;; Main component
(defn main
  "Create main navigation with general elements."
  []
  (reify om/IRender
    (render [_]
      (dom/div #js {:className "text-muted"}
               (dom/div #js {:className "col col-md-6 col-sm-6 col-xs-6"}
                        (home)
                        (find-arg)
                        (options))
               (dom/div #js {:className "col col-md-6 col-sm-6 col-xs-6 text-right"}
                        (if (lib/logged-in?) (logout) (login)))))))

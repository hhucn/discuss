(ns discuss.components.navigation
  (:require [om.dom :as dom :include-macros true]
            [om.next :as om :refer-macros [defui]]
            [sablono.core :refer-macros [html]]
            [discuss.communication.lib :as comlib]
            [discuss.translations :refer [translate]]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]
            [discuss.communication.auth :as auth]))
(declare Nav)

(defn- element
  "Create one element for the navigation."
  ([icon [group key] f left?]
   (dom/span #js {:key (lib/get-unique-key)
                  :className "pointer"
                  :onClick f
                  :style (if left? #js {:paddingLeft "1em"} #js {:paddingRight "1em"})}
             (vlib/fa-icon icon)
             " "
             (dom/span #js {:className "hover-underline"}
                       (translate group key))))
  ([icon [group key] f] (element icon [group key] f false)))


;;;; Elements
(defn- home
  "Show home screen and initialize discussion."
  []
  (element "fa-home" [:nav :home] #(comlib/init!)))

;; (defn- find-arg)
;;   "Open view to find statements inside of the discussion."
;;   []
;;   (element "fa-search" [:nav :find] #(lib/save-current-and-change-view! :find))

(defn- eden-overview
  "Open view to find statements inside of the discussion."
  []
  (when (and (lib/host-eden) (lib/logged-in?))
    (element "fa-puzzle-piece" [:nav :eden] #(lib/save-current-and-change-view! :eden/overview))))

(defn- create-argument
  "Create an argument to a text reference."
  []
  (element "fa-paper-plane-o" [:tooltip :discuss/start] #(lib/save-current-and-change-view! :create/argument)))

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


;; -----------------------------------------------------------------------------
;; om.next

(defui Nav
  static om/IQuery
  (query [this]
    [:user/logged-in? :layout/lang :host/eden :host/dbas :discuss/experimental?])
  Object
  (render [this]
    (let [{:keys [discuss/experimental?]} (om/props this)]
      (html [:div.text-muted.discuss-nav
             [:div.col.col-md-9.col-sm-9.col-xs-9
              (home) (create-argument)
              (when experimental? (eden-overview))
              (options)]
             [:div.col.col-md-3.col-sm-3.col-xs-3.text-right
              (if (lib/logged-in?) (logout) (login))]]))))
(def nav (om/factory Nav))

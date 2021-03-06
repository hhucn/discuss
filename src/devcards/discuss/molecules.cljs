(ns devcards.discuss.molecules
  (:require [devcards.core :as dc :refer-macros [defcard defcard-om-next]]
            [sablono.core :as html :refer-macros [html]]
            [discuss.parser :as parser]
            [discuss.views :as views]
            [discuss.communication.lib :as comlib]
            [om.next :as om]))

(defcard sample-requests
  "Do some sample requests with `ajax-get`."
  (html [:div
         [:button.btn.btn-sm.btn-primary {:onClick #(comlib/ajax-get "/town-has-to-cut-spending/attitude/36" nil comlib/process-and-set-items-and-bubbles)}
          "/town-has-to-cut-spending/attitude/36"]
         [:br]
         [:button.btn.btn-sm.btn-primary {:onClick #(comlib/ajax-get "/cat-or-dog" nil comlib/process-and-set-items-and-bubbles)}
          "/cat-or-dog"]
         [:br]
         [:button.btn.btn-sm.btn-primary {:onClick #(comlib/ajax-get "/town-has-to-cut-spending/reaction/47/undercut/48?history=/attitude/38-/justify/38/agree" nil comlib/process-and-set-items-and-bubbles)}
          "/town-has-to-cut-spending/reaction/47/undercut/48?history=/attitude/38-/justify/38/agree"]]))

(defcard-om-next discussion-elements
  views/DiscussionElements
  parser/reconciler)

(defcard-om-next view-dispatcher
  views/ViewDispatcher
  parser/reconciler)

(defcard-om-next view-dispatcher-default
  views/ViewDispatcher
  (om/reconciler {:state (merge @(om/app-state parser/reconciler)
                                {:layout/view :default})
                  :parser (om/parser {:read parser/read})}))

(defcard-om-next view-dispatcher-login
  views/ViewDispatcher
  (om/reconciler {:state {:layout/view :login}
                  :parser (om/parser {:read parser/read})}))

(defcard-om-next view-dispatcher-options
  views/ViewDispatcher
  (om/reconciler {:state {:layout/view :options}
                  :parser (om/parser {:read parser/read})}))

(defcard-om-next main-view
  views/MainView
  parser/reconciler)

(ns devcards.discuss.molecules
  (:require [devcards.core :as dc :refer-macros [defcard defcard-om-next]]
            [sablono.core :as html :refer-macros [html]]
            [discuss.parser :as parser]
            [discuss.views :as views]
            [discuss.communication.lib :as comlib]))

(defcard sample-requests
  "Do some sample requests with `ajax-get`."
  (html [:div
         [:button.btn.btn-sm.btn-primary {:onClick #(comlib/ajax-get "/town-has-to-cut-spending/attitude/36" nil comlib/process-discussion-step)}
          "/town-has-to-cut-spending/attitude/36"]
         [:br]
         [:button.btn.btn-sm.btn-primary {:onClick #(comlib/ajax-get "/cat-or-dog" nil comlib/process-discussion-step)}
          "/cat-or-dog"]
         [:br]
         [:button.btn.btn-sm.btn-primary {:onClick #(comlib/ajax-get "/town-has-to-cut-spending/reaction/47/undercut/48?history=/attitude/38-/justify/38/agree" nil comlib/process-discussion-step)}
          "/town-has-to-cut-spending/reaction/47/undercut/48?history=/attitude/38-/justify/38/agree"]]))

(defcard-om-next discussion-elements
  views/DiscussionElements
  parser/reconciler
  {:inspect-data true :history true})

(defcard-om-next view-dispatcher
  views/ViewDispatcher
  parser/reconciler)

(defcard-om-next main-content-view
  views/MainContentView
  parser/reconciler)

(defcard-om-next main-view
  views/MainView
  parser/reconciler)

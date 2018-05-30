(ns discuss.cards
  (:require [devcards.core :as dc :refer-macros [defcard dom-node]]
            [sablono.core :as html :refer-macros [html]]
            [discuss.parser :as parser]
            [discuss.views :as views]
            [discuss.communication.auth :as auth]
            [discuss.components.bubbles :as bubbles]
            [discuss.components.options :as options]
            [discuss.components.navigation :as nav]
            [discuss.components.clipboard :as clipboard]
            [om.next :as om]))

(enable-console-print!)

(defcard shortcuts
  (html [:div.btn.btn-primary {:onClick #(auth/login "Christian" "iamgroot")} "Login"]))

(defcard discussion-atoms
  "## Discussion Atoms")

(dc/defcard-om-next bubbles-view
  bubbles/BubblesView
  parser/reconciler)

(dc/defcard-om-next items-view
  views/ItemsView
  parser/reconciler)

(dc/defcard-om-next add-element-view
  views/AddElement
  parser/reconciler)

(defcard close-button-next
  views/close-button-next)

(defcard control-elements-next
  views/control-elements-next)

(dc/defcard-om-next error-view
  views/ErrorAlert
  (om/reconciler {:state {:layout/error "I am an error message"}
                  :parser (om/parser {:read parser/read})}))

(defcard login
  "## Login")

(dc/defcard-om-next login-form
  views/LoginForm
  parser/reconciler)

(defcard options
  "## Options")

(dc/defcard-om-next option-view
  options/Options
  parser/reconciler)

(defcard nav
  "## Nav")

(dc/defcard-om-next nav-view
  nav/Nav
  parser/reconciler)

(defcard clipboard
  "## Clipboard")

(dc/defcard-om-next clipboard-view
  clipboard/Clipboard
  parser/reconciler)

(defcard molecules
  "## View Molecules")

(dc/defcard-om-next discussion-elements
  views/DiscussionElements
  parser/reconciler)

(dc/defcard-om-next view-dispatcher
  views/ViewDispatcher
  parser/reconciler)

(dc/defcard-om-next main-content-view
  views/MainContentView
  parser/reconciler)

(dc/defcard-om-next main-view
  views/MainView
  parser/reconciler)


;; -----------------------------------------------------------------------------
;; Start devcards

(defn main []
  ;; conditionally start the app based on whether the #main-app-area
  ;; node is on the page
  (when-let [node (.getElementById js/document "main-app-area")]
    (.render js/ReactDOM (html [:div "This is working"]) node)))
(main)

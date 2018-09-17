(ns devcards.discuss.atoms
  (:require [devcards.core :as dc :refer-macros [defcard defcard-om-next]]
            [discuss.parser :as parser]
            [discuss.views :as views]
            [discuss.components.bubbles :as bubbles]
            [discuss.components.options :as options]
            [discuss.components.navigation :as nav]
            [discuss.components.clipboard :as clipboard]
            [om.next :as om]
            [discuss.components.items :as items]
            [discuss.views.alerts :as valerts]
            [discuss.views.add :as vadd]))

(defcard close-button-next
  views/close-button-next)

(defcard control-elements-next
  views/control-elements-next)

(defcard-om-next error-view
  valerts/ErrorAlert
  (om/reconciler {:state {:layout/error "I am an error message"}
                  :parser (om/parser {:read parser/read})}))

(defcard-om-next login-form
  views/LoginForm
  parser/reconciler)

(defcard-om-next add-element-view
  vadd/AddElement
  parser/reconciler)

(defcard-om-next option-view
  options/Options
  parser/reconciler)

(defcard-om-next nav-view
  nav/Nav
  parser/reconciler)

(defcard-om-next clipboard-view
  clipboard/Clipboard
  parser/reconciler)

(defcard-om-next bubbles-view
  bubbles/BubblesView
  parser/reconciler)

(defcard-om-next items-view
  items/Items
  parser/reconciler)

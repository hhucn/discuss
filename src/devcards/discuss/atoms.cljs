(ns devcards.discuss.atoms
  (:require [devcards.core :as dc :refer-macros [defcard defcard-om-next]]
            [discuss.parser :as parser]
            [discuss.views :as views]
            [discuss.components.bubbles :as bubbles]
            [discuss.components.options :as options]
            [discuss.components.navigation :as nav]
            [discuss.components.clipboard :as clipboard]
            [discuss.components.items :as items]))

(defcard close-button-next
  views/close-button-next)

(defcard control-elements-next
  views/control-elements-next)

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

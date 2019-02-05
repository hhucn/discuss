(ns devcards.discuss.views
  (:require [devcards.core :as dc :refer-macros [defcard defcard-om-next]]
            [sablono.core :as html :refer-macros [html]]
            [discuss.parser :as parser]
            [discuss.views :as views]))

(defcard buttons
  (html [:div.btn.btn-secondary {:onClick #(.modal ((js* "$") "#discuss-overlay"))}
         "Toggle Modal"]))

(defcard-om-next discuss
  views/Discuss
  parser/reconciler)

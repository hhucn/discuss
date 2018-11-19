(ns devcards.discuss.components.references
  (:require [devcards.core :as dc :refer-macros [defcard defcard-om-next]]
            [discuss.parser :as parser]
            [discuss.views :as views]
            [sablono.core :as html :refer-macros [html]]
            [discuss.components.tooltip :as tooltip]
            [discuss.references.integration :as rint]))

(defcard buttons
  (html [:div
         [:div.btn.btn-primary {:onClick rint/request-references} "Request References"]]))

(defcard sample-passage
  "<p id='discuss-text'>Currently, the city council discusses to close the University Park, because of its high running expenses of about $100.000 per year. But apparently there is an anonymous investor ensuring to pay the running costs for at least the next five years. Thanks to this anonymous person, the city does not loose a beautiful park, but this again fires up the discussion about possible savings for the future. </p>")

(defcard-om-next tooltip
  tooltip/Tooltip
  parser/reconciler)

(defcard-om-next main-view
  views/MainView
  parser/reconciler)

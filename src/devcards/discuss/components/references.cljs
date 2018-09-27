(ns devcards.discuss.components.references
  (:require [devcards.core :as dc :refer-macros [defcard defcard-om-next]]
            [cljs.test :refer [testing is are]]
            [discuss.parser :as parser]
            [discuss.views :as views]
            [om.next :as om]
            [discuss.components.search.statements :as search]
            [discuss.components.tooltip :as tooltip]))

(defcard sample-passage
  "<p id='discuss-text'>
     Currently, the city council discusses to close the University
     Park, because of its high running expenses of about $100.000 per
     year. But apparently there is an anonymous investor ensuring to
     pay the running costs for at least the next five years. Thanks
     to this anonymous person, the city does not loose a beautiful
     park, but this again fires up the discussion about possible
     savings for the future.
   </p>")

(defcard-om-next tooltip
  tooltip/Tooltip
  parser/reconciler)

(defcard-om-next main-view
  views/MainView
  parser/reconciler)

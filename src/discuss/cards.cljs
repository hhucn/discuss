(ns discuss.cards
  (:require [om.core :as om :include-macros true]
            [sablono.core :as sab :include-macros true]
            [discuss.utils.common :as lib]
            [discuss.views :as views])
  (:require-macros [devcards.core :as dc :refer [defcard defcard-om deftest]]))

(enable-console-print!)

(defcard-om discuss
  views/main-view
  lib/app-state)

;; -----------------------------------------------------------------------------
;; Start devcards

(defn main []
  ;; conditionally start the app based on whether the #main-app-area
  ;; node is on the page
  (when-let [node (.getElementById js/document "main-app-area")]
    (.render js/ReactDOM (sab/html [:div "This is working"]) node)))

(main)

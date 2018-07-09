(ns devcards.discuss.core
  (:require [devcards.core :as dc :refer-macros [defcard defcard-om-next]]
            [sablono.core :as html :refer-macros [html]]
            [discuss.communication.auth :as auth]
            [devcards.discuss.atoms]
            [devcards.discuss.molecules]
            [devcards.discuss.components.search]))

(enable-console-print!)

(defcard shortcuts
  (html [:div.btn.btn-primary {:onClick #(auth/login "Christian" "iamgroot")} "Login"]))


;; -----------------------------------------------------------------------------
;; Start devcards

(defn main []
  ;; conditionally start the app based on whether the #main-app-area
  ;; node is on the page
  (when-let [node (.getElementById js/document "main-app-area")]
    (.render js/ReactDOM (html [:div "This is working"]) node)))
(main)

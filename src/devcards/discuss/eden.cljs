(ns devcards.discuss.eden
  (:require [devcards.core :as dc :refer-macros [defcard defcard-om-next om-next-root]]
            [sablono.core :as html :refer-macros [html]]
            [discuss.eden.views :as eviews]
            [discuss.components.options :as options]
            [discuss.parser :as parser]
            [discuss.views :as views]
            [discuss.communication.auth :as auth]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]
            [discuss.utils.faker :as faker]))

(defcard-om-next connection-browser
  options/ConnectionBrowser
  parser/reconciler)

(defcard buttons
  (html [:div
         (vlib/button #(auth/login "Christian" "iamgroot") "Login as Christian")
         (vlib/button #(lib/save-selection! (faker/random-sentence)) "Set random reference")
         (vlib/button #(lib/last-api! "/town-has-to-cut-spending/justify/37/agree?history=/attitude/37") "Set :api/last-url")]))

(defcard-om-next new-eden-argument
  eviews/EDENArgumentForm
  parser/reconciler)

(defcard-om-next new-eden-statement
  eviews/StatementForm
  parser/reconciler)

(defcard-om-next overview-menu
  eviews/OverviewMenu
  parser/reconciler)

#_(defcard add-new-eden-argument
  (eviews/eden-argument-form {:click-fn (fn [] (println "clicked"))}))

(defcard-om-next main-view
  views/MainView
  parser/reconciler)

(ns discuss.core
  "Entrypoint to this application. Loads all requirements, and bootstraps the application."
  (:require [om.next :as om]
            [goog.dom :as gdom]
            [react :as react]
            [create-react-class :as create-react-class]
            [discuss.communication.lib :as comlib]
            [discuss.config :as config]
            [discuss.parser :as parser]
            [discuss.references.integration :as rint]
            [discuss.utils.common :as lib]
            [discuss.utils.logging :as log]
            [discuss.views :as views]
            [discuss.communication.connectivity :as comcon]))

;; this is to support om with the latest version of React
(set! (.-createClass react) create-react-class)

;; Initialization
(defn ^:export main []
  (enable-console-print!)
  (log/debug "Loaded %s %s" config/project (lib/project-version))
  (om/add-root! parser/reconciler views/Discuss (gdom/getElement (lib/prefix-name "main")))
  (comcon/check-connectivity-of-hosts)
  (rint/request-references)
  (comlib/init!))
(main)

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )

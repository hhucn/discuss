(ns discuss.core
  "Entrypoint to this application. Loads all requirements, and bootstraps the application."
  (:require [om.next :as om]
            [goog.dom :as gdom]
            [react :as react]
            [create-react-class :as create-react-class]
            [discuss.config :as config]
            [discuss.parser :as parser]
            [discuss.utils.common :as lib]
            [discuss.utils.logging :as log]
            [discuss.views :as views]
            [discuss.communication.main :as com]
            [discuss.communication.auth :as auth]))

;; this is to support om with the latest version of React
(set! (.-createClass react) create-react-class)

;; Initialization
(defn ^:export main []
  (enable-console-print!)
  (log/debug "Loaded %s %s" config/project (lib/project-version))
  (com/load-remote-configuration!)
  (om/add-root! parser/reconciler views/Discuss (gdom/getElement (lib/prefix-name "main")))
  (auth/load-credentials-from-localstorage!))
(main)

(defn on-js-reload [])

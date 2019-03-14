(ns devcards.discuss.components.options
  (:require [devcards.core :as dc :refer-macros [defcard-om-next]]
            [discuss.parser :as parser]
            [discuss.components.options :as options]))

(defcard-om-next host-dbas-config
  options/HostDBAS
  parser/reconciler)

(defcard-om-next host-eden-config
  options/HostEDEN
  parser/reconciler)

(defcard-om-next connection-browser
  options/ConnectionBrowser
  parser/reconciler)

(defcard-om-next connectivity-status
  options/ConnectivityStatus
  parser/reconciler)

(defcard-om-next options
  options/Options
  parser/reconciler)

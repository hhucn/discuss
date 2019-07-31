(ns devcards.discuss.components.options
  (:require [devcards.core :as dc :refer-macros [defcard-om-next]]
            [discuss.parser :as parser]
            [discuss.components.options :as options]
            [om.next :as om]))

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

(defcard-om-next options-non-experimental-features
  options/Options
  (om/reconciler {:state (merge @(om/app-state parser/reconciler)
                                {:discuss/experimental? false})
                  :parser (om/parser {:read parser/read :mutate parser/mutate})}))

(defcard-om-next options-with-experimental-features
  options/Options
  (om/reconciler {:state (merge @(om/app-state parser/reconciler)
                                {:discuss/experimental? true})
                  :parser (om/parser {:read parser/read :mutate parser/mutate})}))

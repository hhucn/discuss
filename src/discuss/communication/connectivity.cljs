(ns discuss.communication.connectivity
  (:require [ajax.core :refer [GET POST]]
            [discuss.utils.common :as lib]
            [discuss.config :as config]
            [discuss.utils.logging :as log]))

(defn- set-available! [host-in-app-state]
  (lib/store-to-app-state! (symbol host-in-app-state) true))

(defn- set-offline! [server host-in-app-state]
  (log/warning "Could not connect to %s, host is unreachable (%s)." server host-in-app-state)
  (lib/store-to-app-state! (symbol host-in-app-state) false))

(defn- check-connectivity [server host-in-app-state]
  (GET server
       {:handler       #(set-available! host-in-app-state)
        :error-handler #(set-offline! server host-in-app-state)}))

(defn check-connectivity-of-hosts
  "Query the configured remote hosts."
  []
  (when (seq (lib/host-dbas))
    (check-connectivity (str (lib/host-dbas) (:test config/api))
                        :host/dbas-is-up?))
  (when (seq (lib/host-eden))
    (check-connectivity (lib/host-eden)
                        :host/eden-is-up?)))

(ns devcards.discuss.utils
  (:require [discuss.communication.auth :as auth]
            [sablono.core :as html :refer-macros [html]]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]))

(def shortcuts
  (html [:div
         (vlib/button #(auth/login "Christian" "iamgroot") "Login as Christian")
         (vlib/button #(lib/last-api! "/town-has-to-cut-spending/justify/37/agree?history=/attitude/37") "Set :api/last-url")]))

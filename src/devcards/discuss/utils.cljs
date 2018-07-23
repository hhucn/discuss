(ns devcards.discuss.utils
  (:require [discuss.communication.auth :as auth]
            [sablono.core :as html :refer-macros [html]]
            [discuss.utils.common :as lib]))

(def shortcuts
  (html [:div
         [:div.btn.btn-primary {:onClick #(auth/login "Christian" "iamgroot")} "Login"]
         [:div.btn.btn-primary {:onClick #(lib/last-api! "/town-has-to-cut-spending/justify/37/agree?history=/attitude/37")} "Set :api/last-url"]]))

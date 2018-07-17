(ns devcards.discuss.utils
  (:require [discuss.communication.auth :as auth]
            [sablono.core :as html :refer-macros [html]]))

(def shortcuts
  (html [:div.btn.btn-primary {:onClick #(auth/login "Christian" "iamgroot")} "Login"]))

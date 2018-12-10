(ns devcards.discuss.utils
  (:require [discuss.communication.auth :as auth]
            [sablono.core :as html :refer-macros [html]]
            [discuss.utils.common :as lib]))

(defn button [f display-text]
  [:div.btn.btn-primary {:style {:marginRight "0.5em"}
                         :onClick f}
   display-text])

(def shortcuts
  (html [:div
         (button #(auth/login "Christian" "iamgroot") "Login as Christian")
         (button #(lib/last-api! "/town-has-to-cut-spending/justify/37/agree?history=/attitude/37") "Set :api/last-url")]))

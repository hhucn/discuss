(ns discuss.debug
  "Show information for debugging."
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [discuss.lib :as lib]))

(defn debug-view [data _owner]
  (reify om/IRender
    (render [_]
      (dom/div nil
               (dom/h4 nil "Last API call")
               (dom/pre nil (get-in data [:debug :last-api]))
               (dom/button #js {:className "btn btn-default"
                                :onClick #(discuss.communication/ajax-get (get-in data [:debug :last-api]))}
                           "Resend API Call")

               (dom/h4 nil "Last response")
               (dom/pre nil
                        (apply dom/ul nil
                               (map (fn [[k v]] (dom/li nil (str k "\t\t" v))) (get-in data [:debug :response]))))

               (dom/h4 nil "Token")
               (dom/pre nil (get-in data [:user :token]))
               ))))

(defn update
  "Update displayed debug information."
  [key val]
  (lib/update-state-item! :debug key (fn [_] val)))
(ns discuss.debug
  "Show information for debugging."
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.pprint :as pp]
            [discuss.lib :as lib]))

(defn print-bubbles [bubbles]
  (map #(let [bubble (nth bubbles %)]
         (str
           (cond
             (:is_user bubble) "user: "
             (:is_system bubble) "info: "
             (:is_status bubble) "status: "
             :else ""
             )
           (:message bubble)
           "\n"))
       (range (count bubbles))))

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

               (dom/h4 nil "Bubbles")
               (dom/pre nil
                        (let [bubbles (get-in data [:debug :response :discussion :bubbles])]
                          (print-bubbles bubbles)))

               (dom/h4 nil "Token")
               (dom/pre nil (get-in data [:user :token]))
               ))))

(defn update-debug
  "Update displayed debug information."
  [key val]
  (lib/update-state-item! :debug key (fn [_] val)))
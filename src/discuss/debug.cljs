(ns discuss.debug
  "Show information for debugging."
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [discuss.lib :as lib]))

(defn print-bubbles [bubbles]
  (map #(let [bubble (nth bubbles %)]
         (str
           (cond
             (:is_user bubble) ":user "
             (:is_system bubble) ":info "
             (:is_status bubble) ":status "
             :else ""
             )
           (:message bubble)
           "\n"))
       (range (count bubbles))))

(defn debug-view [data _owner]
  (reify om/IRender
    (render [_]
      (dom/div nil
               (dom/h4 #js {:className "pointer text-muted"
                            :data-toggle "collapse"
                            :data-target "#collapse-debug"
                            :aria-expanded "true"
                            :aria-controls "collapse-debug"}
                       "Debug")
               (dom/div #js {:className "collapse well"
                             :id "collapse-debug"}
                        (dom/h6 nil "Last API call")
                        (dom/pre nil (get-in data [:debug :last-api]))
                        (dom/button #js {:className "btn btn-default"
                                         :onClick   #(discuss.communication/ajax-get (get-in data [:debug :last-api]))}
                                    "Resend API Call")

                        (dom/h6 nil "Token")
                        (dom/pre nil (get-in data [:user :token]))

                        (dom/h6 #js {:className     "pointer"
                                     :data-toggle   "collapse"
                                     :data-target   "#debug-response"
                                     :aria-expanded false
                                     :aria-controls "debug-response"}
                                "Last response")
                        (dom/pre #js {:id        "debug-response"
                                      :className "collapse"}
                                 (apply dom/ul nil
                                        (map (fn [[k v]] (dom/li nil (str k "\t\t" v))) (get-in data [:debug :response]))))

                        (dom/h6 #js {:className     "pointer"
                                     :data-toggle   "collapse"
                                     :data-target   "#debug-bubbles"
                                     :aria-expanded false
                                     :aria-controls "debug-bubbles"}
                                "Bubbles")
                        (dom/pre #js {:id        "debug-bubbles"
                                      :className "collapse"}
                                 (let [bubbles (get-in data [:debug :response :discussion :bubbles])]
                                   (print-bubbles bubbles))))))))

(defn update-debug
  "Update displayed debug information."
  [key val]
  (lib/update-state-item! :debug key (fn [_] val)))
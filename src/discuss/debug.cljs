(ns discuss.debug
  "Show information for debugging."
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlibs]))

(defn print-bubbles [bubbles]
  (map #(let [bubble (nth bubbles %)]
         (str
           (cond
             (:is_user bubble) ":user "
             (:is_system bubble) ":info "
             (:is_status bubble) ":status "
             :else "")
           (:message bubble)
           "\n"))
       (range (count bubbles))))

(defn control-buttons [data]
  (dom/div nil
           (dom/button #js {:className "btn btn-info"
                            :onClick   discuss.core/main}
                       (dom/i #js {:className "fa fa-fort-awesome"}))
           " "
           (dom/button #js {:className "btn btn-default"
                            :onClick   #(discuss.communication.main/ajax-get "api/elektroautos")}
                       (vlibs/fa-icon "fa-car"))
           " "
           (dom/button #js {:className "btn btn-default"
                            :onClick   #(discuss.communication.main/ajax-get "api/cat-or-dog")}
                       (vlibs/fa-icon "fa-paw"))
           " "
           (dom/button #js {:className "btn btn-default"
                            :onClick   #(discuss.communication.main/ajax-get "api/town-has-to-cut-spending")}
                       (vlibs/fa-icon "fa-building"))
           " "
           (dom/button #js {:className "btn btn-default"
                            :onClick   #(discuss.components.sidebar/toggle)}
                       (vlibs/fa-icon "fa-bars"))
           " "
           (dom/button #js {:className "btn btn-default"
                            :onClick   #(discuss.references.integration/process-references (lib/get-references))}
                       (vlibs/fa-icon "fa-file-text") " Request references")
           " "
           (dom/button #js {:className "btn btn-default"
                            :onClick   discuss.communication.auth/one-click-login}
                       "One-Click Login")))

(defn debug-view [data _owner]
  (reify om/IRender
    (render [_]
      (dom/div nil
               (dom/h4 #js {:className     "pointer text-muted"
                            :data-toggle   "collapse"
                            :data-target   "#collapse-debug"
                            :aria-expanded "true"
                            :aria-controls "collapse-debug"}
                       "Debug")
               (dom/div #js {:className "collapse well"
                             :id        "collapse-debug"}
                        (dom/h6 nil "API Calls")
                        (dom/pre nil (get-in data [:common :last-api]))
                        #_(dom/div #js {:className "padding-bottom"}
                                 (dom/div #js {:className "form-group"}
                                          (dom/input #js {:id          (lib/prefix-name "debug-api-call")
                                                          :className   "form-control"
                                                          :placeholder (:last-api data)}))
                                 (dom/button #js {:className "btn btn-default"
                                                  :onClick   #(when-not (empty? (lib/get-value-by-id "debug-api-call")) (discuss.communication.main/ajax-get (lib/get-value-by-id "debug-api-call")))}
                                             "Do the magic"))
                        (dom/h6 nil "Control")

                        (control-buttons data)

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
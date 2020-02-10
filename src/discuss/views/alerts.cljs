(ns discuss.views.alerts
  (:require [om.next :as om :refer-macros [defui]]
            [sablono.core :refer-macros [html]]
            [discuss.utils.views :as vlib]
            [discuss.utils.common :as lib]))
(declare ErrorAlert)

(defui ErrorAlert
  static om/IQuery
  (query [this] [:layout/error])
  Object
  (render [this]
    (let [{:keys [layout/error]} (om/props this)]
      (html
        [:div {:id (lib/prefix-name "error-alert")}
         (when (seq error)
           [:div.alert.alert-info {:role "alert"}
            [:button.close
             [:span {:onClick #(lib/error! nil)
                     :aria-hidden "true"}
              (vlib/safe-html "&times;")]]
            error])]))))
(def error-alert (om/factory ErrorAlert))

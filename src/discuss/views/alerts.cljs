(ns discuss.views.alerts
  (:require [om.next :as om :refer-macros [defui]]
            [sablono.core :as html :refer-macros [html]]
            [discuss.utils.views :as vlib]))

(defui ErrorAlert
  Object
  (render [this]
          (let [{:keys [layout/error]} (om/props this)]
            (when (seq error)
              (html
               [:div.alert.alert-info.alert-dismissable {:role "alert"}
                [:button.close {:data-dismiss "alert"
                                :aria-label "Close"}
                 [:span {:aria-hidden "true"}
                  (vlib/safe-html "&times;")]]
                error])))))
(def error-alert (om/factory ErrorAlert))

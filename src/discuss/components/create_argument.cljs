(ns discuss.components.create-argument
  (:require [om.next :as om :refer-macros [defui]]
            [sablono.core :as html :refer-macros [html]]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]
            [discuss.views.alerts :as valerts]
            [discuss.views.add :as vadd]))

(defui CreateArgumentWithReference
  Object
  (render [this]
          (html [:div
                 (vlib/view-header "Erzeuge ein Argument zu einer Textstelle")
                 (valerts/error-alert (om/props this))

                 (if (lib/logged-in?)
                   [:p.text-center
                    "Du kannst hier Position zu einer Textstelle beziehen.
                    Fülle dazu die folgenden Felder aus!"]
                   [:div
                    [:p.text-center
                     "Du kannst hier Position zu einer Textstelle beziehen. Aber
                     vorher musst du dich einlogggen."]
                    (vlib/button #(lib/change-view-next! :login) "Login")])

                 (lib/show-add-form!)

                 ])))
(def create-argument-with-reference (om/factory CreateArgumentWithReference))

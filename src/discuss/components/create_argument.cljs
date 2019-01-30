(ns discuss.components.create-argument
  (:require [om.next :as om :refer-macros [defui]]
            [sablono.core :as html :refer-macros [html]]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]))

(defui CreateArgumentWithReference
  static om/IQuery
  (query [this]
         [:layout/lang])
  Object
  (render [this]
          (lib/show-add-form!)
          (lib/add-step! :add/position)
          (html [:div
                 (vlib/view-header (t :create/argument :header))
                 [:p.text-center
                  [:span (t :create/argument :lead)]
                  (if (lib/logged-in?)
                    [:span (t :create/argument :logged-in)]
                    [:span " " (t :create/argument :not-logged-in :space)])]
                 (when-not (lib/logged-in?)
                   [:div.text-center
                    (vlib/button (fn []
                                   (lib/next-view! (lib/current-view))
                                   (lib/change-view! :login)) (t :common :login))])])))
(def create-argument-with-reference (om/factory CreateArgumentWithReference))

(ns discuss.eden.views
  (:require [om.next :as om :refer-macros [defui]]
            [sablono.core :as html :refer-macros [html]]
            [discuss.components.clipboard :as clipboard]
            [discuss.eden.ajax :as eajax]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.views :as vlib]
            [discuss.views.alerts :as valerts]
            [discuss.components.search.statements :as search]))

(defui OverviewMenu
  Object
  (render [this]
          (html [:div
                 (vlib/view-header (t :eden :overview))
                 (valerts/error-alert (om/props this))
                 ])))
(def overview-menu (om/factory OverviewMenu))

(defui EDENArgumentForm
  "Form to add a new position and a reason to the discussion."
  static om/IQuery
  (query [this] `[:selection/current :search/selected :layout/lang])
  Object
  (render [this]
          (let [{current-selection :selection/current
                 selected-search :search/selected} (om/props this)
                position (or (:position (om/get-state this)) "")
                reason (or (:reason (om/get-state this)) "")
                smaller-input (first (sort-by count [position reason]))]
            (html [:div.panel.panel-default
                   {:onDragOver clipboard/allow-drop
                    :onDrop clipboard/update-reference-drop}
                   [:div.panel-body
                    [:h5.text-center "Construct a new argument"]
                    (valerts/error-alert (om/props this))

                    [:p.text-center "Here you have the possibility to create a new argument and spread it over the Internet."]
                    [:div.input-group
                     [:span.input-group-addon.input-group-addon-left "In my opinion..."]
                     [:input.form-control {:onChange #(om/update-state! this assoc :position (.. % -target -value))
                                           :value (or position "")
                                           :placeholder (t :discussion :add-position-placeholder)}]]

                    [:div.input-group
                     [:span.input-group-addon.input-group-addon-left "... " (t :common :because)]
                     [:input.form-control {:style {:backgroundColor (when-not (nil? selected-search) "rgb(250,250,250)")}
                                           :onChange #(do (om/update-state! this assoc :reason (.. % -target -value))
                                                          (search/search reason))
                                           :value (or (:text selected-search) reason "")
                                           :placeholder (t :discussion :add-reason-placeholder)}]
                     (when-not (nil? selected-search)
                       [:span.input-group-addon.pointer {:onClick search/remove-selected-search-result!}
                        (vlib/fa-icon "fa-times")])]

                    (vlib/show-selection)
                    [:button.btn.btn-default
                     {:onClick #(eajax/post-eden-argument
                                 {:premise position
                                  :conclusion reason
                                  :reference current-selection
                                  :search/selected selected-search})
                      :disabled (when (nil? selected-search) (> 10 (count smaller-input)))}
                     (vlib/remaining-characters smaller-input selected-search)]]])))
  (componentDidMount [this] (search/remove-all-search-related-results-and-selections)))
(def eden-argument-form (om/factory EDENArgumentForm))

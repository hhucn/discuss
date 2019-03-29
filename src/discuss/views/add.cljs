(ns discuss.views.add
  (:require [om.next :as om :refer-macros [defui]]
            [sablono.core :as html :refer-macros [html]]
            [discuss.components.clipboard :as clipboard]
            [discuss.communication.main :as com]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.views :as vlib]
            [discuss.views.alerts :as valerts]
            [discuss.components.search.statements :as search]
            [discuss.utils.common :as lib]))

(defui StatementForm
  "Form to add a new statement to the discussion."
  static om/IQuery
  (query [this]
         [:selection/current :search/selected :layout/error :discussion/bubbles])
  Object
  (render [this]
          (let [{current-selection :selection/current
                 bubbles :discussion/bubbles
                 selected-search :search/selected} (om/props this)
                statement (or (:statement (om/get-state this)) "")]
            (html
             [:div.panel.panel-default
              {:onDragOver clipboard/allow-drop
               :onDrop clipboard/update-reference-drop}
              [:div.panel-body
               [:h5.text-center (:text (last bubbles))]
               (valerts/error-alert (om/props this))

               [:form {:onSubmit #(do (.preventDefault %)
                                      (com/post-statement {:statement statement
                                                           :reference current-selection
                                                           :search/selected selected-search}))}
                [:div.input-group
                 [:span.input-group-addon.input-group-addon-left "... " (t :common :because)]
                 [:input.form-control {:style {:backgroundColor (when-not (nil? selected-search) "rgb(250,250,250)")}
                                       :onChange #(do (om/update-state! this assoc :statement (.. % -target -value))
                                                      (search/search statement))
                                       :value (or (:text selected-search) statement "")
                                       :placeholder (t :discussion :add-reason-placeholder)
                                       :disabled (not (nil? selected-search))}]
                 (when-not (nil? selected-search)
                   [:span.input-group-addon.pointer {:onClick search/remove-selected-search-result!}
                    (vlib/fa-icon "fa-times")])]

                (vlib/show-selection)
                [:button.btn.btn-default
                 {:type :submit
                  :disabled (when (nil? selected-search) (> 10 (count statement)))}
                 (vlib/remaining-characters statement selected-search)]]]])))
  (componentDidMount [this] (search/remove-all-search-related-results-and-selections)))
(def statement-form (om/factory StatementForm))

(defn- disabled?
  "Check if in :create/argument or not. If in this view, the use is forced to add
  a reference to her position."
  [selected-search current-selection smaller-input]
  (if-not (= :create/argument (lib/current-view))
    (when (nil? selected-search) (> 10 (count smaller-input)))
    (when (nil? selected-search)
      (or (not (pos? (count current-selection)))
          (> 10 (count smaller-input))))))

(defui PositionForm
  "Form to add a new position and a reason to the discussion."
  static om/IQuery
  (query [this] [:selection/current :search/selected :layout/error])
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
                    [:h5.text-center (t :discussion :add-position-heading) "..."]
                    (valerts/error-alert (om/props this))

                    [:form {:onSubmit #(do (.preventDefault %)
                                           (com/post-position {:position position
                                                               :reason reason
                                                               :reference current-selection
                                                               :search/selected selected-search}))}
                     [:div.input-group
                      [:span.input-group-addon.input-group-addon-left "... " (t :common :that)]
                      [:input.form-control {:onChange #(om/update-state! this assoc :position (.. % -target -value))
                                            :value (or position "")
                                            :placeholder (t :discussion :add-position-placeholder)}]]

                     [:div.input-group
                      [:span.input-group-addon.input-group-addon-left "... " (t :common :because)]
                      [:input.form-control {:style {:backgroundColor (when-not (nil? selected-search) "rgb(250,250,250)")}
                                            :onChange #(do (om/update-state! this assoc :reason (.. % -target -value))
                                                           (search/search reason))
                                            :value (or (:text selected-search) reason "")
                                            :placeholder (t :discussion :add-reason-placeholder)
                                            :disabled (not (nil? selected-search))}]
                      (when-not (nil? selected-search)
                        [:span.input-group-addon.pointer {:onClick search/remove-selected-search-result!}
                         (vlib/fa-icon "fa-times")])]

                     (vlib/show-selection)
                     [:button.btn.btn-default
                      {:type :submit
                       :disabled (disabled? selected-search current-selection smaller-input)}
                      (vlib/remaining-characters smaller-input selected-search)]]]])))
  (componentDidMount [this] (search/remove-all-search-related-results-and-selections)))
(def position-form (om/factory PositionForm))

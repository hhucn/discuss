(ns discuss.views.add
  (:require [om.dom :as dom :include-macros true]
            [om.next :as om :refer-macros [defui]]
            [sablono.core :as html :refer-macros [html]]
            [discuss.components.clipboard :as clipboard]
            [discuss.communication.main :as com]
            [discuss.references.lib :as rlib]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]
            [discuss.views.alerts :as valerts]
            [discuss.components.bubbles :as bubbles]
            [discuss.components.search.statements :as search]))

(defn- remaining-characters
  "Show remaining characters needed to submit a post."
  [statement selected-search-results]
  (if (nil? selected-search-results)
    (let [remaining (- 10 (count statement))]
      (if (pos? remaining)
        (str remaining " " (t :common :chars-remaining))
        (t :discussion :submit)))
    (t :discussion :submit)))

(defn- remove-selection-then-reference!
  "Remove selection on first click, then the reference if available."
  []
  (let [selection (lib/get-selection)
        sel-ref (rlib/get-selected-reference)]
    (cond
      selection (lib/remove-selection!)
      sel-ref (rlib/remove-selected-reference!))))

(defn- show-selection
  "Shows selected text from website if available."
  []
  (let [selection (or (lib/get-selection) (:text (rlib/get-selected-reference)) "")]
    (html
     (if (> (count selection) 1)
       [:div.input-group
        [:span.input-group-addon.input-group-addon-left
         (vlib/fa-icon "fa-quote-left")]
        [:input.form-control {:style {:backgroundColor "rgb(250,250,250)"}
                              :value selection
                              :title (t :references :disabled/tooltip)
                              :disabled true}]
        [:span.input-group-addon
         (vlib/fa-icon "fa-quote-right")]
        [:span.input-group-addon.pointer {:onClick remove-selection-then-reference!}
         (vlib/fa-icon "fa-times")]]
       [:div.text-center {:style {:paddingBottom "1em"}}
        (t :references :ask-to-add)]))))

(defn- input-group
  "Construct input group with placeholder in the left side and input on the right
  side. Stores user-input in the calling object."
  [this field input-group-text form-placeholder with-search? selected-search-result]
  (let [field-value (or (get (om/get-state this) field) "")]
    [:div.input-group {:key (lib/get-unique-key)}
     [:span.input-group-addon.input-group-addon-left
      (str "... " input-group-text)]
     (if (nil? selected-search-result)
       [:input.form-control
        {:onChange (fn [e]
                     (let [form-value (.. e -target -value)]
                       (when with-search?
                         (search/search form-value))
                       (om/update-state! this assoc field form-value)))
         :value (or field-value "")
         :placeholder form-placeholder}]
       [[:input.form-control
         {:style {:backgroundColor "rgb(250,250,250)"}
          :value (or (:text selected-search-result) "")
          :disabled true}]
        [:span.input-group-addon.pointer {:onClick search/remove-selected-search-result!}
         (vlib/fa-icon "fa-times")]])]))

(defui StatementForm
  "Form to add a new statement to the discussion."
  static om/IQuery
  (query [this]
         `[:selection/current :search/selected
           {:discussion/bubbles ~(om/get-query bubbles/BubblesView)}])
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
               (input-group this :statement (t :common :because) (t :discussion :add-reason-placeholder) true selected-search)
               #_(show-selection)
               [:button.btn.btn-default
                {:onClick #(com/post-statement {:statement statement
                                                :reference current-selection
                                                :search/selected selected-search})
                 :disabled (when (nil? selected-search) (> 10 (count statement)))}
                (remaining-characters statement selected-search)]]])))
  (componentDidMount [this] (search/remove-all-search-related-results-and-selections)))
(def statement-form (om/factory StatementForm))

(defui PositionForm
  "Form to add a new position and a reason to the discussion."
  static om/IQuery
  (query [this] [:selection/current :search/selected])
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
                    (input-group this :position (t :common :that) (t :discussion :add-position-placeholder) false nil)
                    (input-group this :reason (t :common :because) (t :discussion :add-reason-placeholder) true selected-search)
                    #_(show-selection)
                    [:button.btn.btn-default
                     {:onClick #(com/post-position {:position position
                                                    :reason reason
                                                    :reference current-selection
                                                    :search/selected selected-search})
                      :disabled (when (nil? selected-search) (> 10 (count smaller-input)))}
                     (remaining-characters smaller-input selected-search)]]])))
  (componentDidMount [this] (search/remove-all-search-related-results-and-selections)))
(def position-form (om/factory PositionForm))

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
            [discuss.views.alerts :as valerts]))

(defn- remaining-characters
  "Show remaining characters needed to submit a post."
  [statement]
  (let [remaining (- 10 (count statement))]
    (if (pos? remaining)
      (str remaining " " (t :common :chars-remaining))
      (t :discussion :submit))))

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
  (let [selection (or (lib/get-selection) (:text (discuss.references.lib/get-selected-reference)) "")]
    (if (> (count selection) 1)
      (dom/div #js {:className "input-group"}
               (dom/span #js {:className "input-group-addon input-group-addon-left"}
                         (vlib/fa-icon "fa-quote-left"))
               (dom/input #js {:className "form-control"
                               :style #js {:backgroundColor "rgb(250,250,250)"}
                               :value selection
                               :title (t :references :disabled/tooltip)
                               :disabled true})
               (dom/span #js {:className "input-group-addon"}
                         (vlib/fa-icon "fa-quote-right"))
               (dom/span #js {:className "input-group-addon pointer"
                              :onClick   remove-selection-then-reference!}
                         (vlib/fa-icon "fa-times")))
      (dom/div #js {:className "text-center"
                    :style #js {:paddingBottom "1em"}}
               (t :references :ask-to-add)))))

(defn- input-group
  "Construct input group with placeholder in the left side and input on the right
  side. Stores user-input in the calling object."
  [this field input-group-text form-placeholder]
  (let [field-value (or (get (om/get-state this) field) "")]
    [:div.input-group
     [:span.input-group-addon.input-group-addon-left
      (str "... " input-group-text)]
     [:input.form-control
      {:onChange (fn [e]
                   (let [form-value (.. e -target -value)]
                     #_(search/search form-value)  ;; TODO implement search
                     (om/update-state! this assoc field form-value)))
       :value field-value
       :placeholder form-placeholder}]]))

(defn- form-field-statement [this heading current-selection]
  (let [statement (or (:statement (om/get-state this)) "")]
    [:div
     [:h5.text-center heading]
     (valerts/error-alert (om/props this))
     (input-group this :statement (t :common :because) (t :discussion :add-reason-placeholder))
     (show-selection)
     [:button.btn.btn-default
      {:onClick #(com/post-statement statement current-selection)
       :disabled (> 10 (count statement))}
      (remaining-characters statement)]]))

(defn- form-field-position [this current-selection]
  (let [position (or (:position (om/get-state this)) "")
        reason (or (:reason (om/get-state this)) "")
        smaller-input (first (sort-by count [position reason]))]
    [:div
     [:h5.text-center (t :discussion :add-position-heading) "..."]
     (valerts/error-alert (om/props this))
     (input-group this :position (t :common :that) (t :discussion :add-position-placeholder))
     (input-group this :reason (t :common :because) (t :discussion :add-reason-placeholder))
     (show-selection)
     [:button.btn.btn-default
      {:onClick #(com/post-position position reason current-selection)
       :disabled (> 10 (count smaller-input))}
      (remaining-characters smaller-input)]]))

(defui AddElement
  "Form to add new elements to the discussion."
  static om/IQuery
  (query [this] [:selection/current :discussion/add-step :discussion/bubbles])
  Object
  (render [this]
          (let [{current-selection :selection/current
                 add-step :discussion/add-step bubbles :discussion/bubbles} (om/props this)]
            (html [:div.panel.panel-default
                   {:onDragOver clipboard/allow-drop
                    :onDrop clipboard/update-reference-drop}
                   [:div.panel-body
                    (if (= :add/position add-step)
                      (form-field-position this current-selection)
                      (form-field-statement this
                                            (:text (last bubbles))
                                            current-selection))]]))))
(def add-element (om/factory AddElement))

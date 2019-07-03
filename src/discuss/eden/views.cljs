(ns discuss.eden.views
  (:require [om.next :as om :refer-macros [defui]]
            [sablono.core :refer-macros [html]]
            [discuss.communication.lib :as comlib]
            [discuss.components.clipboard :as clipboard]
            [discuss.components.search.statements :as search]
            [discuss.eden.ajax :as eajax]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]
            [discuss.views.alerts :as valerts]))

(defn- format-unix-time
  "Format unix timestamp to a readable string."
  ([seconds locale]
   (let [js-date (js/Date. (* 1000 seconds))]
     (str
      (.toLocaleDateString js-date locale)
      ", "
      (.toLocaleTimeString js-date locale))))
  ([seconds]
   (format-unix-time seconds (lib/language-locale))))

(defn- maybe-print-timestamp
  "If there is a timestamp provided, pack it into a string."
  [seconds]
  (when (seq seconds) (str ", " (format-unix-time seconds))))

(defn- references-to-list [references]
  (let [wrapped-refs (map #(str "\"" (:text %) "\"") references)]
    (if (= 1 (count wrapped-refs))
      (first wrapped-refs)
      (into [:ul]
            (for [e wrapped-refs]
              [:li e])))))

(defui Argument
  Object
  (render [this]
          (let [{:keys [link premise conclusion]} (om/props this)
                argument-author (get-in link [:author :name])
                premise-author (get-in premise [:content :author :name])
                conclusion-author (get-in conclusion [:content :author :name])
                conclusion-references (:references conclusion)]
            (html [:div.bs-callout.bs-callout-info
                   [:p (vlib/safe-html (str (get-in premise [:content :text])
                                            " " (t :common :because) " "
                                            (get-in conclusion [:content :text])))]
                   [:div.row
                    [:div.col-sm-4
                     [:p [:span.btn.btn-sm.btn-primary
                          "Coming soon"
                          #_{:onClick #(comlib/jump-to-argument (get-in premise [:content :identifier :entity-id]))}]]]

                    [:div.col-sm-8.small
                     [:dl.dl-horizontal
                      [:dt "Argument"]
                      [:dd "By " argument-author (maybe-print-timestamp (:created link))]
                      [:dt "Premise"]
                      [:dd "By " premise-author (maybe-print-timestamp (:created (:content premise)))]
                      [:dt "Conclusion"]
                      [:dd "By " conclusion-author (maybe-print-timestamp (:created (:content conclusion)))]

                      (when (seq conclusion-references)
                        [:span
                         [:dt "References"]
                         [:dd (references-to-list conclusion-references)]])]]]]))))
(def argument-view (om/factory Argument {:keyfn lib/get-unique-key}))

(defui ShowArguments
  static om/IQuery
  (query [this] `[:eden/arguments])
  Object
  (componentDidMount
   [this]
   (when (lib/host-eden-is-up?)
     (eajax/search-arguments-by-author (lib/get-nickname))))
  (render [this]
          (let [{:keys [eden/arguments]} (om/props this)
                supportive-arguments (filter #(= :support (:type (:link %))) arguments)]
            (html [:div
                   (if (zero? (count supportive-arguments))
                     [:p.text-center.text-info (t :eden :arguments/not-found)]
                     [:div
                      [:p.text-center (t :eden :overview/lead)]
                      (map argument-view supportive-arguments)])]))))
(def show-arguments (om/factory ShowArguments))

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
            (html [:div  ;; .panel.panel-default
                   {:onDragOver clipboard/allow-drop
                    :onDrop clipboard/update-reference-drop}
                   [:div  ;; .panel-body
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

(defui StatementForm
  "Form to add a new statement with an optional reference to the eden network."
  static om/IQuery
  (query [this] `[:selection/current :search/selected :layout/lang])
  Object
  (render [this]
    (let [{current-selection :selection/current
           selected-search :search/selected} (om/props this)
          position (or (:position (om/get-state this)) "")
          reason (or (:reason (om/get-state this)) "")
          smaller-input (first (sort-by count [position reason]))]
      (html [:div  ;; .panel.panel-default
             {:onDragOver clipboard/allow-drop
              :onDrop clipboard/update-reference-drop}
             [:div  ;; .panel-body
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

(defui OverviewMenu
  Object
  (render [this]
          (html [:div
                 (vlib/view-header (t :eden :overview))
                 (valerts/error-alert (om/props this))
                 (show-arguments (om/props this))])))
(def overview-menu (om/factory OverviewMenu))

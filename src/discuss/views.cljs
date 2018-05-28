(ns discuss.views
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [om.next :as nom :refer-macros [defui]]
            [clojure.string :as string]
            [goog.dom :as gdom]
            [sablono.core :as html :refer-macros [html]]
            [discuss.components.bubbles :as bubbles]
            [discuss.components.clipboard :as clipboard]
            [discuss.components.find :as find]
            [discuss.components.navigation :as nav]
            [discuss.components.options :as options]
            [discuss.components.search.statements :as search]
            [discuss.communication.auth :as auth]
            [discuss.communication.main :as com]
            [discuss.communication.lib :as comlib]
            [discuss.history :as history]
            [discuss.references.lib :as rlib]
            [discuss.references.main :as ref]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.bootstrap :as bs]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]
            [discuss.parser :as parser]))

;;;; Auxiliary
(defn- remaining-characters
  "Show remaining characters needed to submit a post."
  [statement]
  (let [remaining (- 10 (count statement))]
    (if (and (pos? remaining) (empty? (lib/get-origin)))
      (str remaining " " (t :common :chars-remaining))
      (t :discussion :submit))))


;;;; Elements
(defn error-view
  "Display error message if there are errors."
  [data owner]
  (reify
    om/IRender
    (render [_]
      (om/observe owner (lib/get-cursor :layout))
      (when (lib/error?)
        (dom/div #js {:className "alert alert-info alert-dismissable"
                      :role      "alert"}
                 (dom/button #js {:className    "close"
                                  :data-dismiss "alert"
                                  :aria-label   "Close"}
                             (dom/span #js {:aria-hidden "true"}
                                       (vlib/safe-html "&times;")))
                 (lib/get-error))))))

(defn control-elements []
  (reify
    om/IRender
    (render [_]
      (dom/div nil
               (dom/hr nil)
               (dom/div #js {:className "row"}
                        (dom/div #js {:className "col-md-offset-4 col-sm-offset-4 col-xs-offset-4 col-md-4 col-sm-4 col-xs-4 text-center"}
                                 (dom/button #js {:className "btn btn-default btn-sm"
                                                  :onClick   history/back!
                                                  :disabled  (> 2 (count (re-seq #"/" (lib/get-last-api))))}
                                             (vlib/fa-icon "fa-step-backward")
                                             (t :common :back :space)))
                        (dom/div #js {:className "col-md-4 col-sm-4 col-xs-4 text-right"}
                                 (bs/button-default-sm comlib/init! (vlib/fa-icon "fa-refresh") (t :discussion :restart :space))))))))

(defn close-button []
  (reify om/IRender
    (render [_]
      (dom/div nil
               (dom/hr nil)
               (dom/div #js {:className "text-center"}
                        (bs/button-default-sm lib/change-to-next-view! (vlib/fa-icon "fa-times") (t :common :close :space)))))))

(defn avatar-view
  "Get the user's avatar and add login + logout functions to it."
  []
  (dom/div #js {:className "discuss-avatar-main-wrapper pull-right text-muted text-center"}
           (when (lib/logged-in?)
             (dom/div nil
                      (dom/img #js {:src       (lib/get-avatar)
                                    :className "discuss-avatar-main img-responsive img-circle"})
                      (dom/span nil (str (t :common :hello) " " (lib/get-nickname) "!"))))))

(defn login-form [_ owner]
  (reify
    om/IInitState
    (init-state [_]
      {:nickname ""
       :password ""})
    om/IRenderState
    (render-state [_ {:keys [nickname password]}]
      (dom/div nil
               (om/build error-view {})
               (vlib/view-header (t :common :login))
               (dom/p #js {:className "text-center"} (t :login :hhu-ldap))
               (dom/div #js {:className "input-group"}
                        (dom/span #js {:className "input-group-addon"}
                                  (vlib/fa-icon "fa-user fa-fw"))
                        (dom/input #js {:className   "form-control"
                                        :onChange    #(vlib/commit-component-state :nickname % owner)
                                        :value       nickname
                                        :placeholder (t :login :nickname)}))
               (dom/div #js {:className "input-group"}
                        (dom/span #js {:className "input-group-addon"}
                                  (vlib/fa-icon "fa-key fa-fw"))
                        (dom/input #js {:className   "form-control"
                                        :onChange    #(vlib/commit-component-state :password % owner)
                                        :value       password
                                        :type        "password"
                                        :placeholder (t :login :password)}))
               (dom/button #js {:className "btn btn-default"
                                :onClick   #(auth/login nickname password)
                                :disabled  (not (and (pos? (count nickname))
                                                     (pos? (count password))))}
                           (t :common :login))))))

;; Views
(defn item-view [item _owner]
  (reify
    om/IDidUpdate
    (did-update [_ _ _]
      (let [radio (gdom/getElement (lib/prefix-name (str "item-list-radio-" (:id item))))]
        (set! (.-checked radio) false)))                    ;; Uncheck radio button on reload
    om/IRender
    (render [_]
      (dom/div #js {:className "radio"}
               (dom/label #js {}
                          (dom/input #js {:id        (lib/prefix-name (str "item-list-radio-" (:id item)))
                                          :type      "radio"
                                          :className (lib/prefix-name "dialog-items")
                                          :name      (lib/prefix-name "dialog-items-group")
                                          :onClick   #(com/item-click (:id item) (:url item))
                                          :value     (:url item)})
                          " "
                          (vlib/safe-html (string/join (str " <i>" (t :common :and) "</i> ") (map :title (:premises item))))))))) ; get all premises of item and add an "and" between them

(defn items-view
  "Show discussion items."
  [data]
  (reify om/IRender
    (render [_]
      (dom/div nil
               (apply dom/ul #js {:id (lib/prefix-name "items-main")}
                      (map #(om/build item-view % (lib/unique-react-key-dict)) (get-in data [:items :elements])))))))


(defn init-view
  "Show button if discussion has not been initialized yet."
  []
  (dom/div #js {:key       (lib/get-unique-key)
                :className "text-center"}
           (bs/button-primary com/init-with-references! (t :common :start-discussion))))

(defn discussion-elements [data]
  (if-not (empty? (:discussion @lib/app-state))
    (dom/div #js {:key (lib/get-unique-key)}
             (om/build bubbles/view data)
             (om/build items-view data))
    (init-view)))

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

(defn- origin-set [origin]
  (html [:input.form-control {:disabled true
                              :value (:content origin)}]))

(defn add-element
  "Show form to add a new statement."
  [_ owner]
  (reify
    om/IInitState (init-state [_] {:statement ""})
    om/IRenderState
    (render-state [_ {:keys [statement]}]
      (om/observe owner (lib/get-cursor :user))
      (om/observe owner (lib/get-cursor :references))
      (om/observe owner (lib/get-cursor :origin))
      (let [origin (lib/get-origin)]
        (html [:div.panel.panel-default
               {:onDragOver clipboard/allow-drop
                :onDrop clipboard/update-reference-drop}
               [:div.panel-body
                [:h4.text-center (t :discussion :add-argument)]
                [:h5.text-center (vlib/safe-html (lib/get-add-premise-text))]
                (om/build error-view {})
                [:div.input-group
                 [:span.input-group-addon.input-group-addon-left
                  (str "... " (t :common :because))]
                 (if-not (empty? origin)
                   (origin-set origin)
                   [:input.form-control
                    {:onChange (fn [e] (search/search (.. e -target -value))
                                 (vlib/commit-component-state :statement e owner))
                     :value statement}])]
                (show-selection)
                [:button.btn.btn-default
                 {:onClick #(com/dispatch-add-action
                             statement (lib/get-selection) origin)
                  :disabled (and (> 10 (count statement)) (empty? origin))}
                 (remaining-characters statement)]]])))))

(defn view-dispatcher
  "Dispatch current template in main view by the app state."
  [data]
  (reify om/IRender
    (render [_]
      (let [view (lib/current-view)]
        (dom/div #js {:className "panel panel-default"}
                 (dom/div #js {:className "panel-body"}
                          (case view
                            :login (om/build login-form {})
                            :options (om/build options/view data)
                            :reference-usages (om/build ref/usages-view data)
                            :reference-create-with-ref (om/build ref/create-with-reference-view data)
                            :find (om/build find/view data)
                            (discussion-elements data))
                          (if (contains? #{:login :options :find :reference-usages} view)
                            (om/build close-button data)
                            (om/build control-elements data))))))))

(defn main-content-view [data]
  (reify om/IRender
    (render [_]
      (dom/div nil
               (when (seq (:discussion @lib/app-state))
                 (dom/div #js {:className "text-center"}
                          (t :discussion :current)
                          (dom/br nil)
                          (dom/strong nil (get-in data [:issues :info]))))
               (om/build view-dispatcher data)
               (when (get-in data [:layout :add?])
                 (dom/div nil
                          (om/build add-element {})
                          (om/build search/results-now data)))
               (om/build nav/main data)
               (om/build clipboard/view data)))))

(defn main-view [data]
  (reify om/IRender
    (render [_]
      (dom/div #js {:id (lib/prefix-name "dialog-main")}
               (avatar-view)
               (dom/h4 nil
                       (vlib/logo)
                       " "
                       (dom/span #js {:className     "pointer"
                                      :data-toggle   "collapse"
                                      :data-target   (str "#" (lib/prefix-name "dialog-collapse"))
                                      :aria-expanded "true"
                                      :aria-controls (lib/prefix-name "dialog-collapse")}
                                 (get-in data [:layout :title])))
               (dom/div #js {:className "collapse in"
                             :id        (lib/prefix-name "dialog-collapse")}
                        (om/build main-content-view data))))))

;; -----------------------------------------------------------------------------
;; om.next Views

(defui ItemView
  static nom/IQuery
  (query [this] [:htmls :url])
  Object
  (render [this]
          (let [{:keys [htmls url]} (nom/props this)]
            (html [:div.radio
                   [:label
                    [:input {:type "radio"
                             :className (lib/prefix-name "dialog-items")
                             :name (lib/prefix-name "dialog-items-group")
                             :onClick #(println "clicked item, goto" url)
                             :value url}]
                    " "
                    (vlib/safe-html (string/join (str " <i>" (t :common :and) "</i> ") htmls))]]))))
(def item-view-next (nom/factory ItemView {:keyfn :url}))

(defui ItemsView
  static nom/IQuery
  (query [this]
         `[{:discussion/items ~(nom/get-query ItemView)}])
  Object
  (render [this]
          (let [{:keys [discussion/items]} (nom/props this)]
            (html [:div (map item-view-next items)]))))
(def items-view-next (nom/factory ItemsView))

(defui DiscussionElements
  static nom/IQuery
  (query [this] [:discussion/items :discussion/bubbles])
  Object
  (render [this]
          (html [:div
                 (bubbles/bubbles-view-next (nom/props this))
                 (items-view-next (nom/props this))])))
(def discussion-elements-next (nom/factory DiscussionElements))

#_(defn discussion-elements-next
  "Show default view for the discussion"
  [this]
  (html [:div
         (bubbles/bubbles-view-next (nom/props this))
         (items-view-next (nom/props this))]))

(defui ViewDispatcher
  static nom/IQuery
  (query [this] [:layout/view :discussion/items :discussion/bubbles])
  Object
  (render [this]
          (let [{:keys [layout/view]} (nom/props this)]
            (html [:div.panel.panel-default
                   [:div.panel-body
                    (case view
                      ;; TODO: :login (om/build login-form {})
                      ;; TODO: :options (om/build options/view data)
                      ;; TODO: :reference-usages (om/build ref/usages-view data)
                      ;; TODO: :reference-create-with-ref (om/build ref/create-with-reference-view data)
                      ;; TODO: :find (om/build find/view data)
                      (discussion-elements-next (nom/props this)))
                    #_(if (contains? #{:login :options :find :reference-usages} view)
                      (om/build close-button data)
                      (om/build control-elements data))]]))))
(def view-dispatcher-next (nom/factory ViewDispatcher))

(defui MainContentView
  static nom/IQuery
  (query [this] [:issue/info :discussion/items :discussion/bubbles])
  Object
  (render [this]
          (let [{:keys [issue/info]} (nom/props this)]
            (html [:div
                   [:div.text-center
                    (t :discussion :current)
                    [:br]
                    [:strong info]]
                   (view-dispatcher-next (nom/props this))]))))
(def main-content-view-next (nom/factory MainContentView))

(defui MainView
  static nom/IQuery
  (query [this]
         [:layout/title :issue/info :discussion/items :discussion/bubbles])
  Object
  (render [this]
          (let [{:keys [layout/title]} (nom/props this)]
            (html [:div#discuss-dialog-main
                   (avatar-view)
                   [:h4 (vlib/logo)
                    " "
                    [:span.pointer {:data-toggle   "collapse"
                                    :data-target   (str "#" (lib/prefix-name "dialog-collapse"))
                                    :aria-expanded "true"
                                    :aria-controls (lib/prefix-name "dialog-collapse")}
                     title]]
                   (main-content-view-next (nom/props this))]))))
(def main-view-next (nom/factory MainView))

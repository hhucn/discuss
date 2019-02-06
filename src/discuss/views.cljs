(ns discuss.views
  (:require [om.next :as om :refer-macros [defui]]
            [sablono.core :refer-macros [html]]
            [discuss.components.bubbles :as bubbles]
            [discuss.components.clipboard :as clipboard]
            [discuss.components.items :as items]
            [discuss.components.navigation :as nav]
            [discuss.components.options :as options]
            [discuss.components.search.statements :as search]
            [discuss.components.avatar :as avatar]
            [discuss.communication.lib :as comlib]
            [discuss.references.main :as ref]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.bootstrap :as bs]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]
            [discuss.views.add :as vadd]
            [discuss.views.login :as vlogin]
            [discuss.parser :as parser]
            [discuss.components.tooltip :as tooltip]
            [discuss.eden.views :as eviews]
            [discuss.components.create-argument :as carg]))

(defn close-button-next
  "Close current panel and switch view."
  []
  (html [:div [:br]
         [:div.text-center
          (bs/button-default-sm #(lib/change-to-next-view!) (vlib/fa-icon "fa-times") (t :common :close :space))]]))

(defn control-elements-next
  "Back and restart button."
  []
  (html [:div [:hr]
         [:div.row
          [:div {:className "col-md-offset-4 col-sm-offset-4 col-xs-offset-4 col-md-4 col-sm-4 col-xs-4 text-center"}
           [:button.btn.btn-default.btn-sm {:onClick parser/back!
                                            :disabled  (neg? (count (parser/mutation-history parser/reconciler)))}
            (vlib/fa-icon "fa-step-backward")
            (t :common :back :space)]]
          [:div.col-md-4.col-sm-4.col-xs-4.text-right
           (bs/button-default-sm #(comlib/init!) (vlib/fa-icon "fa-refresh") (t :discussion :restart :space))]]]))


(defui DiscussionElements
  Object
  (render [this]
          (html [:div
                 (bubbles/bubbles-view-next (om/props this))
                 (items/items (om/props this))])))
(def discussion-elements-next (om/factory DiscussionElements))

(defui ViewDispatcher
  static om/IQuery
  (query [this] [:layout/view])
  Object
  (render [this]
          (let [{:keys [layout/view]} (om/props this)]
            (html [:div.panel.panel-default
                   [:div.panel-body
                    (case view
                      :login (vlogin/login-form (om/props this))
                      :options (options/options (om/props this))
                      :reference-usages (ref/usages-view-next (om/props this))
                      :eden/overview (eviews/overview-menu (om/props this))
                      :eden/add-argument (eviews/eden-argument-form (om/props this))
                      :eden/show-arguments (eviews/show-arguments (om/props this))
                      :create/argument (carg/create-argument-with-reference (om/props this))
                      :discussion/main (discussion-elements-next (om/props this))
                      (discussion-elements-next (om/props this)))
                    (cond
                      (some #{view} [:login :options :find :reference-usages :eden/overview])
                      (close-button-next)
                      (some #{view} [:create/argument]) nil
                      :default (control-elements-next))]]))))
(def view-dispatcher-next (om/factory ViewDispatcher))

(defui MainView
  static om/IQuery
  (query [this] [:layout/add? :layout/title :discussion/add-step])
  Object
  (render [this]
          (let [{:keys [layout/add? layout/title discussion/add-step]} (om/props this)]
            (html
             [:div#discuss-dialog-main
              (avatar/avatar (om/props this))
              [:h4 (vlib/logo)
               " "
               [:span.pointer {:data-toggle   "collapse"
                               :data-target   (str "#" (lib/prefix-name "dialog-collapse"))
                               :aria-expanded "true"
                               :aria-controls (lib/prefix-name "dialog-collapse")}
                title
                [:small [:small " " (lib/project-version)]]]]
              [:br]
              (view-dispatcher-next (om/props this))
              (when add?
                [:div
                 (if (= :add/position add-step)
                   (vadd/position-form (om/props this))
                   (vadd/statement-form (merge {:button-fn #(prn "Foo")} (om/props this))))])
              [:div (nav/nav (om/props this))]
              [:br]
              (search/results (om/props this))
              [:div (clipboard/clipboard (om/props this))]]))))
(def main-view (om/factory MainView))

(defui Overlay
  Object
  (render
   [this]
   (let [modal-name (lib/prefix-name "overlay")]
     (html [:div {:className "modal fade"
                  :id modal-name
                  :tabIndex -1
                  :role "dialog"
                  :aria-labelledby (str modal-name "Label")}
            [:div {:className "modal-dialog modal-lg"
                   :role "document"}
             [:div.modal-content
              [:div.modal-header {:style {:paddingBottom 0}}
               [:button {:type "button"
                         :className "close"
                         :data-dismiss "modal"
                         :aria-label "Close"}
                [:span {:aria-hidden true}
                 (vlib/safe-html "&times;")]]]
              [:div.modal-body
               (main-view (om/props this))]]]]))))
(def overlay (om/factory Overlay))

(defui Discuss
  Object
  (render [this]
          (html
           [:div
            (tooltip/tooltip (om/props this))
            (overlay (om/props this))
            (main-view (om/props this))])))
(def discuss (om/factory Discuss {:keyfn identity}))

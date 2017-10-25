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
    (if (pos? remaining)
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

(defn add-element
  "Show form to add a new statement."
  [_ owner]
  (reify
    om/IInitState
    (init-state [_]
      {:statement ""})
    om/IRenderState
    (render-state [_ {:keys [statement]}]
      (om/observe owner (lib/get-cursor :user))
      (om/observe owner (lib/get-cursor :references))
      (dom/div #js {:className  "panel panel-default"
                    :onDragOver clipboard/allow-drop
                    :onDrop     clipboard/update-reference-drop}
               (dom/div #js {:className "panel-body"}
                        (dom/h4 #js {:className "text-center"} (t :discussion :add-argument))
                        (dom/h5 #js {:className "text-center"} (vlib/safe-html (lib/get-add-premise-text)))
                        (om/build error-view {})
                        (dom/div #js {:className "input-group"}
                                 (dom/span #js {:className "input-group-addon input-group-addon-left"}
                                           (str "... " (t :common :because)))
                                 (dom/input #js {:className "form-control"
                                                 :onChange  (fn [e]
                                                              (search/search (.. e -target -value))
                                                              (vlib/commit-component-state :statement e owner))
                                                 :value     statement}))
                        (show-selection)
                        (dom/button #js {:className "btn btn-default"
                                         :onClick   #(com/dispatch-add-action statement (lib/get-selection))
                                         :disabled  (> 10 (count statement))}
                                    (remaining-characters statement)))))))

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
                 (om/build add-element {}))
               (om/build nav/main data)
               (om/build clipboard/view data)))))

@(nom/app-state parser/reconciler)

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


(defui ^:once MainView
  Object
  (render [this]
          (html [:div
                 (om/build main-view lib/app-state)
                 (search/results (nom/props this))])))
(def main-view-next (nom/factory MainView))

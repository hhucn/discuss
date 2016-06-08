(ns discuss.views
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string :as string]
            [discuss.auth :as auth]
            [discuss.clipboard :as clipboard]
            [discuss.communication :as com]
            [discuss.extensions]
            [discuss.find :as find]
            [discuss.history :as history]
            [discuss.integration :as integration]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]
            [discuss.references :as ref]
            [discuss.sidebar :as sidebar]))

;;;; Auxiliary functions
(defn get-bubble-class [bubble]
  "Check bubble type and return a class-string to match the CSS styles."
  (cond
    (:is_user bubble) "bubble-user"
    (:is_system bubble) "bubble-system"
    (:is_status bubble) "bubble-status text-center"
    (:is_info bubble) "bubble-info text-center"))


;;;; Elements
(defn control-elements []
  (dom/div #js {:className "text-center"}
           (dom/h4 nil
                   (vlib/fa-icon (str (lib/prefix-name "control-buttons") " fa-angle-double-left fa-border") com/init!)
                   " "
                   (vlib/fa-icon (str (lib/prefix-name "control-buttons") " fa-angle-left fa-border") history/back!)
                   " "
                   (vlib/fa-icon (str (lib/prefix-name "control-buttons") " fa-angle-right fa-border pointer")))))

(defn avatar-view
  "Get the user's avatar and add login + logout functions to it."
  []
  (dom/div #js {:className "discuss-avatar-main-wrapper pull-right text-muted text-center"}
           (if (lib/logged-in?)
             (dom/div nil
                      (dom/img #js {:src       (lib/get-avatar)
                                    :className "discuss-avatar-main img-responsive img-circle"})
                      (dom/span nil (str "Hello " (lib/get-nickname) "!"))
                      " "
                      (dom/span #js {:className "pointer"
                                     :onClick   auth/logout}
                                (vlib/fa-icon "fa-sign-out")))
             (dom/div #js {:className "pointer"
                           :onClick   #(lib/change-view! :login)}
                      (vlib/fa-icon "fa-sign-in")))))

(defn login-form [_ owner]
  (reify
    om/IInitState
    (init-state [_]
      {:nickname ""
       :password ""})
    om/IRenderState
    (render-state [_ {:keys [nickname password]}]
      (dom/div nil
               (dom/h5 #js {:className "text-center"} "Login")
               (dom/div #js {:className "input-group"}
                        (dom/span #js {:className "input-group-addon"}
                                  (vlib/fa-icon "fa-user fa-fw"))
                        (dom/input #js {:className   "form-control"
                                        :onChange    #(vlib/commit-component-state :nickname % owner)
                                        :value       nickname
                                        :placeholder "nickname"}))
               (dom/div #js {:className "input-group"}
                        (dom/span #js {:className "input-group-addon"}
                                  (vlib/fa-icon "fa-key fa-fw"))
                        (dom/input #js {:className   "form-control"
                                        :onChange    #(vlib/commit-component-state :password % owner)
                                        :value       password
                                        :type        "password"
                                        :placeholder "password"}))
               (dom/button #js {:className "btn btn-default"
                                :onClick   #(auth/login nickname password)}
                           "Login")
               (dom/div #js {:className "text-center text-muted pointer"
                             :onClick   #(lib/change-view! :discussion)}
                        "Back")))))

;; Views
(defn bubble-view [bubble]
  (reify om/IRender
    (render [_]
      (let [bubble-class (get-bubble-class bubble)
            ref (lib/get-reference (:id bubble))]
        (comment (println bubble)
                 (println (:id bubble)))
        (dom/li #js {:className bubble-class}
                (dom/div #js {:className "avatar"})
                (dom/p #js {:className "messages"}
                       (vlib/safe-html (:message bubble))))))))

(defn bubbles-view []
  (apply dom/ol #js {:className "bubbles"}
         (map #(om/build bubble-view (lib/merge-react-key %)) (lib/get-bubbles))))

(defn item-view [item _owner]
  (reify om/IRender
    (render [_]
      (dom/div #js {:className "radio"}
               (dom/label #js {}
                          (dom/input #js {:type      "radio"
                                          :className (lib/prefix-name "dialogue-items")
                                          :name      (lib/prefix-name "dialogue-items-group")
                                          :onClick   #(com/item-click (:id item) (:url item))
                                          :value     (:url item)})
                          " "
                          (vlib/safe-html (string/join " <i>and</i> " (map :title (:premises item))))))))) ; get all premises of item and add an "and" between them

(defn items-view [data]
  (dom/div nil
           (apply dom/ul #js {:id (lib/prefix-name "items-main")}
                  (map #(om/build item-view (lib/merge-react-key %)) (:items data)))))

(defn discussion-elements [data]
  (dom/div nil
           (bubbles-view)
           (items-view data)
           (control-elements)))

(defn error-view
  "Display error message if there are errors."
  []
  (when (lib/error?)
    (dom/div #js {:className "alert alert-info alert-dismissable"
                  :role      "alert"}
             (dom/button #js {:className    "close"
                              :data-dismiss "alert"
                              :aria-label   "Close"}
                         (dom/span #js {:aria-hidden "true"}
                                   (vlib/safe-html "&times;")))
             (lib/get-error))))

(defn add-element
  "Show form to add a new statement."
  [_ owner]
  (reify
    om/IInitState
    (init-state [_]
      {:statement ""})
    om/IRenderState
    (render-state [_ {:keys [statement]}]
      (dom/div #js {:className  "panel panel-default"
                    :onDragOver discuss.clipboard/allow-drop
                    :onDrop     discuss.clipboard/update-reference-drop}
               (dom/div #js {:className "panel-body"}
                        (dom/h4 #js {:className "text-center"} (lib/get-add-text))
                        (dom/h5 #js {:className "text-center"} (vlib/safe-html (lib/get-add-premise-text)))
                        (error-view)
                        (dom/div #js {:className "input-group"}
                                 (dom/span #js {:className "input-group-addon"}
                                           (vlib/fa-icon "fa-comment"))
                                 (dom/input #js {:className "form-control"
                                                 :onChange  #(vlib/commit-component-state :statement % owner)
                                                 :value     statement}))
                        (when (lib/get-selection)
                          (dom/div #js {:className "input-group"}
                                   (dom/span #js {:className "input-group-addon"}
                                             (vlib/fa-icon "fa-quote-left"))
                                   (dom/input #js {:className "form-control"
                                                   :value     (lib/get-selection)})
                                   (dom/span #js {:className "input-group-addon"}
                                             (vlib/fa-icon "fa-quote-right"))))
                        (dom/button #js {:className "btn btn-default"
                                         :onClick   #(com/dispatch-add-action statement (lib/get-selection))}
                                    "Submit"))))))

(defn view-dispatcher
  "Dispatch current template in main view by the app state."
  [data]
  (let [view (lib/current-view)]
    (cond
      (= view :login) (om/build login-form {})
      (= view :reference-dialog) (om/build ref/dialog-view {})
      (= view :reference-usages) (om/build ref/usages-view {})
      :else (discussion-elements data))))

(defn main-content-view
  [data]
  (dom/div nil
           (dom/div #js {:className "text-center"}
                    (get-in data [:layout :intro])
                    (dom/br nil)
                    (dom/strong nil (get-in data [:issues :info])))
           (vlib/panel-wrapper
             (view-dispatcher data))
           (when (get-in data [:layout :add?])
             (om/build add-element {}))))

(defn main-view [data _owner]
  (reify om/IRender
    (render [_]
      (dom/div #js {:id (lib/prefix-name "dialogue-main")}
               (avatar-view)
               (dom/h4 nil
                       (vlib/logo)
                       " "
                       (get-in data [:layout :title]))
               (main-content-view data)))))

(defn reference-view [data owner]
  (reify
    om/IInitState
    (init-state [_]
      {:show false})
    om/IRenderState
    (render-state [_ {:keys [show]}]
      (dom/span nil
                (dom/span nil (:dom-pre data))
                (dom/span #js {:className "arguments pointer"
                               :onClick   #(ref/click-reference data)}
                          (:text data)
                          " "
                          (vlib/logo #(om/set-state! owner :show (vlib/toggle-show show))))
                (dom/span nil (:dom-post data))))))


;;;; Sidebar
(defn sidebar-view [data _owner]
  (reify om/IRender
    (render [_]
      (dom/div nil
               (vlib/fa-icon "fa-bars" #(sidebar/toggle))
               (om/build main-view data)
               (om/build find/form-view {})
               (om/build find/results-view data)
               (om/build clipboard/view data)))))
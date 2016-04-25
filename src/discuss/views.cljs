(ns discuss.views
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string :as string]
            [discuss.communication :as com]
            [discuss.extensions]
            [discuss.history :as history]
            [discuss.integration :as integration]
            [discuss.lib :as lib]
            [discuss.sidebar :as sidebar]
            [discuss.auth :as auth]))

;; Auxiliary functions
(defn fa-icon
  "Wrapper for font-awesome icons."
  ([class]
   (dom/i #js {:className (str "fa " class)}))
  ([class f]
   (dom/i #js {:className (str "pointer fa " class)
               :onClick   f})))

(defn logo
  "If no function is provided, show logo as is. Else bind function to onClick-event and add
   pointer-class."
  ([]
   (fa-icon "fa-comments"))
  ([f]
   (fa-icon "fa-comments" f)))

(defn safe-html
  "Creates DOM element with interpreted HTML."
  [string]
  (dom/span #js {:dangerouslySetInnerHTML #js {:__html string}}))

(defn get-bubble-class [bubble]
  "Check bubble type and return a class-string to match the CSS styles."
  (cond
    (:is_user bubble) "bubble-user"
    (:is_system bubble) "bubble-system"
    (:is_status bubble) "bubble-status text-center"))

(defn- commit-target-value
  "Set local state of view, parse the value of the target of val."
  [key val owner]
  (om/set-state! owner key (.. val -target -value)))

(defn display
  "Toggle display view."
  [show]
  (if show
    #js {}
    #js {:display "none"}))

(defn toggle-show [show] (if show false true))

;; Elements
(defn loading-element []
  (when (lib/loading?)
    (dom/div #js {:className "loader"}
             (dom/svg #js {:className "circular" :viewBox "25 25 50 50"}
                      (dom/circle #js {:className "path" :cx "50" :cy "50" :r "20" :fill "none" :strokeWidth "5" :strokeMiterlimit "10"})))))

(defn control-elements []
  (dom/div #js {:className "text-center"}
           (dom/h4 nil
                   (fa-icon (str (lib/prefix-name "control-buttons") " fa-angle-double-left fa-border") com/init!)
                   " "
                   (fa-icon (str (lib/prefix-name "control-buttons") " fa-angle-left fa-border") history/back!)
                   " "
                   (fa-icon (str (lib/prefix-name "control-buttons") " fa fa-angle-right fa-border pointer")))))

(defn login-view-buttons [data]
  (dom/div #js {:className "text-muted"}
           (dom/div #js {:className "row"}
                    (if (lib/logged-in?)
                      (dom/div #js {:className "col-md-5"}
                               (str "Logged in as " (get-in data [:user :nickname])))
                      (dom/div #js {:className "col-md-5"}))
                    (dom/div #js {:className "col-md-2 text-center"}
                             (loading-element))

                    (if (lib/logged-in?)
                      (dom/div #js {:className "col-md-5 text-right pointer"
                                    :onClick   auth/logout}
                               "Logout")
                      (dom/div #js {:className "col-md-5 text-right pointer"
                                    :onClick   #(lib/change-view! :login)}
                               "Login")))))

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
                                  (fa-icon "fa-user fa-fw"))
                        (dom/input #js {:className   "form-control"
                                        :onChange    #(commit-target-value :nickname % owner)
                                        :value       nickname
                                        :placeholder "nickname"}))
               (dom/div #js {:className "input-group"}
                        (dom/span #js {:className "input-group-addon"}
                                  (fa-icon "fa-key fa-fw"))
                        (dom/input #js {:className   "form-control"
                                        :onChange    #(commit-target-value :password % owner)
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
      (let [bubble-class (get-bubble-class bubble)]
        (dom/li #js {:className bubble-class}
                (dom/div #js {:className "avatar"})
                (dom/p #js {:className "messages"}
                       (safe-html (:message bubble))))))))

(defn bubbles-view []
  (apply dom/ol #js {:className "bubbles"}
         (om/build-all bubble-view (lib/get-bubbles))))

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
                          (safe-html (string/join " <i>and</i> " (map #(:title %) (:premises item))))))))) ; get all premises of item and add an "and" between them

(defn items-view [data]
  (dom/div nil
           (apply dom/ul #js {:id (lib/prefix-name "items-main")}
                  (om/build-all item-view (:items data)))))

(defn discussion-elements [data]
  (dom/div nil
           (bubbles-view)
           (items-view data)
           (control-elements)
           (login-view-buttons data)))

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
                                   (safe-html "&times;")))
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
      (dom/div #js {:className "panel panel-default"}
               (dom/div #js {:className "panel-body"}
                        (dom/h4 #js {:className "text-center"} (lib/get-add-text))
                        (dom/h5 #js {:className "text-center"} (safe-html (lib/get-add-premise-text)))
                        (error-view)
                        (dom/div #js {:className "input-group"}
                                 (dom/span #js {:className "input-group-addon"}
                                           (fa-icon "fa-comment"))
                                 (dom/input #js {:className "form-control"
                                                 :onChange  #(commit-target-value :statement % owner)
                                                 :value     statement}))
                        (when (lib/get-selection)
                          (dom/div #js {:className "input-group"}
                                   (dom/span #js {:className "input-group-addon"}
                                             (fa-icon "fa-quote-left"))
                                   (dom/input #js {:className "form-control"
                                                   :value     (lib/get-selection)})
                                   (dom/span #js {:className "input-group-addon"}
                                             (fa-icon "fa-quote-right"))))
                        (dom/button #js {:className "btn btn-default"
                                         :onClick   #(com/dispatch-add-action statement (lib/get-selection))}
                                    "Submit"))))))

(defn main-content-view
  [data]
  (dom/div nil
           (dom/div #js {:className "text-center"}
                    (get-in data [:layout :intro])
                    (dom/br nil)
                    (dom/strong nil (get-in data [:issues :info])))
           (dom/div #js {:className "panel panel-default"}
                    (dom/div #js {:className "panel-body"}
                             (let [view (get-in data [:layout :template])]
                               (cond
                                 (= view :login) (om/build login-form {})
                                 :else (discussion-elements data)))))
           (when (get-in data [:layout :add?])
             (om/build add-element {}))))

(defn main-view [data owner]
  (reify om/IRender
    (render [_]
      (dom/div #js {:id (lib/prefix-name "dialogue-main")}
               (dom/h4 nil
                       (logo)
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
                               :onClick   #(integration/click-reference (:text data) (:url data))}
                          (:text data)
                          " "
                          (logo #(om/set-state! owner :show (toggle-show show))))
                (dom/span nil (:dom-post data))))))

(defn sidebar-view [data owner]
  (reify om/IRender
    (render [_]
      (dom/div nil
               (logo #(sidebar/toggle))
               (main-content-view data)
               (dom/h5 nil "Reference")
               (dom/div #js {:className "well well-sm"}
                        (get-in @lib/app-state [:layout :reference]))))))

(defn tooltip-view [data owner]
  (reify om/IRender
    (render [_]
      (dom/div nil
               (logo)
               " "
               "Save as reference"))))
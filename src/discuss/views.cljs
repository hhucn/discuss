(ns discuss.views
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.pprint :refer [pprint]]
            [discuss.communication :as com]
            [discuss.history :as history]
            [discuss.lib :as lib]
            [discuss.auth :as auth]))

;; Auxiliary functions
(defn safe-html
  "Creates DOM element with interpreted HTML."
  [string]
  (dom/span #js {:dangerouslySetInnerHTML #js {:__html string}}))


;; Elements
(defn loading-element [data]
  (when (lib/loading?)
    (dom/i #js {:className "fa fa-circle-o-notch fa-spin pull-right"})))

(defn control-elements []
  (dom/div #js {:className "text-center"}
           (dom/h3 nil
                   (dom/i #js {:className "fa fa-angle-double-left pointer"
                               :onClick lib/init!})
                   " "
                   (dom/i #js {:className "fa fa-angle-left pointer"
                               :onClick history/back!})
                   " "
                   (dom/i #js {:className "fa fa-angle-right pointer"}))))

(defn login-view-buttons [data _owner]
  (dom/div #js {:className "text-muted"}
           (dom/div #js {:className "row"}
                    (if (get-in data [:user :logged-in?])
                      (do
                        (dom/div nil
                                 (dom/div #js {:className "col-md-6"}
                                          (str "Logged in as " (get-in data [:user :nickname])))
                                 (dom/div #js {:className "col-md-6 text-right pointer"
                                               :onClick auth/logout}
                                          "Logout")))
                      (dom/div #js {:className "col-md-offset-6 col-md-6 text-right pointer"
                                    :onClick #(lib/change-view! :login)}
                               "Login")))))

(defn login-form []
  (dom/div nil
           (dom/form nil
                     (dom/div #js {:className "form-group"}
                              (dom/label #js {:htmlFor "login-form-nickname"} "Nickname")
                              (dom/input #js {:id (lib/prefix-name "login-nickname")
                                              :className "form-control"
                                              :placeholder "nickname"}))
                     (dom/div #js {:className "form-group"}
                              (dom/label #js {:htmlFor "login-form-password"} "Password")
                              (dom/input #js {:id (lib/prefix-name "login-password")
                                              :className "form-control"
                                              :type "password"
                                              :placeholder "password"})))
           (dom/button #js {:className "btn btn-default"
                            :onClick   #(auth/login (lib/get-value-by-id "login-nickname") (lib/get-value-by-id "login-password"))}
                       "Submit")
           (dom/div #js {:className "text-center text-muted pointer"
                         :onClick #(lib/change-view! :discussion)}
                    "Back")))


;; Views
(defn clipboard-view []
  (reify om/IRender
    (render [_]
      (dom/div #js {:id "foo"}
               (dom/h5 nil "discuss")
               (dom/hr #js {:className "line-double"})
               (dom/div #js {:id (lib/prefix-name "clipboard-topic")})
               (dom/hr nil)
               (dom/div #js {:id (lib/prefix-name "clipboard-arguments")})))))

(defn item-view [item _owner]
  (reify om/IRender
    (render [_]
      (dom/div #js {:className "radio"}
               (dom/label #js {}
                          (dom/input #js {:id        (:id item)
                                          :type      "radio"
                                          :className (lib/prefix-name "dialogue-items")
                                          :name      (lib/prefix-name "dialogue-items-group")
                                          :onClick   #(com/item-click (:url item))
                                          :value     (:url item)})
                          " "
                          (safe-html (:title item)))))))

(defn discussion-elements [data owner]
  (dom/div nil
           (dom/h4 #js {:id (lib/prefix-name "dialogue-topic")
                        :className "text-center"}
                   (safe-html (:message (first (get-in data [:discussion :bubbles])))))
           (apply dom/ul #js {:id (lib/prefix-name "items-main")}
                  (om/build-all item-view (:items data)))
           (control-elements)
           (login-view-buttons data owner)))

(defn add-element [data _owner]
  (dom/div #js {:className "panel panel-default"}
           (dom/div #js {:className "panel-body"}
                    (dom/h4 #js {:className "text-center"} (get-in data [:layout :add-text]))
                    (dom/h5 #js {:className "text-center"} (safe-html (get-in data [:discussion :add_premise_text])))
                    (dom/form nil
                              (dom/div #js {:className "form-group"}
                                       (dom/label #js {:htmlFor "add-element"} (get-in data [:discussion :heading :outro]))
                                       (dom/input #js {:id (lib/prefix-name "login-nickname")
                                                       :className "form-control"}))))))

(defn main-view [data owner]
  (reify om/IRender
    (render [_]
      (dom/div #js {:id (lib/prefix-name "dialogue-main")
                    :className "container"}
               (dom/h3 nil
                       (dom/i #js {:className "fa fa-comments"})
                       (str " " (get-in data [:layout :title])))
               (dom/div #js {:className "text-center"}
                        (:intro (:layout data))
                        (dom/br nil)
                        (dom/strong nil (:info (:issues data))))
               (dom/div #js {:className "panel panel-default"}
                        (dom/div #js {:className "panel-body"}
                                 (loading-element data)
                                 (let [view (get-in data [:layout :template])]
                                   (cond
                                     (= view :login) (login-form)
                                     :else (discussion-elements data owner)))))
               (when (get-in data [:layout :add?])
                 (add-element data owner))))))

(defn debug-view [data _owner]
  (reify om/IRender
    (render [_]
      (dom/div nil
               (dom/h4 nil "Last API call")
               (dom/pre nil (get-in data [:debug :last-api]))

               (dom/h4 nil "Last response")
               ;(pprint data)
               (dom/pre nil
                        (apply dom/ul nil
                               (map (fn [[k v]] (dom/li nil (str k "\t\t" v))) (get-in data [:debug :response]))))))))
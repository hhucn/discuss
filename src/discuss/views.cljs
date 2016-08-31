(ns discuss.views
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string :as string]
            [goog.dom :as gdom]
            [discuss.components.bubbles :as bubbles]
            [discuss.components.clipboard :as clipboard]
            [discuss.communication.auth :as auth]
            [discuss.communication.main :as com]
            [discuss.history :as history]
            [discuss.references.lib :as rlib]
            [discuss.references.main :as ref]
            [discuss.utils.bootstrap :as bs]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]))

;;;; Auxiliary
(defn- remaining-characters
  "Show remaining characters needed to submit a post."
  [statement]
  (let [remaining (- 10 (count statement))]
    (if (pos? remaining)
      (str "Noch " remaining " Zeichen")
      "Abschicken")))


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
                        (dom/div #js {:className "col-md-offset-4 col-md-4 text-center"}
                                 (bs/button-default-sm history/back! (vlib/fa-icon "fa-step-backward") "Zurück"))
                        (dom/div #js {:className "col-md-4 text-right"}
                                 (bs/button-default-sm com/init! (vlib/fa-icon "fa-refresh") "Neustarten")))))))

(defn avatar-view
  "Get the user's avatar and add login + logout functions to it."
  []
  (dom/div #js {:className "discuss-avatar-main-wrapper pull-right text-muted text-center"}
           (if (lib/logged-in?)
             (dom/div nil
                      (dom/img #js {:src       (lib/get-avatar)
                                    :className "discuss-avatar-main img-responsive img-circle"})
                      (dom/span nil (str "Hallo " (lib/get-nickname) "!"))
                      " "
                      (dom/span #js {:className "pointer"
                                     :onClick   auth/logout}
                                (vlib/fa-icon "fa-sign-out")))
             (dom/div #js {:className "pointer"
                           :onClick   #(lib/change-view! :login)}
                      (vlib/fa-icon "fa-sign-in")
                      " Login"))))

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
               (dom/div #js {:className "text-center"} "Login")
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
                                :onClick   #(auth/login nickname password)
                                :disabled  (not (and (pos? (count nickname))
                                                     (pos? (count password))))}
                           "Login")))))

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
                                          :className (lib/prefix-name "dialogue-items")
                                          :name      (lib/prefix-name "dialogue-items-group")
                                          :onClick   #(com/item-click (:id item) (:url item))
                                          :value     (:url item)})
                          " "
                          (vlib/safe-html (string/join " <i>and</i> " (map :title (:premises item))))))))) ; get all premises of item and add an "and" between them

(defn items-view
  "Show discussion items."
  [data]
  (reify om/IRender
    (render [_]
      (dom/div nil
               (apply dom/ul #js {:id (lib/prefix-name "items-main")}
                      (map #(om/build item-view (lib/merge-react-key %)) (:items data)))))))

(defn init-view
  "Show button if discussion has not been initialized yet."
  []
  (dom/div #js {:className "text-center"} (bs/button-primary com/init-with-references! "Starte Diskussion!")))

(defn discussion-elements [data]
  (if-not (empty? (:discussion @lib/app-state))
    (dom/div #js {:key (lib/get-unique-key)}
             (om/build bubbles/view data)
             (om/build items-view data)
             (om/build control-elements data))
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
               (dom/span #js {:className "input-group-addon"}
                         (vlib/fa-icon "fa-quote-left"))
               (dom/input #js {:className "form-control"
                               :value     selection})
               (dom/span #js {:className "input-group-addon"}
                         (vlib/fa-icon "fa-quote-right"))
               (dom/span #js {:className "input-group-addon pointer"
                              :onClick   remove-selection-then-reference!}
                         (vlib/fa-icon "fa-times")))
      (dom/div #js {:className "text-center"} "Möchten Sie Ihre Aussage durch eine Referenz von dieser Seite stützen? Dann markieren Sie einfach einen Teil des Textes mit der Maus."))))

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
                        (dom/h4 #js {:className "text-center"} (lib/get-add-text))
                        (dom/h5 #js {:className "text-center"} (vlib/safe-html (lib/get-add-premise-text)))
                        (om/build error-view {})
                        (dom/div #js {:className "input-group"}
                                 (dom/span #js {:className "input-group-addon"}
                                           (vlib/fa-icon "fa-comment"))
                                 (dom/input #js {:className "form-control"
                                                 :onChange  #(vlib/commit-component-state :statement % owner)
                                                 :value     statement}))
                        (show-selection)
                        (dom/button #js {:className "btn btn-default"
                                         :onClick   #(com/dispatch-add-action statement (lib/get-selection))
                                         :disabled  (> 10 (count statement))}
                                    (remaining-characters statement))
                        (dom/hr nil)
                        (dom/p #js {:className "text-muted text-center"}
                               "Das Markieren von Textpassagen über mehrere Absätze hinweg und das doppelte markieren schon markierter Textstellen wurde noch nicht implementiert."))))))

(defn- build-with-buttons
  "Add navigation buttons to the provided view."
  [view data]
  (dom/div nil
           (om/build view data)
           (om/build control-elements data)))

(defn view-dispatcher
  "Dispatch current template in main view by the app state."
  [data]
  (let [view (lib/current-view)]
    (cond
      (= view :login) (build-with-buttons login-form {})
      (= view :reference-usages) (build-with-buttons ref/usages-view {})
      (= view :reference-create-with-ref) (build-with-buttons ref/create-with-reference-view data)
      :else (discussion-elements data))))

(defn main-content-view [data]
  (dom/div nil
           (when (seq (:discussion @lib/app-state))
             (dom/div #js {:className "text-center"}
                      (get-in data [:layout :intro])
                      (dom/br nil)
                      (dom/strong nil (get-in data [:issues :info]))))
           (bs/panel-wrapper (view-dispatcher data))
           (when (get-in data [:layout :add?])
             (om/build add-element {}))
           (clipboard/view)))

(defn main-view [data]
  (reify om/IRender
    (render [_]
      (dom/div #js {:id (lib/prefix-name "dialogue-main")}
               (avatar-view)
               (dom/h4 nil
                       (vlib/logo)
                       " "
                       (dom/span #js {:className     "pointer"
                                      :data-toggle   "collapse"
                                      :data-target   (str "#" (lib/prefix-name "dialogue-collapse"))
                                      :aria-expanded "true"
                                      :aria-controls (lib/prefix-name "dialogue-collapse")}
                                 (get-in data [:layout :title])))
               (dom/div #js {:className "collapse in"
                             :id        (lib/prefix-name "dialogue-collapse")}
                        (main-content-view data))))))

(defn reference-view [reference owner]
  (reify
    om/IInitState
    (init-state [_]
      {:show false})
    om/IRenderState
    (render-state [_ {:keys [show]}]
      (dom/span nil
                (dom/span nil (:dom-pre reference))
                (dom/span #js {:className "arguments pointer"
                               :onClick   #(ref/click-reference reference)}
                          (:text reference)
                          " "
                          (vlib/logo #(om/set-state! owner :show (vlib/toggle-show show))))
                (dom/span nil (:dom-post reference))))))
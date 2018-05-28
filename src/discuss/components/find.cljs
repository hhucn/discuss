(ns discuss.components.find
  "Search engine."
  (:require [om.core :as om]
            [om.dom :as dom]
            [clojure.string :refer [join]]
            [discuss.communication.lib :as comlib]
            [discuss.config :as config]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.bootstrap :as bs]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]))

(defn- get-search-results
  "Extract values and create a list of maps."
  [] (get-in @lib/app-state [:discussion :search :values]))

(defn- statement-handler
  "Called when received a response in the search."
  [response]
  (let [res (lib/process-response response)]
    (lib/update-state-item! :discussion :search (fn [_] res))))

(defn- find-statement
  "Find related statements to given keywords."
  [keywords issue-id]
  (when-not (empty? keywords)
    (let [mode 3
          request-url (join "/" [(get-in config/api [:get :statements]) issue-id mode keywords])]
      (comlib/ajax-get request-url {} statement-handler))))

(defn- update-state-find-statement
  "Saves current state into object and sends search request to discussion system."
  [key val issue-id owner]
  (vlib/commit-component-state key val owner)
  (find-statement (.. val -target -value) issue-id))

(defn- store-selected-issue
  "Store issue id from current selection into local state of the component. Preparation to find statements
   for selected issue."
  [e owner]
  (let [issue-title (.. (first (.. e -target -selectedOptions)) -innerText)
        issue (lib/get-issue issue-title)]
    (vlib/commit-component-state :issue-id (:uid issue) owner)))


;;;; Views
(defn item-view [data _owner]
  (reify om/IRender
    (render [_]
      (let [distance (lib/str->int (:distance data))
            argument (first (:arguments data))
            issue (:issue data)]
        (when (:text argument)
          (dom/div #js {:className "bs-callout bs-callout-info"}
                   (dom/span #js {:className "badge pull-right"} distance)
                   (dom/a #js {:href    "javascript:void(0)"
                               :onClick #(comlib/jump-to-argument (:slug issue) (:uid argument))}
                          (vlib/safe-html (:text argument)))))))))

(defn- issue-selector-view
  "Create option items from each issue."
  [issue]
  (reify om/IRender
    (render [_]
      (let [option-issue (lib/str->int (:uid issue))]
        (dom/option #js {:value (str (lib/prefix-name "issue-selector-") option-issue)}
                    (:title issue))))))

(defn- issue-component
  "Issue selector as separate component. Returns DOM elements. Stores selection in its owner."
  [owner]
  (let [issue-id (:issue-id (om/get-state owner))]
    (dom/div #js {:className "form-group"}
             (dom/select #js {:className "form-control"
                              :onChange  #(store-selected-issue % owner)
                              :value     (str (lib/prefix-name "issue-selector-") issue-id)}
                         (map #(om/build issue-selector-view % (lib/unique-react-key-dict)) (lib/get-issues))))))

(defn form-view
  "Create form to select issue and place the search."
  [_ owner]
  (reify
    om/IInitState
    (init-state [_]
      {:search-value ""
       :issue-id     (get-in @lib/app-state [:issues :uid])})
    om/IRenderState
    (render-state [_ {:keys [search-value issue-id]}]
      (dom/div #js {:className "row"}
               (dom/div #js {:className "col-md-offset-1 col-md-10"}
                        (issue-component owner)
                        (dom/div #js {:className "input-group"}
                                 (dom/input #js {:className   "form-control"
                                                 :onChange    #(update-state-find-statement :search-value % issue-id owner)
                                                 :value       search-value
                                                 :placeholder (t :find :statement)})
                                 (dom/span #js {:className "input-group-btn"}
                                           (bs/button-primary #(find-statement search-value issue-id) (vlib/fa-icon "fa-search fa-fw")))))))))

(defn results-view
  "Show results from the search."
  []
  (reify om/IRender
    (render [_]
      (let [results (get-search-results)]
        (dom/div nil
                 (dom/p nil (str (t :find :hits) ": " (count results)))
                 (apply dom/div nil
                        (map #(om/build item-view % (lib/unique-key-dict)) results)))))))

(defn view
  "Return combined view with form and results."
  [data]
  (reify om/IRender
    (render [_]
      (dom/div nil
               (vlib/view-header (t :find :statement))
               (om/build form-view {})
               (om/build results-view data)))))

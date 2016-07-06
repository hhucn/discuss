(ns discuss.find
  "Search engine."
  (:require [clojure.walk :refer [keywordize-keys]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [discuss.communication :as com]
            [discuss.utils.bootstrap :as bs]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]))

(defn- get-search-results
  "Extract values and create a list of maps."
  []
  (get-in @lib/app-state [:discussion :search :values]))

(defn- statement-handler
  "Called when received a response in the search."
  [response]
  (let [res (com/process-response response)]
    (lib/update-state-item! :discussion :search (fn [_] res))))

(defn- find-statement
  "Find related statements to given keywords."
  [keywords issue-id]
  (when-not (= keywords "")
    (let [mode 3
          ;; TODO change this URL
          request-url (clojure.string/join "/" ["api/get/statements" issue-id mode keywords])]
      (com/ajax-get request-url {} statement-handler))))

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
(defn- item-view [data _owner]
  (reify om/IRender
    (render [_]
      (dom/div #js {:className "bs-callout bs-callout-info"}
               (dom/span #js {:className "badge pull-right"}
                         (lib/str->int (:distance data)))
               (dom/div nil (dom/a #js {:href    "javascript:void(0)"
                                        :onClick #(com/ajax-get (:url data))}
                                   (vlib/safe-html (:text data))))))))

(defn- issue-selector-view
  "Create option items from each issue."
  [issue _owner]
  (dom/option #js {:key      (lib/prefix-name (str "discuss-issue-selector-" (:uid issue)))
                   :onChange #(println (:uid %))}
              (:title issue)))

(defn form-view [_ owner]
  (reify
    om/IInitState
    (init-state [_]
      {:search-value ""
       :issue-id     1})
    om/IRenderState
    (render-state [_ {:keys [search-value issue-id]}]
      (dom/div nil
               (dom/div #js {:className "form-group"}
                        (dom/select #js {:className "form-control"
                                         :onChange  #(store-selected-issue % owner)}
                                    (map #(issue-selector-view % owner) (lib/get-issues))))
               (dom/div #js {:className "input-group"}
                        (dom/input #js {:className   "form-control"
                                        :onChange    #(update-state-find-statement :search-value % issue-id owner)
                                        :value       search-value
                                        :placeholder "Find Statement"})
                        (dom/span #js {:className "input-group-btn"}
                                  (bs/button-primary #(find-statement search-value issue-id) (vlib/fa-icon "fa-search fa-fw"))))))))

(defn results-view []
  (reify om/IRender
    (render [_]
      (let [results (get-search-results)]
        (dom/div nil
                 (dom/h6 nil (str "Received " (count results) " entries."))
                 (apply dom/div nil
                        (map #(om/build item-view (lib/merge-react-key %)) results)))))))
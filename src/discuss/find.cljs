(ns discuss.find
  (:require [clojure.walk :refer [keywordize-keys]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [discuss.communication :as com]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]))

(defn get-search-results
  "Extract values and create a list of maps."
  []
  (get-in @lib/app-state [:discussion :search :values]))

(defn statement-handler
  "Called when received a response in the search."
  [response]
  (let [res (lib/json->clj response)
        error (:error res)]
    (lib/loading? false)
    (if (pos? (count error))
      (lib/error-msg! error)
      (do
        (lib/no-error!)
        (lib/update-state-item! :discussion :search (fn [_] res))))))

(defn find-statement
  "Find related statements to given keywords."
  [keywords]
  (when-not (= keywords "")
    (let [issue 1
          mode 3
          request (clojure.string/join "/" ["api/get/statements" issue mode keywords])]
      (com/ajax-get request {} statement-handler))))

(defn update-state-find-statement
  "Saves current state into object and sends search request to discussion system."
  [key val owner]
  (vlib/commit-component-state key val owner)
  (find-statement (.. val -target -value)))

(defn store-selected-issue
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
      (dom/div #js {:className "bs-callout bs-callout-info"}
               (dom/span #js {:className "badge pull-right"}
                         (lib/str->int (:distance data)))
               (dom/div nil (dom/a #js {:href    "javascript:void(0)"
                                        :onClick #(com/ajax-get (:url data))}
                                   (vlib/safe-html (:text data))))))))

(defn issue-selector-view
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
       :issue-id     -1})
    om/IRenderState
    (render-state [_ {:keys [search-value issue-id]}]
      (dom/div nil
               "Selected: "
               issue-id
               (dom/div #js {:className "form-group"}
                        (dom/label nil "Select Issue")
                        (dom/select #js {:className "form-control"
                                         :onChange  #(store-selected-issue % owner)}
                                    (map #(issue-selector-view % owner) (lib/get-issues))))

               (dom/div #js {:className "input-group"}
                        (dom/input #js {:className   "form-control"
                                        :onChange    #(update-state-find-statement :search-value % owner)
                                        :value       search-value
                                        :placeholder "Find Statement"})
                        (dom/span #js {:className "input-group-btn"}
                                  (dom/button #js {:className "btn btn-primary"
                                                   :type      "button"}
                                              (vlib/fa-icon "fa-search fa-fw" #(find-statement search-value)))))))))

(defn results-view []
  (reify om/IRender
    (render [_]
      (dom/div nil
               (apply dom/div nil
                      (om/build-all item-view (get-search-results)))))))
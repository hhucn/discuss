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

(defn item-view [data owner]
  (reify om/IRender
    (render [_]
      (dom/div #js {:className "bs-callout bs-callout-info"}
               (vlib/safe-html (:text data))
               (dom/span #js {:className "badge pull-right"}
                         (lib/str->int (:distance data)))))))

(defn update-state-find-statement
  "Saves current state into object and sends search request to discussion system."
  [key val owner]
  (vlib/commit-target-value key val owner)
  (find-statement (.. val -target -value)))

(defn issue-selector-view
  "Create option items from each issue."
  [issue owner]
  (dom/option #js {:key (lib/prefix-name (str "discuss-issue-selector-" (:uid issue)))}
              (:title issue)))

(defn form-view [_ owner]
  (reify
    om/IInitState
    (init-state [_]
      {:search-value ""})
    om/IRenderState
    (render-state [_ {:keys [search-value]}]
      (dom/div nil
               (let [issues (lib/get-issues)]
                 (dom/div #js {:className "form-group"}
                          (dom/label nil "Select Issue")
                          (dom/select #js {:className "form-control"
                                           :multiple  true}
                                      (map #(issue-selector-view % owner) issues))))

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
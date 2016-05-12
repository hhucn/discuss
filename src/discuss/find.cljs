(ns discuss.find
  (:require [clojure.walk :refer [keywordize-keys]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [discuss.communication :as com]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]))

;(def data (atom {}))
(def counter (atom 0))

(defn prepare-search-results
  "Extract values and create a list of maps."
  []
  (let [vals (get-in @lib/app-state [:discussion :search :values])
        vals-dict (for [[k v] vals] {k v})]
    (vec vals-dict)))

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
        (lib/update-state-item! :discussion :search (fn [_] res))
        #_(let [foo (prepare-search-results)]
          (println foo)
          (println (count foo)))))))

(defn statement
  "Find related statements to given keywords."
  [keywords]
  (when-not (= keywords "")
    (let [issue 1
          mode 3
          request (str "api/get/statements/" issue "/" mode "/" keywords)]
      (com/ajax-get request {} statement-handler))))

(defn item-view [data owner]
  (reify om/IRender
    (render [_]
      (let [bubble-class nil]
        (dom/li #js {:className bubble-class}
                (dom/div #js {:className "avatar"})
                (dom/p #js {:className "messages"}
                       (vlib/safe-html "foo"))))
      #_(dom/div #js {:id (str (lib/prefix-name "search-item-") (swap! counter inc))}
                 (dom/div nil data)
                 (println data)))))

(defn view []
  (reify om/IRender
    (render [_]
      (dom/div nil
               (dom/div #js {:className "input-group"}
                        (dom/span #js {:className "input-group-addon"}
                                  (vlib/fa-icon "fa-search fa-fw"))
                        (dom/input #js {:className   "form-control"
                                        :onChange    #(statement (.. % -target -value))
                                        :placeholder "Find Statement"}))
               (println (prepare-search-results))
               #_(apply dom/ol #js {:className "foobar"}
                        (om/build-all item-view (lib/get-bubbles)))
               #_(apply dom/div nil
                        (om/build-all item-view vals-dict
                                      ))
               ))))
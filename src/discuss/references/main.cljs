(ns discuss.references.main
  "Handle interaction with already existing references, for example showing
  usages in other statements / arguments or providing a form to use it in the
  own statement."
  (:require [om.core :as om]
            [om.dom :as dom]
            [discuss.communication.main :as com]
            [discuss.config :as config]
            [discuss.components.find :as find]
            [discuss.components.sidebar :as sidebar]
            [discuss.references.lib :as rlib]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.bootstrap :as bs]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]))


;;;; Handlers & Queries
(defn reference-usage-handler
  "Handler to process information about the reference. Store results and change
  view."
  [response]
  (let [res (lib/process-response response)]
    (rlib/save-reference-usages! res)
    (lib/change-view! :reference-usages)))

(defn query-reference-details
  "Show usages of the provided reference."
  [reference-id]
  (let [url (str (get-in config/api [:get :reference-usages]) "/" reference-id)]
    (com/ajax-get url {} reference-usage-handler)))


;;;; Interaction with integrated references
(defn click-reference
  "When clicking on a reference directly in the text, interact with the user to
  give her the choice of what her next steps might be."
  [reference]
  (rlib/save-selected-reference! reference)
  (query-reference-details (:id reference))
  (sidebar/show))


;;;; Views
(defn create-with-reference-view
  "View containing information about which reference has been chosen and give
  possibility to find an entry point into the discussion."
  [data]
  (reify om/IRender
    (render [_]
      (dom/div nil
               (dom/h5 #js {:className "text-center"} (t :references :usages))
               (om/build rlib/current-reference-component {})
               (om/build find/form-view {})
               (om/build find/results-view data)))))

(defn dialog-view
  "Show a dialog to give the user the option to choose, whether she wants to get
  some information about the statement or just wants to construct a new
  statement."
  []
  (reify om/IRender
    (render [_]
      (dom/div #js {:className "text-center"}
               (om/build rlib/current-reference-component {})
               (bs/button-primary #(query-reference-details (:id (rlib/get-selected-reference)))
                                  (t :references :where-used))
               " "
               (dom/button #js {:className "btn btn-primary"
                                :onClick   #(lib/change-view! :reference-create-with-ref)}
                           (t :references :jump))))))

(defn single-reference-usage
  "Show single usage of a reference."
  [data]
  (reify om/IRender
    (render [_]
      (let [{:keys [issue argument author]} data]           ; TODO I think this should be the author of the argument
        (dom/div #js {:className "bs-callout bs-callout-info"}
                 (dom/div #js {:className "pull-right"}
                          (bs/button-default-sm #(com/jump-to-argument (:slug issue) (:uid argument)) (vlib/fa-icon "fa-search")))
                 (dom/a #js {:href    "javascript:void(0)"
                             :onClick #(com/jump-to-argument (:slug issue) (:uid argument))}
                        (dom/strong nil (vlib/safe-html (:text argument))))
                 (dom/div nil (t :common :author) ": " (:nickname author))
                 (dom/div nil (t :common :issue) ": " (:title issue))))))) 

(defn usage-list-view
  "List single usages of reference."
  [data]
  (reify om/IRender
    (render [_]
      (let [issue (:issue data)
            arguments (:arguments data)
            author (:author data)]
        (if (some nil? [issue arguments author])
          (dom/div #js {:className "bs-callout bs-callout-warning"}
                   (dom/strong nil (t :references :usages/not-found-lead))
                   (dom/p nil (t :references :usages/not-found-body)))
          (apply dom/div nil
                 (map #(om/build single-reference-usage
                                 {:issue issue, :argument %, :author author}
                                 (lib/unique-key-dict)) arguments)))))))

(defn usages-view
  "List with details showing the usages of the given reference."
  []
  (reify om/IRender
    (render [_]
      (let [usages (rlib/get-reference-usages)]
        (dom/div nil
                 (vlib/view-header (t :references :usages/view-heading))
                 (om/build rlib/current-reference-component {})
                 (apply dom/div nil
                        (map #(om/build usage-list-view % (lib/unique-key-dict)) usages)))))))

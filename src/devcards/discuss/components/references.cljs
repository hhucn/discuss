(ns devcards.discuss.components.references
  (:require [devcards.core :as dc :refer-macros [defcard defcard-om-next]]
            [discuss.parser :as parser]
            [discuss.views :as views]
            [om.next :as om]
            [sablono.core :as html :refer-macros [html]]
            [discuss.references.integration :as rint]
            [discuss.references.main :as ref]))

(def sample-reference-usages
  [{:reference
    {:uid 5,
     :title
     "But apparently there is an anonymous investor ensuring to pay the running costs for at least the next five years",
     :host "localhost:3449",
     :path "/cards.html",
     :statement-uid 68,
     :author {:uid 2, :nickname "Tobias"}},
    :arguments
    [{:uid 59,
      :text
      "Sie argumentieren, dass E-Autos das autonome Fahren vorantreiben, weil Tesla mutig bestehende Techniken einsetzt und zeigt was sie können",
      :author {:uid 1, :nickname "anonymous"},
      :conclusion "E-Autos das autonome Fahren vorantreiben",
      :premise "Tesla mutig bestehende Techniken einsetzt und zeigt was sie können",
      :issue
      {:title "Elektroautos",
       :slug "elektroautos",
       :summary
       "Elektroautos - Die Autos der Zukunft? Bitte diskutieren Sie dazu.",
       :description "",
       :url "/elektroautos",
       :language "de",
       :date "2017-08-19 11:25:09+00:00"}}
     {:uid 42,
      :text "Irgendein anderes Argument aus einer anderen Diskussion",
      :author {:uid 3, :nickname "Christian"},
      :conclusion "meine conclusion",
      :premise "mein generierter premisegroup-text",
      :issue
      {:title "Anderes Thema",
       :slug nil,
       :summary nil,
       :description "",
       :url "/cat-or-dog",
       :language "en",
       :date "2017-08-19 11:25:10+00:00"}}],
    :statement
    {:uid 68,
     :url "/api/elektroautos/justify/68/agree",
     :text "Tesla mutig bestehende Techniken einsetzt und zeigt was sie können"}}])


;; -----------------------------------------------------------------------------

(defcard buttons
  (html [:div
         [:div.btn.btn-primary {:onClick rint/request-references} "Request References"]
         " "
         [:div.btn.btn-primary {:onClick #(ref/query-reference-details 5)} "Details for Reference 5"]]))

(defcard sample-passage
  "<p id='discuss-text'>Currently, the city council discusses to close the University Park, because of its high running expenses of about $100.000 per year. But apparently there is an anonymous investor ensuring to pay the running costs for at least the next five years. Thanks to this anonymous person, the city does not loose a beautiful park, but this again fires up the discussion about possible savings for the future. </p>")

(defcard-om-next reference-usages-for-all-arguments
  ref/ReferenceUsagesForArgumentsView
  (om/reconciler {:state (first sample-reference-usages)
                  :parser (om/parser {:read parser/read})}))

(defcard-om-next usages-view-complete
  ref/UsagesView
  parser/reconciler)

(defcard-om-next main-view
  views/Discuss
  parser/reconciler)

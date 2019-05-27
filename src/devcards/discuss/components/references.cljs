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
    {:uid 11,
     :title
     "Einige der Hochschulen haben sich in Leitbildern oder Grundsätzen für die Forschung verpflichtet, nur zum Wohle des Menschen zu arbeiten",
     :host "localhost:9500",
     :path "/cards.html",
     :statement-uid 94,
     :author {:uid 40, :nickname "Das Känguru"}},
    :arguments
    [{:uid 81,
      :texts
      {:display
       "Andere Teilnehmer haben gesagt, dass ein solches Leitbild sinnlos ist, weil das viel zu unkonkret ist. Jedoch haben Sie sich dann dafür interessiert, dass es eine grundsätzliche Richtung vorgibt.",
       :conclusion "",
       :premise "es eine grundsätzliche Richtung vorgibt",
       :attacks
       {:conclusion "ein solches Leitbild sinnlos ist",
        :premise "das viel zu unkonkret ist"}},
      :author {:uid 42, :nickname "Das Känguru"},
      :issue
      {:description ".",
       :date "2019-04-10 07:42:03+00:00",
       :slug "public",
       :is_private false,
       :is_read_only false,
       :title "public",
       :summary ".",
       :is_disabled false,
       :language "de",
       :url "/public",
       :is_featured false}}
     {:uid 76,
      :texts
      {:display
       "Sie argumentieren, dass ein solches Leitbild sinnlos ist, weil das viel zu unkonkret ist",
       :conclusion "ein solches Leitbild sinnlos ist",
       :premise "das viel zu unkonkret ist",
       :attacks {}},
      :author {:uid 40, :nickname "Der Pinguin"},
      :issue
      {:description ".",
       :date "2019-04-10 07:42:03+00:00",
       :slug "public",
       :is_private false,
       :is_read_only false,
       :title "public",
       :summary ".",
       :is_disabled false,
       :language "de",
       :url "/public",
       :is_featured false}}],
    :statement
    {:uid 94,
     :url "/api/public/justify/94/agree",
     :text "das viel zu unkonkret ist"}}])


;; -----------------------------------------------------------------------------

(defcard buttons
  (html [:div
         [:div.btn.btn-primary {:onClick rint/request-references} "Request References"]
         " "
         [:div.btn.btn-primary {:onClick #(ref/query-reference-details 5)} "Details for Reference 5"]]))

(defcard sample-passage
  "<p id='discuss-text'>Currently, the city council discusses to close the University Park, because of its high running expenses of about $100.000 per year. But apparently there is an anonymous investor ensuring to pay the running costs for at least the next five years. Thanks to this anonymous person, the city does not loose a beautiful park, but this again fires up the discussion about possible savings for the future. </p>")

(defcard real-discussion
  "Im Jahr 2017 erteilte das Bundesministerium für Verteidigung für knapp 79 Millionen Euro Forschungsaufträge, 2016 für etwas mehr als 53 Millionen. Der größte Teil der Ausgaben fließt Instituten der F raunhofer-Gesellschaft zu. Auch das Deutsche Zentrum für Luft- und Raumfahrt steht auf der Liste ganz oben.Darüber hinaus gibt es 24 Hochschulen und Universitäten im gesamten Bundesgebiet, die 2018 für Forschung Geld vom Verteidigungsministerium erhalten haben. Das geht aus der Antwort der Bundesregierung auf die Kleine Anfrage der Linkspartei hervor, die dem ARD-Hauptstadtstudio exklusiv vorliegt."
  "Einige der Hochschulen haben sich in Leitbildern oder Grundsätzen für die Forschung verpflichtet, nur zum Wohle des Menschen zu arbeiten . Das sind etwa die Universitäten in Kiel, Hannover, Duisburg-Essen und Erlangen-Nürnberg, wie eine Abfrage des ARD-Hauptstadtstudios ergab.")


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

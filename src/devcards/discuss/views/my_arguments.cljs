(ns devcards.discuss.views.my-arguments
  (:require [devcards.core :as dc :refer-macros [defcard-om-next]]
            [discuss.parser :as parser]
            [discuss.eden.views :as eviews]))

(comment
  (lib/host-eden! "https://eden.faz.discuss.cs.uni-duesseldorf.de")
  (eajax/search-arguments-by-author "Christian")
  (cljs.pprint/pprint
   (:eden/arguments @(om/app-state discuss.parser/reconciler)))
  )

(def ^:private sample-eden-arguments
  '({:link
     {:author {:dgep-native true, :name "Christian", :id 3},
      :created "1559550397",
      :type :support,
      :source
      {:aggregate-id "eden.faz.discuss.cs.uni-duesseldorf.de:443",
       :entity-id "41",
       :version 1},
      :destination
      {:aggregate-id "eden.faz.discuss.cs.uni-duesseldorf.de:443",
       :version 1,
       :entity-id "39"},
      :identifier
      {:aggregate-id "eden.faz.discuss.cs.uni-duesseldorf.de:443",
       :entity-id "link_34",
       :version 1},
      :delete-flag false},
     :premise
     {:content
      {:text "then we will have more money to expand out pedestrian zone",
       :created nil,
       :author {:dgep-native true, :name "anonymous", :id 1}},
      :identifier
      {:aggregate-id "eden.faz.discuss.cs.uni-duesseldorf.de:443",
       :entity-id "41",
       :version 1},
      :predecessors [],
      :delete-flag false,
      :references []},
     :conclusion
     {:content
      {:text
       "reducing the number of street festivals can save up to $50.000 a year",
       :created nil,
       :author {:dgep-native true, :name "anonymous", :id 1}},
      :identifier
      {:aggregate-id "eden.faz.discuss.cs.uni-duesseldorf.de:443",
       :entity-id "39",
       :version 1},
      :predecessors [],
      :delete-flag false,
      :references []}}
    {:link
     {:author {:dgep-native true, :name "Christian", :id 3},
      :created "1559550399",
      :type :support,
      :source
      {:aggregate-id "eden.faz.discuss.cs.uni-duesseldorf.de:443",
       :entity-id "89",
       :version 1},
      :destination
      {:aggregate-id "eden.faz.discuss.cs.uni-duesseldorf.de:443",
       :version 1,
       :entity-id "88"},
      :identifier
      {:aggregate-id "eden.faz.discuss.cs.uni-duesseldorf.de:443",
       :entity-id "link_73",
       :version 1},
      :delete-flag false},
     :premise
     {:content
      {:text "das Leitungswasser an der Uni grausam ist",
       :created nil,
       :author {:dgep-native true, :name "Christian", :id 3}},
      :identifier
      {:aggregate-id "eden.faz.discuss.cs.uni-duesseldorf.de:443",
       :entity-id "89",
       :version 1},
      :predecessors [],
      :delete-flag false,
      :references
      [{:text "größte Teil der Ausgaben fließt Instituten der F",
        :host "faz.discuss.cs.uni-duesseldorf.de",
        :path "/zivilklausel.html"}]},
     :conclusion
     {:content
      {:text "wir einen Wasserspender anschaffen sollten",
       :created nil,
       :author {:dgep-native true, :name "Christian", :id 3}},
      :identifier
      {:aggregate-id "eden.faz.discuss.cs.uni-duesseldorf.de:443",
       :entity-id "88",
       :version 1},
      :predecessors [],
      :delete-flag false,
      :references []}}
    {:link
     {:author {:dgep-native true, :name "Christian", :id 3},
      :created "1559550397",
      :type :support,
      :source
      {:aggregate-id "eden.faz.discuss.cs.uni-duesseldorf.de:443",
       :entity-id "39",
       :version 1},
      :destination
      {:aggregate-id "eden.faz.discuss.cs.uni-duesseldorf.de:443",
       :version 1,
       :entity-id "36"},
      :identifier
      {:aggregate-id "eden.faz.discuss.cs.uni-duesseldorf.de:443",
       :entity-id "link_32",
       :version 1},
      :delete-flag false},
     :premise
     {:content
      {:text
       "reducing the number of street festivals can save up to $50.000 a year",
       :created nil,
       :author {:dgep-native true, :name "anonymous", :id 1}},
      :identifier
      {:aggregate-id "eden.faz.discuss.cs.uni-duesseldorf.de:443",
       :entity-id "39",
       :version 1},
      :predecessors [],
      :delete-flag false,
      :references []},
     :conclusion
     {:content
      {:text "the city should reduce the number of street festivals",
       :created nil,
       :author {:dgep-native true, :name "Christian", :id 3}},
      :identifier
      {:aggregate-id "eden.faz.discuss.cs.uni-duesseldorf.de:443",
       :entity-id "36",
       :version 1},
      :predecessors [],
      :delete-flag false,
      :references []}}))

(defcard-om-next show-arguments
  eviews/ShowArguments
  parser/reconciler)

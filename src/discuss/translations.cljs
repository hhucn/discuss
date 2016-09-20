(ns discuss.translations
  (:require [discuss.utils.common :as lib]))

(def translations {:de {:common     {:and              "und"
                                     :author           "Autor"
                                     :back             "Zurück"
                                     :chars-remaining  "Zeichen verbleibend"
                                     :hello            "Hallo"
                                     :issue            "Diskussionsthema"
                                     :login            "Login"
                                     :logout           "Logout"
                                     :options          "Einstellungen"
                                     :save             "Speichern"
                                     :show-discuss     "Zeige discuss"
                                     :start-discussion "Starte die Diskussion"}
                        :discussion {:add-argument "Ein neues Argument hinzufügen"
                                     :current      "Aktuelle Diskussion"
                                     :restart      "Neustarten"
                                     :submit       "Abschicken"}
                        :references {:jump       "Springe in die Diskussion"
                                     :usages     "In welchen Argumenten wird dieser Textausschnitt verwendet?"
                                     :where-used "Wo wird diese Referenz verwendet?"}}
                   :en {:common     {:and              "and"
                                     :author           "Author"
                                     :back             "Back"
                                     :chars-remaining  "characters remaining"
                                     :hello            "Hello"
                                     :issue            "Issue"
                                     :login            "Login"
                                     :logout           "Logout"
                                     :options          "Options"
                                     :save             "Save"
                                     :show-discuss     "Show discuss"
                                     :start-discussion "Start Discussion"}
                        :discussion {:add-argument "Add a new argument"
                                     :current      "Current Discussion"
                                     :restart      "Restart"
                                     :submit       "Submit"}
                        :references {:jump       "Jump into the discussion"
                                     :usages     "In which arguments has this reference been used?"
                                     :where-used "Where has this reference been used?"}}})

(defn- prepend-translation
  "Lookup given key and prepend some string to it."
  [group key prepend]
  (str prepend (get-in translations [(lib/get-language) group key])))

(defn translate
  "Get translation string according to currently configured language."
  ([group key & options]
   (let [option (first options)]
     (cond
       (= :space option) (prepend-translation group key " ")
       :default (prepend-translation group key "")))))
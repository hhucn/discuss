(ns discuss.translations
  (:require [discuss.utils.common :as lib]))

(def available [[:de "deutsch"]
                [:en "english"]]) 

(def translations {:de {:common     {:and              "und"
                                     :author           "Autor"
                                     :back             "Zurück"
                                     :chars-remaining  "Zeichen verbleibend"
                                     :close            "Schließen"
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
                        :errors     {:login "Login fehlgeschlagen. Vermutlich sind die Zugangsdaten nicht korrekt."}
                        :login      {:nickname "Benutzername"
                                     :password "Passwort"}
                        :nav        {:home "Start"
                                     :find "Suchen"}
                        :options    {:heading "Einstellungen"
                                     :lang    "Interface-Sprache"}
                        :references {:jump       "Springe in die Diskussion"
                                     :usages     "In welchen Argumenten wird dieser Textausschnitt verwendet?"
                                     :where-used "Wo wird diese Referenz verwendet?"
                                     :clipboard  "Ziehe diese Referenzen in das Textfeld beim Erzeugen eines neuen Arguments, um die Referenz zu nutzen."}}
                   :en {:common     {:and              "and"
                                     :author           "Author"
                                     :back             "Back"
                                     :chars-remaining  "characters remaining"
                                     :close            "Close"
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
                        :errors     {:login "Could not login. Maybe your credentials are wrong."}
                        :login      {:nickname "Nickname"
                                     :password "Password"}
                        :nav        {:home "Home"
                                     :find "Find"}
                        :options    {:heading "Options"
                                     :lang    "Language of Interface"}
                        :references {:jump       "Jump into the discussion"
                                     :usages     "In which arguments has this reference been used?"
                                     :where-used "Where has this reference been used?"
                                     :clipboard  "Drag these references into the input field when creating a new argument."}}})

(defn- prepend-translation
  "Lookup given key and prepend some string to it."
  [group key prepend]
  (str prepend (get-in translations [(lib/language) group key])))

(defn translate
  "Get translation string according to currently configured language."
  ([group key & options]
   (let [option (first options)]
     (cond
       (= :space option) (prepend-translation group key " ")
       :default (prepend-translation group key "")))))

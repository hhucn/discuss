(ns discuss.translations
  (:require [discuss.utils.common :as lib]))

(def available [[:de "deutsch"]
                [:en "english"]])

(def translations
  {:de {:common     {:and              "und"
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
        :find       {:find-statement "Finde Aussage in Diskussion"}
        :login      {:nickname "Benutzername"
                     :password "Passwort"
                     :hhu-ldap "Wir benutzen das Personenverzeichnug der Heinrich-Heine-Universität Düsseldorf. Alle Daten und Informationen werden natürlich SSL-verschlüsselt gesendet und nach Erhalt unter keinen Umständen an Dritte weitergegeben."}
        :nav        {:home "Start"
                     :find "Suchen"}
        :options    {:heading "Einstellungen"
                     :lang "Interface-Sprache"}
        :references {:jump "Springe in die Diskussion"
                     :usages/view-heading "In welchen Argumenten wird dieser Textausschnitt verwendet?"
                     :usages/not-found-lead "Argument konnte nicht gefunden werden"
                     :usages/not-found-body "Vielleicht wurden die mit dieser Referenz verknüpften Argumente entfernt"
                     :where-used "Wo wird diese Referenz verwendet?"
                     :find-statement "Finde Aussage in der Diskussion"
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
                     :current "Current Discussion"
                     :restart "Restart"
                     :submit "Submit"}
        :errors     {:login "Could not login. Maybe your credentials are wrong."}
        :find       {:find-statement "Find statement in the discussion"}
        :login      {:nickname "Nickname"
                     :password "Password"
                     :hhu-ldap "We are using the Register of Persons of the Heinrich-Heine-University Düsseldorf. All data and information are of course sent SSL encrypted and will never be passed on to any third parties after receipt."}
        :nav        {:home "Home"
                     :find "Find"}
        :options    {:heading "Options"
                     :lang "Language of Interface"}
        :references {:jump "Jump into the discussion"
                     :usages/view-heading "In which arguments has this reference been used?"
                     :usages/not-found-lead "No assigned arguments found"
                     :usages/not-found-body "Maybe the assigned arguments have been removed"
                     :where-used "Where has this reference been used?"
                     :clipboard "Drag these references into the input field when creating a new argument"}}})

(defn- prepend-translation
  "Lookup given key and prepend some string to it."
  [group key prepend]
  (str prepend (get-in translations [(lib/language) group key])))

(defn translate
  "Get translation string according to currently configured language."
  ([group key option]
   (cond
     (= :space option) (prepend-translation group key " ")
     :default (prepend-translation group key "")))
  ([group key]
   (translate group key :default)))

(ns discuss.translations
  (:require [discuss.utils.common :as lib]
            [cljs.spec.alpha :as s]))

(def available [[:de "deutsch"]
                [:en "english"]])

(def translations
  {:de {:common     {:and              "und"
                     :author           "Autor"
                     :back             "Zurück"
                     :because          "weil"
                     :chars-remaining  "Zeichen verbleibend"
                     :close            "Schließen"
                     :hello            "Hallo"
                     :issue            "Diskussionsthema"
                     :login            "Login"
                     :logout           "Logout"
                     :options          "Einstellungen"
                     :save             "Speichern"
                     :show-discuss     "Zeige discuss"
                     :start-discussion "Starte die Diskussion"
                     :that             "dass"}
        :clipboard  {:heading "Clipboard"
                     :instruction "Ziehe diese Referenzen in das Textfeld beim Erzeugen eines neuen Arguments, um die Referenz zu nutzen."}
        :discussion {:add-position "Nichts von all dem. Ich habe eine andere Idee"
                     :add-position-heading "Mein Vorschlag wäre, dass"
                     :add-position-placeholder "wir dieses oder jenes machen sollten"
                     :add-reason-placeholder "aus diesen und jenen Gründen"
                     :current      "Aktuelle Diskussion"
                     :restart      "Neustarten"
                     :submit       "Abschicken"}
        :errors     {:login "Login fehlgeschlagen. Vermutlich sind die Zugangsdaten nicht korrekt."}
        :find       {:statement "Finde Aussage in Diskussion"
                     :hits "Ergebnisse"}
        :login      {:nickname "Benutzername"
                     :password "Passwort"
                     :hhu-ldap "Wir benutzen das Personenverzeichnug der Heinrich-Heine-Universität Düsseldorf. Alle Daten und Informationen werden natürlich SSL-verschlüsselt gesendet und nach Erhalt unter keinen Umständen an Dritte weitergegeben."
                     :item "Klicke hier, damit du dich einloggen und eine neue Aussage hinzufügen kannst"}
        :nav        {:home "Start"
                     :find "Suchen"}
        :options    {:current "Aktuell"
                     :default "Standard"
                     :heading "Einstellungen"
                     :lang "Interface-Sprache"
                     :new-route "Neue Route"
                     :reset "Zurücksetzen"
                     :routes "Routen"
                     :save "Speichere"}
        :references {:jump "Springe in die Diskussion"
                     :usages/view-heading "In welchen Aussagen wird dieser Textausschnitt verwendet?"
                     :usages/not-found-lead "Argument konnte nicht gefunden werden"
                     :usages/not-found-body "Vielleicht wurden die mit dieser Referenz verknüpften Argumente entfernt"
                     :where-used "Wo wird diese Referenz verwendet?"
                     :find-statement "Finde Aussage in der Diskussion"
                     :clipboard  "Ziehe diese Referenzen in das Textfeld beim Erzeugen eines neuen Arguments, um die Referenz zu nutzen."
                     :ask-to-add "Möchten Sie Ihre Aussage durch eine Referenz von dieser Seite stützen? Dann markieren Sie einfach einen Teil des Textes mit der Maus."
                     :disabled/tooltip "Du kannst dieses Feld nicht direkt modifizieren. Bitte markiere die gewünschte Stelle direkt auf der Webseite. Dieses Feld füllt sich dann automatisch."}
        :search      {:reuse "Statement auswählen"
                      :origin "Herkunft"
                      :author "Autor"}}
   :en {:common     {:and              "and"
                     :author           "Author"
                     :back             "Back"
                     :because          "because"
                     :chars-remaining  "characters remaining"
                     :close            "Close"
                     :hello            "Hello"
                     :issue            "Issue"
                     :login            "Login"
                     :logout           "Logout"
                     :options          "Options"
                     :save             "Save"
                     :show-discuss     "Show discuss"
                     :start-discussion "Start Discussion"
                     :that             "that"}
        :clipboard  {:heading "Clipboard"
                     :instruction "Drag a reference into the text-field on new argument creation to link the reference with your argument."}
        :discussion {:add-position  "Neither of the above, I have a different idea"
                     :add-position-heading "My suggestion would be that"
                     :add-position-placeholder "we should do this or that"
                     :add-reason-placeholder "of this reason"
                     :current "Current Discussion"
                     :restart "Restart"
                     :submit "Submit"}
        :errors     {:login "Could not login. Maybe your credentials are wrong."}
        :find       {:statement "Find statement in the discussion"
                     :hits "Results"}
        :login      {:nickname "Nickname"
                     :password "Password"
                     :hhu-ldap "We are using the Register of Persons of the Heinrich-Heine-University Düsseldorf. All data and information are of course sent SSL encrypted and will never be passed on to any third parties after receipt."
                     :item "Click here to authenticate and to be able to submit your own statements"}
        :nav        {:home "Home"
                     :find "Find"}
        :options    {:current "Current"
                     :default "Default"
                     :heading "Options"
                     :lang "Interface Language"
                     :new-route "Set new Route"
                     :reset "Reset to Defaults"
                     :routes "Routes"
                     :save "Save"}
        :references {:jump "Jump into the discussion"
                     :usages/view-heading "In which statements has this reference been used?"
                     :usages/not-found-lead "No assigned arguments found"
                     :usages/not-found-body "Maybe the assigned arguments have been removed"
                     :where-used "Where has this reference been used?"
                     :clipboard "Drag these references into the input field when creating a new argument"
                     :ask-to-add "Do you want to add a reference from this site to your statement? Just select the desired text-passage and it will be inserted in this field."
                     :disabled/tooltip "You can't modify this field. Please select the appropriate text-passage from the website. The selection will be automatically added to this field."}
        :search      {:reuse "Select Statement"
                      :origin "Origin"
                      :author "Author"}}})

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

(s/fdef translate
        :args (s/or :option (s/cat :group keyword? :key keyword? :option keyword?)
                    :no-option (s/cat :group keyword? :key keyword?))
        :ret string?)

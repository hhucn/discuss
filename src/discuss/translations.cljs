(ns discuss.translations
  (:require [discuss.utils.common :as lib]
            [cljs.spec.alpha :as s]))

(def available [[:de "deutsch"]
                [:en "english"]])

(def translations
  {:de {:common     {:and              "und"
                     :argue/that       "sagt, dass"
                     :author           "Autor"
                     :back             "Zurück"
                     :because          "weil"
                     :chars-remaining  "noch mind. %d Zeichen eingeben"
                     :close            "Schließen"
                     :delete           "Löschen"
                     :hello            "Hallo"
                     :issue            "Diskussionsthema"
                     :login            "Login"
                     :logout           "Logout"
                     :options          "Einstellungen"
                     :select           "Auswählen"
                     :save             "Speichern"
                     :show-discuss     "Zeige discuss"
                     :start-discussion "Starte die Diskussion"
                     :that             "dass"}
        :clipboard  {:heading "Zwischenablage"
                     :instruction "Wählen Sie eine Referenz aus dieser Liste aus, um beim Erzeugen eines neuen Arguments diese Referenz mit dem neuen Argument zu verknüpfen."}
        :create/argument {:header "Erzeugen Sie ein Argument zu einer Textstelle"
                          :short "Neues Argument zur Referenz erstellen"
                          :lead "Sie können hier Position zu einer Textstelle beziehen."
                          :logged-in "Füllen Sie dazu die folgenden Felder aus."
                          :not-logged-in "Aber vorher müssen Sie sich einlogggen."}
        :discussion {:add-position "Nichts von all dem. Ich habe eine andere Idee"
                     :add-position-heading "Ich denke, "
                     :add-position-placeholder "wir dieses oder jenes machen sollten"
                     :add-reason-placeholder "aus diesen und jenen Gründen"
                     :bubble/congrats "Sie haben das Ende der Diskussion erreicht. Suchen Sie sich einen anderen Einstiegspunkt in dem Artikel, um weiter zu diskutieren."
                     :current      "Aktuelle Diskussion"
                     :reference/missing "Sie müssen sich auf eine Textstelle beziehen"
                     :restart      "Neustarten"
                     :submit       "Abschicken"}
        :eden       {:overview "Meine Argumente"
                     :overview/lead "Finden Sie hier Ihre Argumente, welche Sie in verschiedenen Diskussionen eingeführt haben und springen Sie direkt in die Diskussionen hinein."
                     :arguments/construct "Neues Argument erstellen"
                     :arguments/not-found "Es konnten noch keine Argumente von Ihnen gefunden werden. Ein guter Zeitpunkt eine Diskussion zu wählen und mit anderen Benutzern zu diskutieren!"
                     :arguments/show "Meine Argumente anzeigen"}
        :errors     {:login "Login fehlgeschlagen. Vermutlich sind die Zugangsdaten nicht korrekt."}
        :find       {:statement "Finde Aussage in Diskussion"
                     :hits "Ergebnisse"}
        :login      {:nickname "Benutzername"
                     :password "Passwort"
                     :hhu-ldap "Wir benutzen das Personenverzeichnis der Heinrich-Heine-Universität Düsseldorf. Alle Daten und Informationen werden natürlich SSL-verschlüsselt gesendet und nach Erhalt unter keinen Umständen an Dritte weitergegeben."
                     :item "Hier klicken, damit Sie sich einloggen und eine neue Aussage hinzufügen können"}
        :nav        {:home "Start"
                     :find "Suchen"
                     :eden "Meine Argumente"}
        :options    {:current "Aktuell"
                     :default "Standard"
                     :delete "Löschen"
                     :heading "Einstellungen"
                     :lang "Interface-Sprache"
                     :new-route "Neue Route"
                     :reconnect "Neu verbinden"
                     :reset "Zurücksetzen"
                     :routes "Routen"
                     :save "Speichere"}
        :references {:jump "Springe in die Diskussion"
                     :save/to-clipboard "In Zwischenablage"
                     :usages/view-heading "Interaktionen mit dem Artikel"
                     :usages/lead "Beziehen Sie sich hier auf die ausgewählte Referenz oder schauen Sie sich an, wo diese Referenz bereits in der Diskussion verwendet wurde."
                     :usages/not-found-lead "Argument konnte nicht gefunden werden"
                     :usages/not-found-body "Vielleicht wurden die mit dieser Referenz verknüpften Argumente entfernt"
                     :usages/list "Hier ist eine Liste der bisherigen Verwendungen dieser Textstelle in anderen Argumenten"
                     :where-used "Wo wird diese Referenz verwendet?"
                     :find-statement "Finde Aussage in der Diskussion"
                     :clipboard  "Ziehen Sie diese Referenzen in das Textfeld beim Erzeugen eines neuen Arguments hinein, um die Referenz zu nutzen."
                     :ask-to-add "Möchten Sie Ihre Aussage durch eine Referenz von dieser Seite stützen? Dann markieren Sie einfach einen Teil des Textes mit der Maus."
                     :has-to-add "Um sich auf eine Stelle im Artikel zu beziehen, müssen Sie die Stelle mit der Maus markieren. Dann erscheint der Text in diesem Textfeld."
                     :disabled/tooltip "Dieses Feld kann nicht direkt modifiziert werden. Bitte markieren Sie die gewünschte Stelle direkt auf der Webseite. Dieses Feld füllt sich dann automatisch."}
        :search      {:reuse "Statement auswählen"
                      :origin "Herkunft"
                      :author "Autor"}
        :tooltip     {:discuss/start "Argument erzeugen"}
        :undercut    {:text "Laut %s kann man die Aussage, dass \"%s\", nicht damit begründen, dass \"%s\", weil %s."}}
   :en {:common     {:and              "and"
                     :argue/that       "says that"
                     :author           "Author"
                     :back             "Back"
                     :because          "because"
                     :chars-remaining  "add at least %d more character(s)"
                     :close            "Close"
                     :delete           "Delete"
                     :hello            "Hello"
                     :issue            "Issue"
                     :login            "Login"
                     :logout           "Logout"
                     :options          "Options"
                     :select           "Select"
                     :save             "Save"
                     :show-discuss     "Show discuss"
                     :start-discussion "Start Discussion"
                     :that             "that"}
        :clipboard  {:heading "Clipboard"
                     :instruction "Select a reference from this list on new argument creation to link the reference with your argument."}
        :create/argument {:header "Create an argument with a text reference"
                          :short "Create Argument with Reference"
                          :lead "You can refer position to a text passage here."
                          :logged-in "Please fill in the following fields."
                          :not-logged-in "But first you need to login."}
        :discussion {:add-position  "Neither of the above, I have a different idea"
                     :add-position-heading "I think that"
                     :add-position-placeholder "we should do this or that"
                     :add-reason-placeholder "of this reason"
                     :bubble/congrats "You've reached the end of the current discussion. Find a different position in the article to start over again."
                     :current "Current Discussion"
                     :reference/missing "You need to refer to a text passage"
                     :restart "Restart"
                     :submit "Submit"}
        :eden       {:overview "My Arguments"
                     :overview/lead "You provided these arguments in your discussions. Create a list of them and directly jump into the discussion."
                     :arguments/construct "Construct new Argument"
                     :arguments/not-found "Currently there are no arguments from your account. A good starting point to find a discussion and start adding your own arguments!"
                     :arguments/show "Show my Arguments"}
        :errors     {:login "Could not login. Maybe your credentials are wrong."}
        :find       {:statement "Find statement in the discussion"
                     :hits "Results"}
        :login      {:nickname "Nickname"
                     :password "Password"
                     :hhu-ldap "We are using the Register of Persons of the Heinrich-Heine-University Düsseldorf. All data and information are of course sent SSL encrypted and will never be passed on to any third parties after receipt."
                     :item "Click here to authenticate and to be able to submit your own statements"}
        :nav        {:home "Home"
                     :eden "My Arguments"
                     :find "Find"}
        :options    {:current "Current"
                     :default "Default"
                     :delete "Delete"
                     :heading "Options"
                     :lang "Interface Language"
                     :new-route "Set new Route"
                     :reconnect "Reconnect"
                     :reset "Reset to Defaults"
                     :routes "Routes"
                     :save "Save"}
        :references {:jump "Jump into the discussion"
                     :save/to-clipboard "Save into Clipboard"
                     :usages/view-heading "Interacting with the Article"
                     :usages/lead "Create your own argument based on the text selection or take a look at those arguments, which already refer to this position."
                     :usages/not-found-lead "No assigned arguments found"
                     :usages/not-found-body "Maybe the assigned arguments have been removed"
                     :usages/list "Here you can find a list of those arguments, which refer to the selected text passage"
                     :where-used "Where has this reference been used?"
                     :ask-to-add "Do you want to add a reference from this site to your statement? Just select the desired text-passage and it will be inserted in this field."
                     :has-to-add "To refer position to a passage in the article, you have to select the desired text-passage with your mouse selection."
                     :disabled/tooltip "You can't modify this field. Please select the appropriate text-passage from the website. The selection will be automatically added to this field."}
        :search      {:reuse "Select Statement"
                      :origin "Origin"
                      :author "Author"}
        :tooltip     {:discuss/start "Create Argument"}
        :undercut    {:text "According to %s, the statement that \"%s\" can not be explained by the fact that \"%s\", because %s."}}})

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

(ns discuss.translations
  (:require [discuss.utils.common :as lib]))

(def translations {:de {:common     {
                                     :and             "und"
                                     :author          "Autor"
                                     :back            "Zurück"
                                     :chars-remaining "Zeichen verbleibend"
                                     :hello           "Hallo"
                                     :issue           "Diskussionsthema"
                                     }
                        :discussion {:add-argument "Ein neues Argument hinzufügen"
                                     :current      "Aktuelle Diskussion"
                                     :restart      "Neustarten"
                                     :submit       "Abschicken"}
                        :references {
                                     :jump       "Springe in die Diskussion"
                                     :usages     "In welchen Argumenten wird dieser Textausschnitt verwendet?"
                                     :where-used "Wo wird diese Referenz verwendet?"
                                     }}
                   :en {:common     {:and             "and"
                                     :author          "Author"
                                     :back            "Back"
                                     :chars-remaining "characters remaining"
                                     :hello           "Hello"
                                     :issue           "Issue"
                                     }
                        :discussion {:add-argument "Add a new argument"
                                     :current      "Current Discussion"
                                     :restart      "Restart"
                                     :submit       "Submit"}
                        :references {
                                     :jump       "Jump into the discussion"
                                     :usages     "In which arguments has this reference been used?"
                                     :where-used "Where has this reference been used?"
                                     }}})

(defn translate
  "Get translation string according to currently configured language."
  [group key]
  (get-in translations [(lib/get-language) group key]))

(translate :example :foo)


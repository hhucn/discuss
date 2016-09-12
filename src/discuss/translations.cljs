(ns discuss.translations
  (:require [discuss.utils.common :as lib]))

(def translations {:de {:common     {:and             "und"
                                     :back            "Zurück"
                                     :chars-remaining "Zeichen verbleibend"
                                     :hello           "Hallo"}
                        :discussion {:add-argument "Ein neues Argument hinzufügen"
                                     :current "Aktuelle Diskussion"
                                     :restart "Neustarten"
                                     :submit  "Abschicken"}}
                   :en {:common     {:and             "and"
                                     :back            "Back"
                                     :chars-remaining "characters remaining"
                                     :hello           "Hello"}
                        :discussion {:add-argument "Add a new argument"
                                     :current "Current Discussion"
                                     :restart "Restart"
                                     :submit  "Submit"}}})

(defn translate
  "Get translation string according to currently configured language."
  [group key]
  (get-in translations [(lib/get-language) group key]))

(translate :example :foo)


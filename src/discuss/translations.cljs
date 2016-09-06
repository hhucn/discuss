(ns discuss.translations
  (:require [discuss.utils.common :as lib]))

(def translations {:de {:views {:submit "Abschicken"}}
                   :en {:views {:submit "Submit"}}})

(defn translate
  "Get translation string according to currently configured language."
  [group key]
  (get-in translations [(lib/get-language) group key]))

(translate :example :foo)


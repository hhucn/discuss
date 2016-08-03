(ns discuss.utils.bootstrap
  "Reusable components, which use twitter bootstrap to reduce redundancy."
  (:require [om.dom :as dom]
            [discuss.utils.common :as lib]))

(defn button-primary
  "Create dom element of a bootstrap primary button."
  [fn & strs]
  (dom/button #js {:className "btn btn-primary"
                   :onClick   fn
                   :key       (lib/get-unique-key)}
              strs))
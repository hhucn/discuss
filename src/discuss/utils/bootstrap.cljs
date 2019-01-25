(ns discuss.utils.bootstrap
  "Reusable components, which use twitter bootstrap to reduce redundancy."
  (:require [om.dom :as dom]
            [discuss.utils.common :as lib]))

(defn button
  "Create dom element of a bootstrap button."
  [fn class & strs]
  (dom/button #js {:className (str "btn " class)
                   :onClick   fn
                   :key       (lib/get-unique-key)}
              strs))

(defn button-primary
  "Create dom element of a bootstrap primary button."
  [fn & strs] (button fn "btn-primary" strs))

(defn button-default
  "Create dom element of a bootstrap default button."
  [fn & strs] (button fn "btn-default" strs))

(defn button-default-sm
  "Create dom element of a bootstrap default small button."
  [fn & strs] (button fn "btn-default btn-sm" strs))

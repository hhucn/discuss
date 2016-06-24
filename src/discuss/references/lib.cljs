(ns discuss.references.lib
  (:require [discuss.utils.common :as lib]))

(defn save-selected-reference!
  "Saves the currently clicked reference for further processing."
  [ref]
  (lib/update-state-item! :reference-usages :selected-reference (fn [_] ref)))

(defn get-selected-reference
  "Returns the currently selected reference."
  []
  (get-in @lib/app-state [:reference-usages :selected-reference]))

(defn save-selected-statement!
  "Saves the currently selected statement for further processing."
  [statement]
  (lib/update-state-item! :reference-usages :selected-statement (fn [_] statement)))

(defn get-selected-statement
  "Returns the currently selected statement from reference usages."
  []
  (get-in @lib/app-state [:reference-usages :selected-statement]))

(defn get-reference-usages
  "Return list of reference usages, which were previously stored in the app-state.
   TODO: optimize"
  []
  (get-in @lib/app-state [:common :reference-usages]))
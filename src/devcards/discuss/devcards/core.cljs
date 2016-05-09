(ns discuss.devcards.core
  (:require [devcards.core :as dc :refer-macros [defcard deftest]]
            [cljs.test :refer-macros [is async]]
            [om.dom :as dom]
            [discuss.extensions]
            [discuss.debug :as debug]
            [discuss.lib :as lib]
            [discuss.views :as views]))

(enable-console-print!)

(defcard main
         "## Main discuss Component"
         (dc/om-root views/main-view) lib/app-state)

(defcard debug
         (dc/om-root debug/debug-view) lib/app-state)

(defcard global-state
         @lib/app-state)

(deftest bar
         "baz")
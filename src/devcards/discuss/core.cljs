(ns devcards.discuss.core
  (:require [devcards.core :as dc :refer-macros [defcard defcard-om-next]]
            [sablono.core :as html :refer-macros [html]]
            [discuss.communication.auth :as auth]
            [devcards.discuss.add-content]
            [devcards.discuss.atoms]
            [devcards.discuss.components.avatar]
            [devcards.discuss.components.create-argument]
            [devcards.discuss.components.issue-selector]
            [devcards.discuss.components.loader]
            [devcards.discuss.components.options]
            [devcards.discuss.components.references]
            [devcards.discuss.components.search]
            [devcards.discuss.eden]
            [devcards.discuss.molecules]
            [devcards.discuss.views]
            [devcards.discuss.views.alerts]
            [devcards.discuss.views.login]))

(enable-console-print!)

(devcards.core/start-devcard-ui!)

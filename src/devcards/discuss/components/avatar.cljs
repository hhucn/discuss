(ns devcards.discuss.components.avatar
  (:require [devcards.core :as dc :refer-macros [defcard-om-next]]
            [discuss.parser :as parser]
            [discuss.components.avatar :as avatar]
            [om.next :as om]
            [discuss.views :as views]))

(defcard-om-next avatar-logged-in
  avatar/Avatar
  (om/reconciler {:state (merge @(om/app-state parser/reconciler)
                                {:user/logged-in? true})
                  :parser (om/parser {:read parser/read})}))

(defcard-om-next avatar-non-string-input
  avatar/Avatar
  (om/reconciler {:state (merge @(om/app-state parser/reconciler)
                                {:user/avatar nil
                                 :user/logged-in? true})
                  :parser (om/parser {:read parser/read})}))

(defcard-om-next avatar-not-logged-in
  avatar/Avatar
  (om/reconciler {:state (merge @(om/app-state parser/reconciler)
                                {:user/logged-in? false})
                  :parser (om/parser {:read parser/read})}))

(defcard-om-next main-view
  views/MainView
  (om/reconciler {:state (merge @(om/app-state parser/reconciler)
                                {:user/logged-in? true})
                  :parser (om/parser {:read parser/read})}))

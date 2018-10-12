(ns devcards.discuss.views.login
  (:require [devcards.core :as dc :refer-macros [defcard-om-next]]
            [discuss.parser :as parser]
            [discuss.views.login :as vlogin]))

(defcard-om-next login-view
  vlogin/LoginForm
  parser/reconciler)

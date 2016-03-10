(ns discuss.communication
  (:require [ajax.core :refer [GET]]
            [discuss.config :as config]
            [discuss.debug :as debug]
            [discuss.history :as history]
            [discuss.lib :as lib]))

;; Auxiliary functions
(defn make-url
  "Add suffix if not provided."
  [url]
  (str (:host config/api) url))

;; Discussion-related functions
(defn add-new-start-statement []
  (lib/show-add-form))

;; AJAX stuff
(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text))
  (lib/loading? false))

(defn token-header
  "Return token header for ajax request if user is logged in."
  []
  (when (lib/logged-in?)
    {"X-Messaging-Token" (lib/get-token)}))

(defn ajax-get [url]
  (debug/update-debug :last-api url)
  (GET (make-url url)
       {:handler lib/update-all-states!
        :headers (token-header)
        :error-handler error-handler})
  (lib/loading? true))

(defn item-click
  "Dispatch which action has to be done when clicking an item."
  [id url]
  (lib/hide-add-form)
  (cond
    (= url "back") (history/back!)
    (= url "add")  (lib/show-add-form)
    (= id "item_start_statement") (lib/show-add-form)
    (= id "item_start_premise")   (lib/show-add-form)
    (= id "item_justify_premise") (lib/show-add-form)
    :else (ajax-get url)))
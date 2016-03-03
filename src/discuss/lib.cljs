(ns discuss.lib
  (:require [om.core :as om :include-macros true]
            [clojure.walk :refer [keywordize-keys]]))

(def project "discuss")

(defn prefix-name [name]
  (str project "-" name))

(def app-state
  (atom {:title "discuss"
         :intro "The current discussion is about"
         :discussion
                {:heading {:intro  "What is the initial position you are asdasdinterested in?"
                           :outro  ""
                           :bridge ""}}

         :issue {}
                ;{:uid  1
                ; :date "12:41 PM, 24. Feb. 2016"
                ; :info "Your familiy argues about whether to buy a cat or dog as pet. Now your opinion matters!",
                ; :slug "cat-or-dog",
                ; :title "Cat or Dog",
                ; :arg_count 99}

         :items []
                ;[{:id       "item_1"
                ;  :title    "We should get a cat"
                ;  :url      "api/cat-or-dog/attitude/1"
                ;  :attitude "start"}
                ; {:id       "item_2"
                ;  :title    "We should get a dog"
                ;  :url      "api/cat-or-dog/attitude/2"
                ;  :attitude "start"}
                ; {:id       "item_3"
                ;  :title    "We could get both, a cat and a dog"
                ;  :url      "api/cat-or-dog/attitude/3"
                ;  :attitude "start"}]
         }))

(defn get-cursor
  "Return a cursor to the corresponding keys in the app-state."
  [key]
  (om/ref-cursor (key (om/root-cursor app-state))))

(defn update-state!
  "Get the cursor for given key and update it with the new collection of data."
  [key col]
  (let [state (get-cursor key)]
    (om/transact! state (fn [] col))))

(defn- update-all-states!
  "Update item list with the data provided by the API."
  [response]
  (let [res (keywordize-keys response)
        items (:items res)
        discussion (:discussion res)
        issues (:issues res)]
    (update-state! :items items)
    (update-state! :discussion discussion)
    (update-state! :issues issues)))
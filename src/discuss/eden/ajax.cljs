(ns discuss.eden.ajax
  (:require [discuss.utils.common :as lib]
            [discuss.config :as config]
            [discuss.communication.lib :as comlib]
            [ajax.core :refer [GET]]
            [cljs.spec.alpha :as s]
            [discuss.utils.logging :as log]
            [discuss.eden.specs :as especs]))

(defn- assoc-reference
  "Add reference to premise and conclusion if it has been selected. Else returns
  the unmodified body."
  [body reference]
  (if (seq reference)
    (let [ref-list [{:text reference}]]
      (-> body
          (assoc-in [:premise :references] ref-list)
          (assoc-in [:conclusion :references] ref-list)))
    body))
(s/fdef assoc-reference
  :args (s/cat :body map? :reference string?)
  :ret map?)

(defn- after-eden-post-handler []
  (lib/change-view! :eden/overview))

(defn post-eden-argument
  "Post an Argument directly to EDEN for later usage."
  [{:keys [premise conclusion reference search/selected]}]
  (let [request-url (str (lib/host-eden) (:add/argument config/eden))
        headers {"Content-Type" "application/json"}
        handler after-eden-post-handler
        body {:premise {:text premise}
              :conclusion {:text (if (nil? selected) conclusion (:text selected))}
              :author-id (lib/get-user-id)
              :link-type :support}
        body-with-ref (assoc-reference body reference)]
    (comlib/do-post request-url body-with-ref handler comlib/error-handler headers)))

(defn- received-arguments-handler [response]
  (let [arguments (-> response lib/json->clj :arguments)
        valid-arguments (filter #(s/valid? ::especs/argument %) arguments)]
    (log/debug "Received %d argument(s)" (count valid-arguments))
    (lib/store-to-app-state! 'eden/arguments arguments)))

(defn search-arguments-by-author
  "Send current author-name to EDEN instance and return all associated arguments.
  When no author is provided, use the currently logged in user."
  ([author]
   (log/debug "Querying arguments from EDEN instance for %s" author)
   (GET (str (lib/host-eden) (:search/arguments-by-author config/eden))
        {:handler received-arguments-handler
         :params {:author-name author}}))
  ([] (search-arguments-by-author (lib/get-nickname))))

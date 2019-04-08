(ns discuss.communication.main
  "Functions concerning the communication with the remote discussion system."
  (:require [cljs.reader]
            [ajax.core :refer [GET]]
            [discuss.utils.common :as lib]
            [discuss.references.integration :as rint]
            [discuss.communication.lib :as comlib]
            [discuss.components.search.statements :as search]
            [discuss.utils.logging :as log]
            [discuss.history.discussion :as hdis]
            [discuss.communication.connectivity :as comcon]
            [discuss.config :as config]))

;; Handler
(defn process-url-handler
  "React on response after sending a new statement. Reset atom and call newly
  received url."
  [response]
  (search/remove-search-results!)
  (lib/hide-add-form!)
  (rint/request-references)
  (lib/change-to-next-view!)
  (comlib/process-and-set-items-and-bubbles response))


;;;; POST functions
(defn post-json
  "Wrapper to prepare a POST request. Sending and receiving JSON."
  ([url body handler headers]
   (comlib/do-post (comlib/make-url url) body handler comlib/error-handler headers))
  ([url body handler]
   (post-json url body handler {"Content-Type" "application/json"}))
  ([url body]
   (post-json url body process-url-handler {"Content-Type" "application/json"})))

(defn- build-origin-body
  "Transform search results so that D-BAS can handle it."
  [search-result]
  (when-not (nil? search-result)
    (let [identifier (:identifier search-result)]
      {:entity-id (:entity-id identifier)
       :aggregate-id (:aggregate-id identifier)
       :author (get-in search-result [:author :nickname])
       :version (:version identifier)})))

(defn post-statement
  "Takes statement, an optional reference and maybe a search-result from EDEN to
  post it to the backend."
  [{:keys [statement reference search/selected]}]
  (if (seq (hdis/get-last-discussion-url))
    (let [url (hdis/get-last-discussion-url)
          headers (merge {"Content-Type" "application/json"} (comlib/token-header))
          body {:reason (if (nil? selected) statement (:text selected))
                :reference reference
                :origin (build-origin-body selected)}]
      (post-json url body process-url-handler headers))
    (log/error "Can't determine last used discussion step, cannot post statement
    to empty URL.")))

(defn post-position
  "Add new position, its reason and an optional reference to post it to the
  backend."
  [{:keys [position reason reference search/selected]}]
  (lib/next-view! :discussion/main)
  (let [url (lib/get-current-slug)
        headers (merge {"Content-Type" "application/json"} (comlib/token-header))
        body {:position position
              :reason (if (nil? selected) reason (:text selected))
              :reference reference
              :origin (build-origin-body selected)}]
    (post-json url body process-url-handler headers)))


;; -----------------------------------------------------------------------------
;; Load remote configuration

(defn- after-loading-config
  "In all cases, we want to initialize discuss with the init-functions."
  []
  (comcon/check-connectivity-of-hosts)
  (rint/request-references)
  (comlib/init!))

(defn- config-found
  "Handler for valid request, when a configuration file was found. Parse edn file
  and store the values in the app-state."
  [response]
  (let [{dbas :dbas/api
         eden :eden/api
         slug :discussion/slug} (cljs.reader/read-string response)
        to-be-called-fns [[slug lib/set-slug!] [dbas lib/host-dbas!] [eden lib/host-eden!]]]
    (log/info "Found a valid service configuration file. Setting hosts to dbas: %s, eden: %s, slug: %s" dbas eden slug)
    (doseq [[value f] to-be-called-fns]
      (when (not-empty value)
        (f value)))
    (after-loading-config)))

(defn- config-not-found [_response]
  (log/warning "Remote service configuration could not be found, although it is
  configured. Please check the URL.")
  (after-loading-config))

(defn load-remote-configuration!
  "Function to query remote configuration file and call the appropriate handlers."
  ([url handler error-handler]
   (when (and (string? url) (seq url))
     (log/info "Querying service information from %s" url)
     (GET url
          {:handler handler
           :error-handler error-handler})))
  ([] (load-remote-configuration! config/remote-configuration config-found config-not-found)))

(ns discuss.components.options
  (:require [om.next :as om :refer-macros [defui]]
            [cljs.spec.alpha :as s]
            [sablono.core :as html :refer-macros [html]]
            [discuss.utils.bootstrap :as bs]
            [discuss.translations :as translations :refer [translate] :rename {translate t}]
            [discuss.utils.common :as lib]
            [discuss.utils.views :as vlib]
            [discuss.config :as config]))

(defn- language-button
  "Create button to set language."
  [[lang-keyword lang-verbose]]
  (bs/button-default-sm #(lib/language-next! lang-keyword) lang-verbose))


;; -----------------------------------------------------------------------------
;; Set hosts

(s/fdef set-host-config
  :args (s/cat :this any? :title string? :current-host string?
               :default-host string? :set-host fn? :reset-host fn?))
(defn- set-host-config [this title current-host default-host set-host reset-host]
  (let [content (or (get (om/get-state this) :content) "")]
    [:div
     [:h5 title]
     [:div.input-group
      [:span.input-group-addon (t :options :new-route)]
      [:input.form-control {:onChange #(om/update-state! this assoc :content (.. % -target -value))
                            :value (or content "")
                            :placeholder default-host}]]
     [:div.input-group
      [:span.input-group-addon (t :options :current)]
      [:input.form-control {:value (or (current-host) "")
                            :disabled true}]]
     [:div.input-group
      [:span.input-group-addon (t :options :default)]
      [:input.form-control {:value (or default-host "")
                            :disabled true}]]
     [:button.btn.btn-sm.btn-default {:onClick #(set-host content)
                                      :disabled (empty? content)}
      (t :options :save) " " title]
     [:button.btn.btn-sm.btn-warning.pull-right
      {:onClick reset-host}
      (t :options :reset)]]))

(defui HostDBAS
  static om/IQuery
  (query [this] [:host/dbas :layout/lang])
  Object
  (render [this]
          (let [{:keys [host/dbas]} (om/props this)]
            (html
             (set-host-config this "D-BAS API" lib/host-dbas config/remote-host lib/host-dbas! lib/host-dbas-reset!)))))
(def host-dbas (om/factory HostDBAS))

(defui HostEDEN
  static om/IQuery
  (query [this] [:host/eden :layout/lang])
  Object
  (render [this]
          (let [{:keys [host/eden]} (om/props this)]
            (html
             (set-host-config this "EDEN Search" lib/host-eden config/search-host lib/host-eden! lib/host-eden-reset!)))))
(def host-eden (om/factory HostEDEN))

(defui Options
  static om/IQuery
  (query [this]
         `[:layout/lang :host/dbas :host/eden
           {:options/host-dbas ~(om/get-query HostDBAS)}
           {:options/host-eden ~(om/get-query HostEDEN)}])
  Object
  (render [this]
          (html
           [:div
            [:div (vlib/view-header (t :options :heading))
             [:div.row
              [:div.col-md-3 (vlib/fa-icon "fa-flag") (t :options :lang :space)]
              [:div.col-md-9 (interpose " " (mapv language-button translations/available))]]
             ]
            [:br]
            [:hr]
            [:h4.text-center (t :options :routes)]
            (host-dbas (om/props this))
            [:hr]
            (host-eden (om/props this))])))
(def options (om/factory Options))

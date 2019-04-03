(ns discuss.communication.bubble-replacements
  (:require [hickory.render :as hickrender]
            [hickory.core :as hickory]
            [cljs.spec.alpha :as s]
            [clojure.string :as string]
            [goog.string :refer [format]]
            [goog.string.format]
            [discuss.translations :refer [translate] :rename {translate t}]
            [discuss.utils.common :as lib]
            [discuss.communication.specs :as comspecs]))

(defn replace-profile-link
  "Replace bubble which contains a profile link to a user. This link must be
  converted in discuss, otherwise we'd leave the current discussion."
  [bubble]
  (let [hbubble (:html bubble)]
    (if (re-find #"/user/" hbubble)
      (let [parsed-bubble (mapv hickory/as-hiccup (hickory/parse-fragment hbubble))
            link-in-bubble (first parsed-bubble)
            link' (-> link-in-bubble
                      second
                      (update :href #(format "%s%s" (string/replace (lib/host-dbas) #"/api" "") %))
                      (assoc :target "_blank"))]
        (assoc bubble :html
               (hickrender/hiccup-to-html
                (assoc parsed-bubble 0
                       (assoc link-in-bubble 1 link')))))
      bubble)))

(defn replace-profile-link-bubbles
  "Take collection of bubbles and replace those bubbles containing a profile link."
  [bubbles]
  (mapv replace-profile-link bubbles))

(defn replace-congratulation-bubble-text
  "Takes a bubble and modifies the content if it is a congratulation-bubble from
  dbas."
  [bubble]
  (let [{btype :type, hbubble :html} bubble]
    (if (and (= "info" btype)
             (re-find #"fa-trophy" hbubble))
      (assoc bubble
             :html (t :discussion :bubble/congrats)
             :text (t :discussion :bubble/congrats))
      bubble)))

(defn replace-congratulation-bubbles
  "Takes collection of bubbles, finds the congratulation-bubble and replaces it
  with a new text."
  [bubbles]
  (mapv replace-congratulation-bubble-text bubbles))

(s/fdef replace-profile-link
  :args (s/cat :bubble ::comspecs/bubble)
  :ret ::comspecs/bubble)

(s/fdef replace-profile-link-bubbles
  :args (s/cat :bubbles (s/coll-of ::comspecs/bubble))
  :ret (s/coll-of ::comspecs/bubble))

(s/fdef replace-congratulation-bubble-text
  :args (s/cat :bubble ::comspecs/bubble)
  :ret ::comspecs/bubble)

(s/fdef replace-congratulation-bubbles
  :args (s/cat :bubbles (s/coll-of ::comspecs/bubble))
  :ret (s/coll-of ::comspecs/bubble))

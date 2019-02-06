(ns discuss.history.core
  (:require [discuss.history.discussion :as hdis]
            [discuss.communication.lib :as comlib]))

(defn back!
  "Re-request the URL of the last clicked item."
  []
  (let [discussion-urls (hdis/get-discussion-urls)
        remaining-discussion-urls (pop discussion-urls)
        next-step (peek remaining-discussion-urls)]
    (when-not (nil? next-step)
      (hdis/save-discussion-urls! remaining-discussion-urls)
      (comlib/discussion-step next-step))))

(defn back-possible?
  "Check if there are URLs which can still be accessed to travel back in time."
  []
  (< 1 (count (hdis/get-discussion-urls))))

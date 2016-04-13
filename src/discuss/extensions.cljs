(ns discuss.extensions
  "Extending JS types to be better accessible in CLJS.")

(defn extend-type-fn
  "Given a type t, apply extensions."
  [t]
  (extend-type t
    ISeqable
    (-seq [array] (array-seq array 0))
    ICounted
    (-count [a] (alength a))
    IIndexed
    (-nth
      ([array n]
       (if (< n (alength array)) (aget array n)))
      ([array n not-found]
       (if (< n (alength array)) (aget array n)
                                 not-found)))
    ILookup
    (-lookup
      ([array k]
       (aget array k))
      ([array k not-found]
       (-nth array k not-found)))
    IReduce
    (-reduce
      ([array f]
       (ci-reduce array f))
      ([array f start]
       (ci-reduce array f start)))))

(extend-type-fn js/NodeList)
(extend-type-fn js/HTMLCollection)
(extend-type-fn js/HTMLDocument)
(extend-type-fn js/HTMLDivElement)
(extend-type-fn js/HTMLParagraphElement)
(extend-type-fn js/HTMLSpanElement)
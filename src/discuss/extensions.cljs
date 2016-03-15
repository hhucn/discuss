(ns discuss.extensions)

(extend-type js/HTMLCollection
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
     (ci-reduce array f start))))

(extend-type js/NodeList
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
     (ci-reduce array f start))))
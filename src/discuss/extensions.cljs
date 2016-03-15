(ns discuss.extensions)

(enable-console-print!)

(when (js* "typeof NodeList != \"undefined\"")
  (extend-type js/NodeList
    ISeqable
    (-seq [array] (array-seq array 0))))

(extend-type js/HTMLCollection
  ISeqable
  (-seq [array] (array-seq array 0)))

(extend-type js/HTMLSpanElement
  ISeqable
  (-seq [array] (array-seq array 0)))
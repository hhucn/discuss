(ns discuss.lib)

(def project "discuss")

(defn prefix-name [name]
  (str project "-" name))
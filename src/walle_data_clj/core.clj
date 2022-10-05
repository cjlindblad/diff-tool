(ns walle-data-clj.core
  (:require [clojure.data.json :as json])
  (:require [clojure.data :as data]))

(def local-data
  (json/read-str (slurp "local.json") :key-fn keyword))

(def prod-data
  (json/read-str (slurp "prod.json") :key-fn keyword))

(def local-data-by-pevi
  (group-by :pevi local-data))

(def prod-data-by-pevi
  (group-by :pevi prod-data))

(def local-pevi-set (set (keys local-data-by-pevi)))

(def all-pevis
  (set
   (concat
    (keys local-data-by-pevi)
    (keys prod-data-by-pevi))))

(def stuff
  (map
   (fn [pevi]
     [pevi
      (first (local-data-by-pevi pevi))
      (first (prod-data-by-pevi pevi))])
   all-pevis))

(def diff
  (map
   (fn [[pevi local prod]]
     (let [[local-only prod-only] (data/diff local prod)]
       {pevi
        {:local-only local-only
         :prod-only prod-only}}))
   stuff))

(spit "diff.json" (json/write-str diff))

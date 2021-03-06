(ns sputter.storage.stub
  (:require [sputter.storage :as storage]
            [sputter.word    :as word]
            #?(:clj [sputter.util :refer [for-map]]))
  #?(:cljs
     (:require-macros [sputter.macros :refer [for-map]])))

(extend-type #?(:clj (type {})
                :cljs cljs.core/PersistentArrayMap)
  storage/VMStorage
  (retrieve [m addr pos]
    (get-in m [addr pos] word/zero))
  (store [m addr pos word]
    (if (word/zero? word)
      (update m addr dissoc pos)
      (assoc-in m [addr pos] word)))
  (stored [m addr]
    (count (m addr))))

(defn- ->addr-storage [m]
  (for-map [[pos w] m
            :let  [w (word/->Word w)]
            :when (not (word/zero? w))]
    pos w))

(defn ->Storage [& [m]]
  (for-map [[addr m] m]
    (word/->Word addr) (->addr-storage m)))

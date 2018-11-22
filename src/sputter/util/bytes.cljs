(ns sputter.util.bytes)

(extend-type js/Uint8Array
  ICounted
  (-count [arr]
    (.-byteLength arr))

  IIndexed
  (-nth
   ([arr n]
    (-nth arr n nil))
   ([arr n not-found]
    (if (= 0 (count arr))
      not-found
      (aget arr n))))

  ILookup
  (-lookup
   ([arr n]
    (-nth arr n nil))
   ([arr n not-found]
    (-nth arr n not-found))))

(defn byte-array? [x]
  (js/Uint8Array.prototype.isPrototypeOf x))

(defn byte-array
  [size-or-coll]
  (cond
    (number? size-or-coll) (js/Uint8Array. size-or-coll)

    (or (array? size-or-coll)
        (coll? size-or-coll))
    (let [len (count size-or-coll)
          arr (js/Uint8Array. len)]
      (loop [i 0, coll size-or-coll]
        (when (< i len)
          (aset arr i (first coll))
          (recur (inc i) (next coll))))
      arr)))

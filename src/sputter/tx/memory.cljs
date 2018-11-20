(ns sputter.tx.memory
  (:require [sputter.word :as word]
            [sputter.util.bytes :as bytes]))

(defprotocol VMMemory
  "Ephemeral byte-addressed data store."
  (insert [mem dest-byte byte-vec n]
    "Copy `n` right-most bytes from `byte-vec` to position `dest-byte` in `mem`.
     Returns `mem`.")
  (recall [mem from-byte n-bytes]
    "Read `n-bytes` from `mem`, starting at `from-byte`, extending if necessary.
     Returns vector of `[mem byte-vec]`")
  (words [mem]
    "Return the number of words in `mem`"))

(defn- extend* [table byte-pos]
  (let [need (- byte-pos (count table))]
    (if (pos? need)
      (let [arr (bytes/byte-array byte-pos)]
        (.set arr table 0)
        arr)
      table)))

(extend-type js/Uint8Array
  VMMemory
  (insert [mem dest-byte byte-vec n]
    (let [dest-byte (if (js/bn.isBN dest-byte) (.toNumber dest-byte) dest-byte)
          n (if (js/bn.isBN n) (.toNumber n) n)
          data (.slice byte-vec (- (count byte-vec) n))
          mem'  (extend* mem (+ dest-byte n))]
      (.set mem' data dest-byte)
      mem'))
  (recall [mem from-byte n-bytes]
    (let [from-byte (if (js/bn.isBN from-byte) (.toNumber from-byte) from-byte)
          n-bytes (if (js/bn.isBN n-bytes) (.toNumber n-bytes) n-bytes)
          mem' (extend* mem (+ from-byte n-bytes))]
      [mem' (.slice mem from-byte (+ from-byte n-bytes))]))
  (words [mem]
    (int (Math/ceil (/ (count mem) word/size)))))

(defn memory? [x] (satisfies? VMMemory x))

(defn ->Memory [x]
  (cond
    (memory? x) x
    (coll?   x) (bytes/byte-array x)))

(ns sputter.word
  (:require [sputter.util :as util]
            #?(:cljs [sputter.util.bytes :as bytes])
            #?(:clj [sputter.util.uint :as u]
               :cljs [sputter.util.bn :as bn])
            [clojure.string    :as str])
  #?(:clj (:import [io.nervous.juint UInt256]))
  (:refer-clojure :exclude [zero? or mod]))

(def size 32)
(def max-value
  #?(:clj (u/mask (* 8 size))
     :cljs (bn/mask (* 8 size))))

(defprotocol VMWord
  (add
    [word x]
    [word x m]
    "`word` + `x` [% `m`]")
  (sub [word x]
    "`word` - `x`")
  (mul
    [word x]
    [word x m]
    "`word` * `x` [% `m`]")
  (mod [word x]
    "`word` % `x`")
  (div [word x]
    "`word` / `x`")
  (or [word x]
    "`word` | `x`")
  (zero? [word])
  #?(:clj
     (as-vector [word]
       "Return a fixed length, zero-padded byte vector representation of
        `word`'s underlying bytes.")
     :cljs
     (as-bytes [word]
      "Return a fixed length, zero-padded Uint8Array representation of
       `word`'s underlying bytes."))
  #?(:clj
     (as-uint [word]
       "Return a [[UInt256]] representation of `word`.")
     :cljs
     (as-bigint [word]
       "Return a bn.js representation of `word`.")))

#?(:clj
   (extend-type UInt256
     VMWord
     (add
       ([word x]   (u/+ word x))
       ([word x m] (.addmod word x m)))
     (sub [word x] (u/- word x))
     (mul
       ([word x]   (u/* word x))
       ([word x m] (.mulmod word x m)))
     (div [word x] (u// word x))
     (mod [word x] (u/mod word x))
     (or [word x]
       (u/or word x))
     (zero? [word]
       (u/zero? word))
     (as-vector [word]
       (apply vector-of :byte (u/to-byte-array word size)))
     (as-uint [word]
       word)))

#?(:cljs
   (do
    (defn- truncate [word]
      (bn/and (bn/abs word) max-value))

    (extend-type bn/BigInt
      VMWord
      (add
        ([word x]   (-> word (bn/+ x)   truncate))
        ([word x m] (-> word (bn/+ x)   (mod m))))
      (sub [word x] (-> word (bn/- x)   truncate))
      (mul
        ([word x]   (-> word (bn/* x)   truncate))
        ([word x m] (-> word (bn/* x)   (mod m))))
      (div [word x] (-> word (bn// x)   truncate))
      (mod [word x] (-> word (bn/mod x) truncate))
      (or [word x]  (-> word (bn/or x)  truncate))
      (zero? [word]
        (bn/zero? word))
      (as-bytes [word]
        (bn/to-byte-array word 32))
      (as-bigint [word]
        word))))

#?(:clj (def word? (partial satisfies? VMWord))
   :cljs (defn word? [x] (satisfies? VMWord x)))

(defn ->Word [x]
  #?(:clj
     (cond
       (word?   x) x
       (number? x) (UInt256. (biginteger x))
       (string? x) (UInt256. (cond-> x
                               (str/starts-with? x "0x") (subs 2))
                             16)
       (coll? x) (UInt256. (byte-array x))
       :else (UInt256. x)))
  #?(:cljs
     (cond
       (word? x) x
       :else (bn/bigint x))))

(def one  (->Word 1))
(def zero (->Word 0))

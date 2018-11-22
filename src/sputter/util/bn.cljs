(ns sputter.util.bn
  (:require [cljsjs.bn]
            [sputter.util.bytes :refer [byte-array byte-array?]]
            [clojure.string    :as str])
  (:refer-clojure :exclude [or and - + * / not mod zero?])
  (:require-macros [devcards.core :refer [defcard]]))

;; TODO use unsigned versions of bn.js methods?

(def BigInt js/bn)

(extend-type BigInt
  IEquiv
  (-equiv [o other]
    (clojure.core/and (js/bn.isBN other)
         (.eq o other))))

(defn bigint [x]
  (cond
    (number? x)     (BigInt. x)
    (array? x)      (BigInt. x)
    (string? x)     (BigInt. (cond-> x
                               (str/starts-with? x "0x") (subs 2))
                             16)
    (byte-array? x) (BigInt. (js/Array.from x))))

(def one (bigint 1))
(def zero (bigint 0))

(defn zero? [x] (.isZero x))

(defn <<  [x y] (.shln  x y))
(defn >>  [x y] (.shrn x y))
(defn pow [x y] (.pow x y))
(defn mod [x y] (.mod x y))

(defn not [x] (.not x))
(defn abs [x] (.abs x))

(defn -   [x & xs] (if (not-empty xs) (.sub x (apply -   xs)) x))
(defn +   [x & xs] (if (not-empty xs) (.add x (apply +   xs)) x))
(defn *   [x & xs] (if (not-empty xs) (.mul x (apply *   xs)) x))
(defn /   [x & xs] (if (not-empty xs) (.div x (apply /   xs)) x))
(defn or  [x & xs] (if (not-empty xs) (.or  x (apply or  xs)) x))
(defn and [x & xs] (if (not-empty xs) (.and x (apply and xs)) x))

(defn mask
  ([m] (-> one (<< m) (- one)))
  ([n m]
   (and n (mask m))))

(defn to-byte-array [x & [pad-to-bytes]]
  (if pad-to-bytes
    (byte-array (.toArray x "be" pad-to-bytes))
    (byte-array (.toArray x "be"))))

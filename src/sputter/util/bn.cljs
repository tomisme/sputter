(ns sputter.util.bn
  (:require [cljsjs.bn]
            [sputter.util.bytes :refer [byte-array]])
  (:refer-clojure :exclude [or and - + * / not mod zero?]))

;; TODO use unsigned versions of bn.js methods?

(def BN js/bn)

(defn bignumber [x]
  (cond
    (number? x) (BN. x)
    (array? x) (BN. x)))

(def one (bignumber 1))
(def zero (bignumber 0))

(defn zero? [x] (.isZero x))

(defn <<  [x y] (.shln  x y))
(defn >>  [x y] (.shrn x y))
(defn pow [x y] (.pow x y))
(defn mod [x y] (.mod x y))

(defn not [x] (.not x))

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

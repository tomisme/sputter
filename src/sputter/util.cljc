(ns sputter.util
  (:require [clojure.string          :as str]
            #?(:cljs [sputter.util.bytes :as bytes])
            #?(:clj [pandect.algo.keccak-256 :as kk-256]
               :cljs [goog.crypt :as crypt]))
  #?(:clj (:import [javax.xml.bind DatatypeConverter]
                   [java.math        BigInteger]
                   [io.nervous.juint UInt256]
                   [java.util        Arrays])))

#?(:clj
   (do
     (def sha3       kk-256/keccak-256)
     (def sha3-bytes kk-256/keccak-256-bytes)))

(defn hex->bytes [s]
  #?(:clj
     (-> (str/replace s "0x" "")
         DatatypeConverter/parseHexBinary)
     :cljs
     (-> (str/replace s "0x" "")
         crypt/hexToByteArray
         bytes/byte-array)))

(defn- print-hex [bs]
  (let [bs #?(:clj (cond (instance? BigInteger bs) (.toByteArray bs)
                         (instance? UInt256    bs) (.toByteArray bs)
                         (number? bs)              (.toByteArray (biginteger bs))
                         :else                     bs)
              :cljs bs)]
    (-> bs
        #?(:clj DatatypeConverter/printHexBinary
           :cljs crypt/byteArrayToString)
        (str/replace #"^0+" ""))))

(defn- pad [s opts]
  (let [bs     (/ (count s) 2)
        zeroes (* 2 (- (:pad-left opts bs) bs))]
    (str (apply str (repeat zeroes "0")) s)))

(defn bytes->hex [bs & [opts]]
  (let [strings (->> bs print-hex str/lower-case)]
    (pad (apply str strings) opts)))

(defn byte-slice [bytes i len]
  #?(:clj (Arrays/copyOfRange bytes (long i) (long (+ i len)))
     :cljs (.slice bytes i (+ i len))))

(defn map-values [f m]
  (into {}
    (for [[k v] m]
      [k (f v)])))

#?(:clj
   (defmacro for-map
     ([seq-exprs key-expr val-expr]
      `(for-map ~(gensym "m") ~seq-exprs ~key-expr ~val-expr))
     ([m-sym seq-exprs key-expr val-expr]
      `(let [m-atom# (atom (transient {}))]
         (doseq ~seq-exprs
           (let [~m-sym @m-atom#]
             (reset! m-atom# (assoc! ~m-sym ~key-expr ~val-expr))))
         (persistent! @m-atom#)))))

(defn error? [v]
  (and (keyword? v) (= (namespace v) "sputter.error")))

(defn- longest-prefix [a b]
  (take-while identity (map (fn lp [aa bb] (when (= aa bb) aa)) a b)))

(defn split-prefix [a b]
  (let [prefix (longest-prefix a b)
        n      (count prefix)]
    [(not-empty prefix) (drop n a) (drop n b)]))

;; TODO merge with util.clj
(ns sputter.test.util
  (:require [sputter.util         :as util]
            [sputter.tx           :as tx]
            [sputter.storage      :as storage]
            [sputter.storage.stub :as storage.stub]
            [sputter.word         :as word]
            [sputter.vm           :as vm]
            [clojure.test         :as test :refer [is]]
            [clojure.string       :as str]
            [sputter.util.bn      :as bn])
  (:require-macros
   [cljs.test :refer [testing]]
   [sputter.macros :refer [for-map]]
   [devcards.core :refer [defcard deftest]]))

(defn- hex->biginteger [s]
  (if (= s "0x")
    (bn/bigint 0)
    (bn/bigint (util/hex->bytes s))))

(defn- hex->word [s]
  (if (or (not s) (= s "0x"))
    word/zero
    (word/->Word s)))

(defn- ->storage-map [m]
  (for-map [[pos w] m]
    (hex->word pos) (hex->word w)))

(defn- ->storage-maps [addr->m]
  (for-map [[addr m] addr->m]
    (hex->word addr) (->storage-map (:storage m))))

(defn- map->Storage [m]
  (storage.stub/->Storage (->storage-maps m)))

(defn- test->tx [t]
  (let [exec (:exec t)]
    (tx/create-transaction
     {:program (vm/disassemble (:code exec))
      :gas     (hex->biginteger (:gas exec))
      :message {:recipient (word/->Word (:address exec))}
      :storage (map->Storage (:pre t))})))

(defn- assert-gas [test exp tx]
  (when-let [exp (some-> exp hex->biginteger)]
    (is (= exp (bn/bigint (:gas tx)))
        (str test ": Gas value mismatch. " exp " != " (:gas tx)))))

(defn- assert-return [test exp tx]
  (let [exp (hex->word exp)
        act (word/->Word (:sputter/return tx bn/zero))]
    (is (= exp act)
        (str test ": Return value mismatch. " exp " != " act))))

(defn- assert-error [test exp tx]
  (if (:sputter/error tx)
    (is (nil? (:gas exp))
        (str test ": Wants gas, but got error " (:sputter/error tx)))
    (is (:gas exp) (str test ": No gas value?"))))

(defn- assert-storage [test exp tx]
  (doseq [[addr pos->w] exp]
    (doseq [[pos w] pos->w :let [act-w (storage/retrieve tx addr pos)]]
      (is (= w act-w)
          (str test ": Storage value mismatch for "
               addr ": " w " != " act-w)))))

(defn- run-vm-test [test-name test]
  (let [tx (test->tx test)
        post  (vm/execute tx)]
    (testing "error"
      (assert-error   test-name test post))
    (testing "gas"
      (assert-gas     test-name (:gas test) post))
    (testing "storage"
      (assert-storage test-name (map->Storage (:post test)) post))
    (testing "return"
      (assert-return  test-name (:out test) post))))

(defn run-vm-tests [tests]
  (doseq [[test-name test] tests]
    (run-vm-test test-name test)))

(defn test-details [tests]
  (for [[test-name test] tests]
    (if (= test-name :addmod2)
      {test-name {:test test
                  :tx (test->tx test)}})))

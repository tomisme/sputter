;; TODO this file shouldn't be necessary
(ns sputter.test.get-tests
  (:require [cheshire.core    :as json]
            [clojure.java.io  :as io]
            [clojure.string   :as str]))

(def ^:private test-path "externals/ethereum-tests/VMTests")

(defn- hex-literal? [x]
  (and (string? x) (str/starts-with? x "0x")))

(let [renames {"gasPrice"          "gas-price"
               "currentCoinbase"   "coinbase"
               "currentGasLimit"   "gas-limit"
               "currentNumber"     "number"
               "currentDifficulty" "difficulty"
               "currentTimestamp"  "timestamp"}]
  (defn- ->test-key [k]
    (if (hex-literal? k)
      k
      (keyword (renames k k)))))

(defn- file->tests [f]
  (-> f io/reader (json/parse-stream ->test-key)))

(defn- ->test-suite [& subpath]
  (let [path    (str test-path "/" (str/join "/" subpath))
        entries (->> path io/file file-seq)]
    (into []
      (comp
       (remove #(.isDirectory %))
       (mapcat file->tests))
      entries)))

(def ^:private vm-tests (delay (->test-suite)))

(defn- named-tests [re]
  (into []
    (for [[test-name test] @vm-tests
          :when (re-find re (name test-name))]
      [test-name test])))

(defmacro get-tests [re]
  (named-tests re))

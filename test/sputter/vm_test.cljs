(ns sputter.vm-test
  (:require
   [sputter.test.util :as test.util])
  (:require-macros
   [sputter.test.get-tests :refer [get-tests]]
   [devcards.core :refer [defcard deftest]]))

(deftest add
  (test.util/run-vm-tests (get-tests #"^add\d+$")))

(deftest addmod
  (test.util/run-vm-tests (get-tests #"^addmod[^_]+$")))

(deftest sub
  (test.util/run-vm-tests (get-tests #"^sub\d+$")))

(deftest mul
  (test.util/run-vm-tests (get-tests #"^mul(?!mod)")))

(deftest mulmod
  (test.util/run-vm-tests (get-tests #"^mulmod[^_]+$")))

(deftest div
  (test.util/run-vm-tests (get-tests #"^div")))

(deftest mod*
  (test.util/run-vm-tests (get-tests #"^mod")))

(deftest or*
  (test.util/run-vm-tests (get-tests #"^or\d+")))

(deftest gt
  (test.util/run-vm-tests (get-tests #"^gt\d+")))

(deftest lt
  (test.util/run-vm-tests (get-tests #"^lt\d+")))

(deftest dup
  (test.util/run-vm-tests (get-tests #"^dup")))

(deftest swap
  (test.util/run-vm-tests (get-tests #"^swap")))

(deftest push
  (test.util/run-vm-tests (get-tests #"^push(?!32AndSuicide)")))

(deftest mload
  (test.util/run-vm-tests (get-tests #"^mload")))

(deftest mstore
  (test.util/run-vm-tests (get-tests #"^mstore")))

(deftest msize
  (test.util/run-vm-tests (get-tests #"^msize")))

(deftest return
  (test.util/run-vm-tests (get-tests #"^return")))

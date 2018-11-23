(ns sputter.vm-test
  (:require
   [sputter.test.util :as test.util])
  (:require-macros
   [sputter.test.get-tests :refer [get-tests]]
   [devcards.core :refer [defcard deftest]]))

; (defcard add
;   (test.util/test-details (get-tests #"^add\d+$")))

;;

; (deftest add
;   (test.util/run-vm-tests (get-tests #"^add\d+$")))

; (defcard addmod
;   (test.util/test-details (get-tests #"^addmod[^_]+$")))

; (deftest addmod
;   (test.util/run-vm-tests (get-tests #"^addmod[^_]+$")))

; (deftest sub
;   (test.util/run-vm-tests (get-tests #"^sub\d+$")))

; (deftest mul
;   (test.util/run-vm-tests (get-tests #"^mul(?!mod)")))

; (deftest mulmod
;   (test.util/run-vm-tests (get-tests #"^mulmod[^_]+$")))

; (deftest div
;   (test.util/run-vm-tests (get-tests #"^div")))

; (deftest mod*
;   (test.util/run-vm-tests (get-tests #"^mod")))

; (deftest or*
;   (test.util/run-vm-tests (get-tests #"^or\d+")))

; (deftest gt
;   (test.util/run-vm-tests (get-tests #"^gt\d+")))

; (deftest lt
;   (test.util/run-vm-tests (get-tests #"^lt\d+")))
;; 1
(deftest dup
  (test.util/run-vm-tests (get-tests #"^dup")))

; (deftest swap
;   (test.util/run-vm-tests (get-tests #"^swap")))

; (deftest push
;   (test.util/run-vm-tests (get-tests #"^push(?!32AndSuicide)")))
;; 1
(deftest mload
  (test.util/run-vm-tests (get-tests #"^mload")))
;; 8
(deftest mstore
  (test.util/run-vm-tests (get-tests #"^mstore")))
;; 2
(deftest msize
  (test.util/run-vm-tests (get-tests #"^msize")))
;; 5
(deftest return
  (test.util/run-vm-tests (get-tests #"^return")))

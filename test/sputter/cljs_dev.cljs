(ns sputter.cljs-dev
  (:require [devcards.core]
            [sputter.vm :as vm]
            [sputter.word :as word]
            [sputter.storage :as storage]
            [sputter.tx :as tx]
            [sputter.tx.memory :as mem]
            [sputter.util.bn :as bn]
            [sputter.util.bytes :refer [byte-array]]
            [sputter.vm-test])
  (:require-macros
   [devcards.core :refer [defcard]]))
; (defn d [x] (js/console.log x) x)

(defcard bigints
  {
   "(- one one)" (bn/- bn/one bn/one)
   "(+ one one one)" (bn/+ bn/one bn/one bn/one)
   "256.toArray" (.toArray (bn/bigint 256))
   "one to-byte-array" (bn/to-byte-array bn/one)
   "one pad" (bn/to-byte-array bn/one 10)})

(defcard words
  {
   "max-value bytes" (count (js->clj (.toArray word/max-value)))
   "one" word/one
   "(word? one)" (word/word? word/one)
   "(word? 1)" (word/word? 1)
   "(add one one)" (word/add word/one word/one)
   "(sub one one)" (word/sub word/one word/one)
   "as-bytes" (word/as-bytes (word/->Word 255))})

(def bytes1
  (byte-array [0x60 0x01 0x60 0x02 0x01]))

(defcard byte-array
  {
   "bytes1" bytes1
   "(count bytes1)" (count bytes1)
   "(nth bytes1 0)" (nth bytes1 0)})

(defcard mem
  {
   "empty" (mem/->Memory [])
   "recall padded" (mem/recall (mem/->Memory []) 0 10)
   "inserted" (let [aa (byte-array (repeat word/size 0xaa))
                    bb (byte-array (repeat word/size 0xbb))
                    cc (byte-array (repeat word/size 0xcc))]
                (-> (mem/->Memory [])
                    (mem/insert 0         aa word/size)
                    (mem/insert word/size cc word/size)
                    (mem/insert 16        bb word/size)))})

(defcard storage
  (-> (tx/create-transaction {:message {:recipient (word/->Word 7)}})
    (storage/store :recipient (word/->Word 1) (word/->Word 42))))

(defcard add
  (let [p (vm/disassemble (byte-array [0x60 0x01 0x60 0x02 0x01]))
        t (tx/create-transaction
           {:gas 2100 :program p})]
    {"program" p
     "transaction" (assoc t :program "...")
     "(execute)" (vm/execute t)}))

(defcard jump
  (let [p (vm/disassemble (byte-array [0x60 0x02 0x5b 0x60 0x01 0x90
                                       0x03 0x80 0x60 0x02 0x57]))
        t (tx/create-transaction
           {:gas 2100 :program p})]
    {"program" p
     "transaction" (assoc t :program "...")
     "(execute)" (assoc (vm/execute t) :program "...")}))

(defcard mem
  (let [p (vm/disassemble "0x60ff60005260ee601e53601e6002f3")
        t (tx/create-transaction
            {:gas 2100
             :program p})]
    {"program" p
     "transaction" (assoc t :program "...")
     "(execute)" (assoc (vm/execute t) :program "...")}))

(devcards.core/start-devcard-ui!)

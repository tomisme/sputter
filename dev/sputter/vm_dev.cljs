(ns sputter.vm-dev
  (:require [sputter.util.bytes :refer [byte-array]]
            [sputter.vm :as vm]
            [sputter.tx :as tx])
  (:require-macros
   [devcards.core :refer [defcard]]))

(defcard test
  (let [p (vm/disassemble "0x60036001600660000308600055")
        t (tx/create-transaction
            {:gas 100000
             :program p
             :message {:recipient "0x0f572e5295c57f15886f9b263e2f6d2d6c7b5ec6"}})]
    {"program" p
     "transaction" (assoc t :program "...")
     "(execute)" (assoc (vm/execute t) :program "...")}))

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

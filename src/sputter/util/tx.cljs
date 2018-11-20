(ns sputter.util.tx
  (:require-macros [sputter.util.tx]))

(defn guard [tx f & args]
  (if (:sputter/error tx)
    tx
    (apply f tx args)))

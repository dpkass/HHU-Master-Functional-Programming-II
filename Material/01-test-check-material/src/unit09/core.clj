(comment "a)  Create a namespace in the test directory, that can access both functions.")

(ns unit09.core
  (:require [clojure.test :as t]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop])
  (:use unit09.edit-distance))


(comment "b)  Write a test.check property, which fails if two non-empty input-strings return
different output for both functions. Which implementation is faulty? What is the error?

Note: You do not need to repair the faulty implementation")

(def levenshteins-equal-prop (prop/for-all [in1 gen/string-alphanumeric in2 gen/string-alphanumeric]
  (= (levenshtein in1 in2) (levenschtein in1 in2))))

(def test-res (tc/quick-check 10000 levenshteins-equal-prop :seed 1 :max-size 5))

(def fail-case (:fail test-res))
fail-case

(apply levenshtein fail-case)
(apply levenschtein fail-case)


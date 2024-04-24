(ns functional-programming-ii.main-test
  (:require
   [clojure.test :refer [deftest is]]
   [functional-programming-ii.main :as main]))

(deftest main-test
  (is (= 0 (main/-main))))

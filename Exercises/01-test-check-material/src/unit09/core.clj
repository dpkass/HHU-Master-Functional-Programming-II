(comment "Task 1: (test.check)

In the files for the exercise you will find the namespace unit09.edit-distance. Have a look at
the functions levenshtein and levenschtein.")

(comment "a)  Create a namespace in the test directory, that can access both functions.")

(ns unit09.core
  (:require [clojure.string :as str]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop])
  (:use [unit09.edit-distance]))


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


(comment "c)  Write a generator user-generator, which generates users at a university. A user is
represented by a map, containing the first- and last name as well as the ID of the user. The ID
consists of the first two characters of the first name, the first three characters of the last
name and three digits. An example of a generated user is:

{:first-name \"John\", :last-name \"Wayne\", :id \"joway142\"}.

If a name is not long enough, as many characters as possible are used in the ID, for example
one possible ID of „Rich X“ would be rix666. You can choose from a fixed set of names. For this
purpose, you can use the name lists from https://github.com/dominictarr/random-name")

(def first-lastname-number-generator
  (gen/tuple
   (gen/elements ["Bruce" "Peter" "Clark"])
   (gen/elements ["Wayne" "Parker" "Kent"])
   (gen/large-integer* {:min 100 :max 999})))

(defn subs' "safe subs" [str lo hi]
  (let [len (count str)]
    (subs str (min lo len) (min hi len))))

(defn to-username [first last num]
  (assert (<= 100 num 999))
  (str/lower-case (str (subs' first 0 2) (subs' last 0 3) num)))

(defn to-user-map [[first last num]]
  {:first-name first
   :last-name  last
   :id         (to-username first last num)})

(def user-generator
  (gen/fmap to-user-map first-lastname-number-generator))

(gen/sample user-generator)




(comment "Task 2: (Shortest path problem)

Given a triangle in the form of a vector of vectors we want to find the shortest
possible path from the top of the triangle to the bottom, such that the sum of the weights is
minimized. In each step, only one adjacent field in the next lower row may be selected.")

(defn path
  ([pyramid] (first (path pyramid 0 0)))
  ([pyramid x y]
   (if (>= x (count pyramid))
     [0 '()]
     (let [val (get-in pyramid [x y])
           [l-val l-acc] (path pyramid (inc x) y)
           [r-val r-acc] (path pyramid (inc x) (inc y))]
       (if (< l-val r-val)
         [(+ l-val val) (conj l-acc val)]
         [(+ r-val val) (conj r-acc val)])))))

(defn my-trampoline [a b] true)




(comment "Task 3: (Trampoline (4clojure No. 78) and recursion)

We have already seen that self-recursion in tail position using recur ensures that no
additional stack frames are used per recursion step. This approach does not work if two (or more)
functions call each other.
The higher-order function trampoline receives a function and an arbitrary amount of values. The
function is then called with the given values as parameters. If the return value itself is a
function again, it is called without parameters. As long as the resulting return values are
functions, they will continue to be called, otherwise the value is returned by trampoline.")

(comment "a) Implement a function that behaves like trampoline. In particular, the recursion
should not use any more stack frames.

(letfn [(triple [x] (fn [] (sub-two (* 3 x))))
    (sub-two [x] (fn [] (stop? (- x 2))))
    (stop? [x] (if (> x 50) x (fn [] (triple x))))]
  (my-trampoline triple 2))
=> 82

(letfn [(my-even? [x] (if (zero? x) true (fn [] (my-odd? (dec x)))))
    (my-odd? [x] (if (zero? x) false (fn [] (my-even? (dec x)))))]
  (map (partial my-trampoline my-even?) (range 6)))
=> [true false true false true false]


Note: You can use fn? to check if a value is a function or not.

Additonal note: partial is a higher order function, which receives a function f and a part of
its arguments a1, . . . , ai and returns a function which accepts further parameters ai+1, . . .
, an (i ≤ n) and then calls f with parameters a1 to an. letfn is a special let for functions.")

(defn my-trampoline
  ([f & args] (my-trampoline (apply f args)))
  ([f] (if-not (fn? f) f (recur (f)))))


(comment "b) What do you have to do if the result of the entire calculation happens to be a
function itself?")

; wrap it
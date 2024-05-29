(ns transducers.exercise-03)


(comment "Exercise 3.1 (Transducer

Implement a function (transplace m), which receives a map m as argument and returns a transducer.
If an element is present as a key in m, it is replaced by the associated value, otherwise the
original element is retained.
The function replace must not be used for this.

user=> (transduce (transplace {:y :a}) conj [:x :y :z])
[:x :a :z]

user=> (transduce (comp (transplace {nil 0}) (map inc)) conj [42 nil 3])
[43 1 4]

user=> (transduce (comp (transplace {nil -1}) (partition-by pos?)) conj [1 2 3 0 5 6])
[[1 2 3] [0] [5 6]]")

(defn transplace [m]
  (fn [rf]
    (fn
      ([final] (rf final))
      ([acc x] (rf acc (get m x x))))))

(transduce (transplace {:y :a}) conj [:x :y :z])
(transduce (comp (transplace {nil 0}) (map inc)) conj [42 nil 3])
(transduce (comp (transplace {nil -1}) (partition-by pos?)) conj [1 2 3 0 5 6])

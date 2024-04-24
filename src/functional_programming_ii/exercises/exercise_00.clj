(ns functional-programming-ii.exercises.exercise-00)


(comment "Implementieren Sie eine Funktion, die zu einer Sequenz alle Kombinationen von zwei ihrer
 Elemente bestimmt. Duplikate in der Eingabe müssen nicht betrachtet werden.")

(comment "Implement a function, that takes a sequence and returns all combinations of two of it's
elements. Duplicates in the input may be ignored.")

(defn combinations [v]
  (if (empty? v)
    #{}
    (let [[h & t] v]
      (into (combinations t) (for [e t] [h e])))))

(assert (= (combinations [1 2 3 4])
           #{[1 2] [1 3] [1 4] [2 3] [2 4] [3 4]}))



(comment "Implementieren Sie eine Funktion compress, die eine beliebige Sequenz nach den
Lauflängen kodiert.
Implementieren Sie eine Funktion decompress, die die Lauflängenkodierung wieder expandiert.")

(comment "Implement a function compress, that encodes a sequence according to RLE.
Implement the matching function decompress, that expands an encoded sequence.")

(comment
 "Expanded: [1 3 3 3 3 3 3 3 7],
Encoded: [1 1 7 3 1 7]")                                    ; should actually be reversed

(defn decompress [v]
  (flatten (for [[r e] (partition 2 v)] (repeat r e))))

(defn compress [v]
  (flatten (for [c (partition-by identity v)] [(count c) (first c)])))

(assert (= (decompress [1 1 7 3 1 7])
           [1 3 3 3 3 3 3 3 7]))

(assert (= (compress [1 3 3 3 3 3 3 3 7])
           [1 1 7 3 1 7]))


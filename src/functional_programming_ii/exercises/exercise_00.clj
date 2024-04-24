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


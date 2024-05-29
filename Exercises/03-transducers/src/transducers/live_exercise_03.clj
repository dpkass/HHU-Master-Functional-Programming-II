(ns transducers.live-exercise-03)


(comment
 "Es sollen folgende Transformationen auf einer Liste von Zahlen durchgeführt werden:
 • alle ungeraden Zahlen entfernen
 • übrige Zahlen verdoppeln
 • zwischen diese Zahlen ein Komma (Character \\,) einfügen
 • mit str zusammenführen")

(defn double-even-str [coll]
  (->> coll
       (filter even?)
       (map #(* 2 %))
       (interpose \,)
       (apply str)))

(double-even-str (range 20))



(comment
 "Was tut der folgende, statebehaftete Transducer? Was ist ein möglicher Aufruf?"

 (defn xyz []
   (fn [xf]
     (let [p (atom ::none)]
       (fn
         ([] (xf))
         ([a] (xf a))
         ([a e]
          (let [pp @p]
            (reset! p e)
            (if (= pp e)
              a
              (xf a e)))))))))

(comment "removes duplicates saved in p")



(comment
 " Implementieren Sie eine Funktion (transplace m), die eine Map m als Argument bekommt und 
 einen Transducer zurückgibt. Wenn ein Element als Schlüssel in m vorhanden ist, soll es durch 
 den assoziierten Wert ersetzt werden, ansonsten soll das originale Element verwendet werden.
 Die Funktion replace darf dabei nicht benutzt werden.")

; see other exercise



(comment
 "Implementieren Sie einen Transducer (transpeat n), der alle Elemente n mal wiederholt."

 user=> (transduce (transpeat 3) conj [:x :y :z])
 [:x :x :x :y :y :y :z :z :z]

 ;; early termination
 user=> (transduce (comp (take 3) (transpeat 2))
                   conj [:x :y :z])
 [:x :x :y :y :z :z]

 user=> (transduce (comp (transpeat 2) (take 3))
                   conj [:x :y :z])
 [:x :x :y])

(defn transpeat [n]
  (fn [rf]
    (fn
      ([] (rf))
      ([a] (rf a))
      ([a e] (reduce rf a (repeat n e))))))

(transduce (transpeat 3) conj [:x :y :z])
(transduce (comp (take 3) (transpeat 2)) conj [:x :y :z])
(transduce (comp (transpeat 2) (take 3)) conj [:x :y :z])

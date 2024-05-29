
"Es sollen folgende Transformationen auf einer Liste von Zahlen durchgeführt
 werden:
 • alle ungeraden Zahlen entfernen
 • übrige Zahlen verdoppeln
 • zwischen diese Zahlen ein Komma (Character \,) einfügen
 • mit str zusammenführen"


(defn do-stuff1 [c]
  (transduce 
    (comp (filter even?)
          (map (partial * 2)) 
          (interpose \,))
    str
    c))

(defn do-stuff2 [c]
  (transduce 
    (comp (filter even?)
          (map (partial * 2)) 
          (interpose \,))
    (fn ([] (StringBuilder.))
        ([a] (str a))
        ([a e] (.append ^StringBuilder a ^Long e)))
    c))

(use 'criterium.core)

; (quick-bench (do-stuff1 (range 100000)))
; (quick-bench (do-stuff2 (range 100000)))


(use 'clojure.reflect)
(use 'clojure.pprint)
(print-table (:members (reflect (StringBuilder.))))


 
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
              (xf a e))))))))
 
 
(use 'clojure.repl)
(source dedupe)
(doc volatile!)
 
 
 (comment
   "Implementieren Sie eine Funktion (transplace m), die eine Map m als
    Argument bekommt und einen Transducer zurückgibt. Wenn ein Element als
    Schlüssel in m vorhanden ist, soll es durch den assoziierten Wert ersetzt
    werden, ansonsten soll das originale Element verwendet werden.
    Die Funktion replace darf dabei nicht benutzt werden."
 
   user=> (transduce (transplace {:y :a}) conj [:x :y :z])
   [:x :a :z]
 
   user=> (transduce (comp (transplace {nil 0}) (map inc))
                     conj
                     [42 nil 3])
   [43 1 4]
 
   user=> (transduce (comp (transplace {nil -1})
                           (partition-by pos?))
                     conj
                     [1 2 3 0 5 6])
   [[1 2 3] [0] [5 6]])
 
(defn transplace [m]
  (fn [step]
    (fn ([] (step))
        ([a] (step a))
        ([a e] (step a (if (contains? m e) (get m e) e))))))
 

(reduce (fn [a e]
          (if (< 100 e)
            (reduced e)
            a))
        0
        (range 10000)
        )

(defn transpeat [n]
  (fn [step]
    (fn ([] (step))
        ([a] (step a))
        ([a e] (loop [nn n
                      aa a]
                 (println aa nn e)
                 (if (or (reduced? aa) (zero? nn))
                   aa
                   (recur (dec nn) (step aa e))))))))

(defn transpeat' [n]
  (fn [step]
    (fn ([] (step))
        ([a] (step a))
        ([a e] (let [x (reduce step  ;; frisst das reduced-Signal!
                               a
                               (repeat n e)
                               )]
                 (println x))))))

(source take)
(comment
  "Implementieren Sie einen Transducer (transpeat n), der alle Elemente n mal wiederholt. 
  Beispielaufruf:"

  user=> (transduce (transpeat 3) conj [:x :y :z])
  [:x :x :x :y :y :y :z :z :z]
  ;; early termination

  user=> (transduce (comp (take 3) (transpeat 2))
                    conj [:x :y :z])
  [:x :x :y :y :z :z]

  user=> (transduce (comp (transpeat' 2) (take 3))
                    conj [:x :y :z])
  [:x :x :y])



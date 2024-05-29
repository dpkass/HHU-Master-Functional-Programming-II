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


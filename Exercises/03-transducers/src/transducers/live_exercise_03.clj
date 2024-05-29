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


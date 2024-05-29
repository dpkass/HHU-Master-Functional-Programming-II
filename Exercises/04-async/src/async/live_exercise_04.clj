(ns async.live-exercise-04
  (:use [clojure.core.async]))



(comment
 "Schreiben Sie eine Funktion (receive-n c n), die n Elemente von einem channel nimmt und diese als seq zurück gibt.
 Beispiel:"

 user=> (let [c (chan)]
          (dotimes [i 10]
            (thread
             (Thread/sleep 10)
             (>!! c i)))
          (receive-n c 10))
 (2 3 0 1 5 6 4 7 8 9))

(defn receive-n [c n]
  (repeatedly n #(<!! c)))

(let [c (chan)]
  (dotimes [i 10]
    (thread
     (Thread/sleep 10)
     (>!! c i)))
  (receive-n c 10))



(comment
 "Koordinieren Sie die geteilte Ressource (stdout), indem Sie core.async Channel verwenden."

 (do (future (dotimes [x 100] (println "(..." x "...)")))
     (future (dotimes [x 100] (println "(..." x "...)")))))

(let [c (chan)]
  (go (while :true (println (<! c))))
  (future (dotimes [x 100] (>!! c (str "(..." x "...)"))))
  (future (dotimes [x 100] (>!! c (str "(..." x "...)")))))

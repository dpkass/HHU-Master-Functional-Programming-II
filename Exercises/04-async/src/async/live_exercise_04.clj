(ns async.live-exercise-04
  (:require [clojure.core.async :as async]))



(comment
 "Schreiben Sie eine Funktion (receive-n c n), die n Elemente von einem channel nimmt und diese als seq zurück gibt.
 Beispiel:"

 user=> (let [c (async/chan)]
          (dotimes [i 10]
            (async/thread
             (Thread/sleep 10)
             (async/>!! c i)))
          (receive-n c 10))
 (2 3 0 1 5 6 4 7 8 9))

(defn receive-n [c n]
  (repeatedly n #(async/<!! c)))

(let [c (async/chan)]
  (dotimes [i 10]
    (async/thread
     (Thread/sleep 10)
     (async/>!! c i)))
  (receive-n c 10))



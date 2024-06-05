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



(comment
 "Schreiben Sie ein Makro (wuäh!) (do-or-timeout n body), das versucht den body zu evaluieren.
 Falls dies nach n Millisekunden noch nicht gelungen ist, soll :timeout zurückgegeben werden. Sie
 können davon ausgehen, dass der body nicht nil zurück gibt."

 user=> (do-or-timeout 1000 (do (Thread/sleep 100) (inc 1)))
 2

 user=> (do-or-timeout 1000 (do (Thread/sleep 1001) (inc 1)))
 :timeout)


(defmacro do-or-timeout [n body]
  `(let [c# (timeout ~n)]
     (go (>! c# ~body))
     (or (<!! c#) :timeout)))

(do-or-timeout 1000 (do (Thread/sleep 100) (inc 1)))
(do-or-timeout 1000 (do (Thread/sleep 1001) (inc 1)))



(comment
 "Implementieren Sie die Funktionen (mult c) und (tap a c). mult gibt ein Objekt zurück, dass als
 erstes Argument in tap weitergereicht wird. Alle Elemente auf c sollen dann auf allen tappenden
 Channels weitergereicht werden."

 (def c (chan)) (def out1 (chan)) (def out2 (chan))

 (def a (mult c))
 (tap a out1) (tap a out2)

 (go (>! c 42))
 (<!! out1)
 (<!! out2))

(defn mult [c]
  (let [outs (atom [])]
    (go (while :true
          (let [in (<! c)]
            (doseq [out outs] (>! out in)))))))

(defn tap [a c]
  (swap! a conj c))


(do
  (def c (chan)) (def out1 (chan)) (def out2 (chan))

  (def a (mult c))
  (tap a out1) (tap a out2))

(go (>! c 42))
(<!! out1)
(<!! out2)

(require '[clojure.core.async :as async :refer [go go-loop
                                                <! >! <!! >!! 
                                                alts! alts!! timeout
                                                chan]])


; Schreiben Sie eine Funktion (receive-n c n), die n Elemente von
; einem channel nimmt und diese als seq zurück gibt.
;
; Beispiel:

(defn receive-n [c n]
  (doall (for [i (range n)]
           (<!! c))))

(defn receive-n [c n]
  (<!! (go-loop [i n
                 acc []]
                (if (zero? i)
                  acc
                  (recur (dec i) (conj acc (<! c)))))))

 user=> (let [c (async/chan)]
            (dotimes [i 10]
              (async/thread
                (Thread/sleep (* i 10))
                (async/>!! c i)))
            (receive-n c 10))
 (2 3 0 1 5 6 4 7 8 9)

; Achtung: Die Reihenfolge kann sich hier ändern



; ----------------

; Koordinieren Sie die geteilte Ressource (stdout),
; indem Sie core.async Channel verwenden.

(do (future (dotimes [x 100] (println "(..." x "...)")))
    (future (dotimes [x 100] (println "(..." x "...)")))
    (Thread/sleep 100))


(def print-channel (chan))

(go-loop []
         (if-let [s (<! print-channel)]
           (do (println s) (recur))
           [(println "Channel closed for construction, Techniker ist informiert")]))

(defn println-superduper [& args]
  (>!! print-channel (apply str (interpose \space args))))

(do (future (dotimes [x 100] (println-superduper "(..." x "...)")))
    (future (dotimes [x 100] (println-superduper "(..." x "...)")))
    (Thread/sleep 100))






; ----------------

; Schreiben Sie ein Makro (wuäh!) do-or-timeout n body),
; das versucht den body zu evaluieren.
; Falls dies nach n Millisekunden noch nicht gelungen ist,
; soll :timeout zurückgegeben werden.
; Sie können davon ausgehen, dass der body nicht nil zurück gibt.

(defmacro do-or-timeout [n & body]
  `(let [[v# c#] (alts!! [(go ~@body)
                          (timeout ~n)])]
     (if (nil? v#) :timeout v#)))

user=> (do-or-timeout 1000
                      (do (Thread/sleep 100)
                          (inc 1)))
2

user=> (do-or-timeout 2
                      (do (Thread/sleep 3)
                          (inc 1)))
:timeout





; Implementieren Sie die Funktionen (mult c) und (tap a c).
; mult gibt ein Objekt zurück, dass als erstes Argument in tap weitergereicht wird.
; Alle Elemente auf c sollen dann auf allen tappenden Channels weitergereicht werden.
; 
 (def c (chan))
 (def out1 (chan)) 
 (def out2 (chan))


(defn mult [c]
  (let [a (atom {:in c, :out []})]
    (go-loop []
      (let [v (<! c)]
        (doseq [cc (:out @a)]
          (>! cc v)))
      (recur))
    a))

(defn tap [a c]
  (swap! a update :out conj c))

 (def a (mult c))
 (tap a out1)  (tap a out2)
 
 (go (>! c 42))
 (<!! out1) ;; 42
 (<!! out2) ;; 42

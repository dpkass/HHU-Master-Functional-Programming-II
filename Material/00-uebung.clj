(defn combinations [v]
  (if (empty? v)
    #{}
    (let [e (first v)]
      (into (combinations (rest v))
            (for [e2 (rest v)]
              [e e2])))))

(combinations [1 2 3 4])


(defn compress [v]
  (loop [v v
         acc []]
    (if (empty? v)
      acc
      (let [x (first v)
            [xs xsn't] (split-with #(= x %) v)]
        (recur 
          xsn't
          (conj acc (count xs) x))))))

(defmacro forcat [bindings & body]
  `(apply concat
          (for ~bindings [~@body])))
`bindings
(defn compress [v]
  (forcat [c (partition-by identity v)]
    (count c) (first c)))

(defn decompress [v]
  (mapcat ; (fn [[coun't element]] (repeat coun't element))
          (partial apply repeat)
          (partition 2 v)))

(decompress [1 1 7 3 1 7])
(quote (1 3 3 3 3 3 3 3 7 1))
(decompress (compress '(1 3 3 3 3 3 3 3 7 1)))


(use 'clojure.repl)
(source iterate)
(defn oscil2 [v fs]
  (cons v
        (lazy-seq
          (oscil2 ((first fs) v)
                  (rest fs)))))

(defn oscil
  [v & fs]
  (oscil2 v (cycle fs)))

(type  (rest (oscil 1 #(- % 2) #(+ 5 %))))

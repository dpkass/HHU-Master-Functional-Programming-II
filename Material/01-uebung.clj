(require '[clojure.test.check :as tc]
         '[clojure.test.check.generators :as gen]
         '[clojure.test.check.properties :as prop #?@(:cljs [:include-macros true])])



(defn levenshtein [[h1 & t1 :as s1] [h2 & t2 :as s2]]
  (cond (empty? s1) (count s2)
        (empty? s2) (count s1)
        :otherwise (min
                    (inc (levenshtein t1 s2))
                    (inc (levenshtein s1 t2))
                    (if (= h1 h2)
                      (levenshtein t1 t2)
                      (inc (levenshtein t1 t2))))))

(defn mapHelper "Which letter should be kept" [hold keep]
  (if (= hold keep)
    hold
    false))

(defn levenshtein-Helper "compares two strings, keeps equal letters and adds all not equal up" [stringSeq1 stringSeq2]
  (count (filter #(= false %) (map mapHelper stringSeq1 stringSeq2))))

(defn prepareString "makes the strings equaly long" [string lengthOfTheOther]
  (let [dif (- (count string) lengthOfTheOther)]
    (if (< dif 0)
      (concat (seq string) (repeat (- dif) " "))
      (seq string))))

(defn levenschtein "prepare the strings and compares them in two different kinds, return result" [string1 string2]
  (let [prepString11 (prepareString string1 (count string2))
        prepString12 (prepareString string2 (count string1))
        prepString21 (prepareString (reverse string1) (count string2))
        prepString22 (prepareString (reverse string2) (count string1))
        x (levenshtein-Helper prepString11 prepString12)
        y (levenshtein-Helper prepString21 prepString22)]
    (if (< x y)
      x
      y)))

(levenschtein "foo" "bar")
(levenschtein "simple" "easy")
(levenschtein "foo" "fon")
(levenschtein "" "fon")

(def prop (prop/for-all [a gen/string-alphanumeric
                         b gen/string-alphanumeric]
                        (= (levenshtein a b) ;; <-- Orakel
                           (levenschtein a b))))
(tc/quick-check 10000 prop :max-size 5)

(levenschtein "ZZZZZ" "0ZZZZZ0")




(defmacro forcat [bindings & body]
  `(apply concat
          (for ~bindings [~@body])))
(defn compress [v]
  (forcat [c (partition-by identity v)]
    (count c) (first c)))

(defn decompress [v]
  (mapcat ; (fn [[coun't element]] (repeat coun't element))
          (partial apply repeat)
          (partition 2 v)))

(compress (for [_ (range 10)]
  (rand-int 2)
  ))

(tc/quick-check
  10000
  (prop/for-all [a (gen/vector gen/small-integer)]
                (= a (decompress (compress a))))) ;; Funktion + Umkehrfunktion

#_(tc/quick-check
  10000
  (prop/for-all [a (gen/such-that 
                     #(even? (count %))
                     (gen/vector gen/nat))]
                (= a (compress (decompress a))))) ;; Funktion + Umkehrfunktion


(frequencies [1 3 54  6 3 2  8  4 2 1 3 4 :a :b :a])
(frequencies [])
(def a [0])
(def res (frequencies a))
(frequencies [Double/NaN
              Double/NaN
              Double/NaN
              Double/NaN
              Double/NaN
              ])
(tc/quick-check 
  10000
  (prop/for-all [a (gen/vector gen/double)]
                (let [res (frequencies a)]
                
                  (and (map? res)
                       ;(let [settyMcSetFace (set a)]
                       ;  (every? #(contains? settyMcSetFace %) res))
                       (every? number? (vals res))
                       ; (filter ...) ;; richtige Anzahl (TODO) 
                       (= (set (keys res)) (set a))
                       ; nichts verloren
                       ))))

(use 'clojure.pprint)
(pprint (gen/sample
  (gen/recursive-gen 
    (fn [inner] 
      (gen/fmap list*
                (gen/tuple (gen/elements '[+ - * /])
                           inner
                           inner)))
  gen/nat)))



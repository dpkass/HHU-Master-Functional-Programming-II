(ns reframe.aufgaben.core)

(defn next-player [cp] (if (= cp "X") "O" "X"))

(def lines
  [[0 1 2] [3 4 5] [6 7 8]
   [0 3 6] [1 4 7] [2 5 8]
   [0 4 8] [2 4 6]])

(defn select-indexes [v idxs]
  (map #(v %) idxs))

(defn win? [squares]
  (some
   (fn [line] (let [line-vals (select-indexes squares line)]
                (and (first line-vals) (apply = line-vals))))
   lines))

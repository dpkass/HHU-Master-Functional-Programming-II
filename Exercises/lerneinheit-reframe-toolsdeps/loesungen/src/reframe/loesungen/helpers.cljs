(ns reframe.loesungen.helpers)

(def init-squares
  (vec (repeat 9 "")))

(def lines [[0 1 2]
            [3 4 5]
            [6 7 8]
            [0 3 6]
            [1 4 7]
            [2 5 8]
            [0 4 8]
            [2 4 6]])

(defn check [v]
  (if (= (count (filter #(not= "" %) v)) 3)
    (apply = v)
    false))

(defn won? [squares]
  (->> lines
       (map (fn [line] (vec (map #(get squares %) line))))
       (map check)
       (some #(= % true))
       (boolean)))

(defn playable? [squares id]
  (= (get squares id) ""))

(defn next-player [xIsNext]
  (if xIsNext
    "X"
    "O"))

(defn current-player [xIsNext]
  (if xIsNext
    "O"
    "X"))

(defn count-unplayed [squares]
  (count (filter #(= % "") squares)))

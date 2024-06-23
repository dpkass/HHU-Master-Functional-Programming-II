(ns reframe.aufgaben.events
  (:require [re-frame.core :refer [reg-event-db]]))

(reg-event-db
 ::initialize-db
 (fn [_ _] {:squares (vec (repeat 9 nil))
            :current-player "X"}))


(defn next-player [cp] (if (= cp "X") "O" "X"))

(reg-event-db
 ::click-square
 (fn [db [_ num]]
   (let [cp (:current-player db)]
     (-> db
         (assoc-in [:squares num] cp)
         (assoc :current-player (next-player cp))))))

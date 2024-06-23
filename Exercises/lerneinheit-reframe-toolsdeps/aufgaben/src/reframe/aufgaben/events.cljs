(ns reframe.aufgaben.events
  (:require [re-frame.core :refer [reg-event-db]]
            [reframe.aufgaben.core :refer [next-player]]))

(reg-event-db
 ::initialize-db
 (fn [_ _] {:squares        (vec (repeat 9 nil))
            :current-player "X"
            :history        []}))

(reg-event-db
 ::click-square
 (fn [db [_ sq]]
   (let [{:keys [current-player squares history]} db]
     (-> db
         (assoc-in [:squares sq] current-player)
         (assoc :current-player (next-player current-player))
         (assoc :history (conj history squares))))))

(reg-event-db
 ::return
 (fn [db [_ squares move-num]]
   (let [{:keys [history]} db]
     (-> db
         (assoc :squares squares)
         (assoc :history (subvec history 0 move-num))))))

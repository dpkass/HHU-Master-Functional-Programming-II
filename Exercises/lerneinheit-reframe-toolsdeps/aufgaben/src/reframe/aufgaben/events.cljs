(ns reframe.aufgaben.events
  (:require [re-frame.core :refer [reg-event-db]]
            [reframe.aufgaben.core :refer [next-player win?]]))

(reg-event-db
 ::initialize-db
 (fn [_ _] {:squares        (vec (repeat 9 nil))
            :current-player "X"
            :history        []
            :winner         nil}))

(reg-event-db
 ::click-square
 (fn [db [_ sq]]
   (let [{:keys [current-player squares history]} db
         new-db (-> db
                    (assoc-in [:squares sq] current-player)
                    (update :current-player next-player)
                    (assoc :history (conj history squares)))]
     (assoc new-db :winner (when (win? (:squares new-db)) current-player)))))

(reg-event-db
 ::return
 (fn [db [_ squares move-num]]
   (let [{:keys [history]} db]
     (-> db
         (assoc :squares squares)
         (assoc :history (subvec history 0 move-num))))))

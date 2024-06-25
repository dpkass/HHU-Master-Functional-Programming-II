(ns reframe.aufgaben.events
  (:require [re-frame.core :refer [reg-event-db]]
            [reframe.aufgaben.core :refer [get-winner next-player]]))

(reg-event-db
 ::initialize-db
 (fn [_ _] {:squares        (vec (repeat 9 nil))
            :current-player "X"
            :history        []
            :winner         nil}))

(reg-event-db
 ::click-square
 (fn [{:keys [current-player squares winner] :as db} [_ sq]]
   (if (or winner (squares sq))
     db                                                     ; do nothing
     (let [updated-squares (assoc squares sq current-player)]
       (-> db
           (assoc :squares updated-squares)
           (update :current-player next-player)
           (update :history conj squares)
           (assoc :winner (get-winner updated-squares)))))))

(reg-event-db
 ::return
 (fn [db [_ squares move-num]]
   (let [{:keys [history]} db]
     (-> db
         (assoc :squares squares)
         (assoc :history (subvec history 0 move-num))
         (dissoc :winner)))))

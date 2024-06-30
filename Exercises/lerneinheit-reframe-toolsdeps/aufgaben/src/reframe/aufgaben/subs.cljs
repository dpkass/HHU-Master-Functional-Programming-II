(ns reframe.aufgaben.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(def sub (comp deref subscribe))

(reg-sub ::winner #(:winner %))
(reg-sub ::history #(:history %))
(reg-sub ::current-player #(:current-player %))

(reg-sub ::square (fn [db [_ sq]] (get-in db [:squares sq])))

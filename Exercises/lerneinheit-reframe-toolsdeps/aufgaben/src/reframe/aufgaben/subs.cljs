(ns reframe.aufgaben.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(def sub (comp deref subscribe))

(reg-sub
 ::square
 (fn [db [_ sq]] (get-in db [:squares sq])))

(reg-sub ::current-player #(:current-player %))
(reg-sub ::history #(:history %))

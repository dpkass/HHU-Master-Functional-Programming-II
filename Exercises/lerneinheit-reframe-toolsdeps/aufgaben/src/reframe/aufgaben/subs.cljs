(ns reframe.aufgaben.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(def sub (comp deref subscribe))

(reg-sub
 ::square
 (fn [db [_ num]] (get-in db [:squares num])))

(reg-sub ::current-player #(:current-player %))
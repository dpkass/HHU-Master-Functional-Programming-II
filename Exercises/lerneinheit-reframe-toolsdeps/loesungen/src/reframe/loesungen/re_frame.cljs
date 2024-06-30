(ns reframe.loesungen.re-frame
  (:require
   [re-frame.core :as rf]
   [reframe.loesungen.helpers :as h]))

;; HANDLERS

(defn move-handler
  [db [_ index]]
  (let [{:keys [squares xIsNext history]} db]
    (if (and (h/playable? squares index)
             (not (h/won? squares)))
      (let [next-squares (assoc squares index (h/next-player xIsNext))
            next-xIsNext (not xIsNext)
            next-history (conj (vec (take (- 9 (h/count-unplayed squares)) history))
                               [next-squares next-xIsNext])]
        {:squares next-squares
         :xIsNext next-xIsNext
         :history next-history})
      db)))


(defn time-travel-handler
  [db [_ squares xIsNext]]
  (assoc db :squares squares :xIsNext xIsNext))

;; EVENTS

(rf/reg-event-db
 :init
 (fn [_ _]
   {:squares h/init-squares
    :xIsNext true
    :history []}))

(rf/reg-event-db
 :reset
 (fn [db _]
   (assoc db :squares h/init-squares :xIsNext true)))

(rf/reg-event-db
 :move
 move-handler)

(rf/reg-event-db
 :time-travel
 time-travel-handler)

;; SUBS

(rf/reg-sub
 :query-squares
 (fn [db _] (:squares db)))

(rf/reg-sub
 :query-xIsNext
 (fn [db _] (:xIsNext db)))

(rf/reg-sub
 :query-history
 (fn [db _] (:history db)))

(rf/reg-sub
 :query-squares-value
 :<- [:query-squares]
 (fn [squares [_ index]]
   (get squares index)))

;; KOMPONENTEN

(defn Square [index]
  (let [value @(rf/subscribe [:query-squares-value index])]
    [:button {:className "square"
              :on-click #(rf/dispatch [:move index])}
     value]))

(defn Status []
  (let [squares @(rf/subscribe [:query-squares])
        xIsNext @(rf/subscribe [:query-xIsNext])]
    (if (h/won? squares)
      [:p "Winner: " (h/current-player xIsNext)]
      (if (= (h/count-unplayed squares) 0)
        [:p "Draw!"]
        [:p "Next player: " (h/next-player xIsNext)]))))

(defn Board []
  [:div
   [:div {:className "board-row"}
    [Square 0]
    [Square 1]
    [Square 2]]
   [:div {:className "board-row"}
    [Square 3]
    [Square 4]
    [Square 5]]
   [:div {:className "board-row"}
    [Square 6]
    [Square 7]
    [Square 8]]])

(defn history-button [idx [squares xIsNext]]
  ^{:key (inc idx)}
  [:li
   [:button {:on-click
             #(rf/dispatch [:time-travel squares xIsNext])}
    "Go to move #" (inc idx)]])

(defn Reset []
  ^{:key 0} [:li
             [:button {:on-click #(rf/dispatch [:reset])}
              "Go to Game start"]])

(defn Game []
  (let [history @(rf/subscribe [:query-history])]
    [:div {:className "game"}
     [:div {:className "game-board"}
      [Board]]
     [:div {:className "game-info"}
      [:div
       [Status]
       [:ol
        [Reset]
        (map-indexed history-button history)]]]]))

(defn init-db []
  (rf/dispatch-sync [:init]))

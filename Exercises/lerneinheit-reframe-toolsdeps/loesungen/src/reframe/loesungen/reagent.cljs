(ns reframe.loesungen.reagent
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reframe.loesungen.helpers :as h]))

;; STATE

(defonce state (atom {:squares h/init-squares
                      :xIsNext true
                      :history []}))

(defn reset-state []
  (swap! state assoc
         :squares h/init-squares
         :xIsNext true))

;; COMPONENTS

(defn Square [index]
  (let [{:keys [xIsNext squares history]} @state]
    [:button {:className "square"
              :on-click (fn []
                          (if (and (h/playable? squares index)
                                   (not (h/won? squares)))
                            (let [next-squares (assoc squares index (h/next-player xIsNext))
                                  next-xIsNext (not xIsNext)
                                  next-history (conj (vec (take (- 9 (h/count-unplayed squares)) history)) [next-squares next-xIsNext])]
                              (reset! state {:squares next-squares
                                             :history next-history
                                             :xIsNext next-xIsNext}))))}
     (get squares index)]))


(defn Status []
  (let [{:keys [squares xIsNext]} @state]
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
             #(swap! state assoc
                     :squares squares
                     :xIsNext xIsNext)}
    "Go to move #" (inc idx)]])

(defn Reset []
  ^{:key 0} [:li
             [:button {:on-click #(reset-state)}
              "Go to game start"]])

(defn Game []
  [:div {:className "game"}
   [:div {:className "game-board"}
    [Board]]
   [:div {:className "game-info"}
    [:div
     [Status]
     [:ol
      [Reset]
      (map-indexed history-button (:history @state))]]]])

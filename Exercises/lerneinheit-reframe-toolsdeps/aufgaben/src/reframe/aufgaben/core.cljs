(ns reframe.aufgaben.core
  (:require
   [re-frame.core :refer [dispatch dispatch-sync subscribe]]
   [reagent.dom :as rdom]
   [reframe.aufgaben.events :as events]
   [reframe.aufgaben.subs :as subs :refer [sub]]))

(defn Square [num]
  (let [value (sub [::subs/square num])]
    [:button.square
     (when (not value) {:on-click #(dispatch [::events/click-square num])})
     value]))

(defn Status []
  (str "Next player: " (sub [::subs/current-player])))

;; Diese Komponente kann (muss nicht) so bleiben.
(defn Board []
  [:div
   [Status]
   [:div.board-row [Square 0] [Square 1] [Square 2]]
   [:div.board-row [Square 3] [Square 4] [Square 5]]
   [:div.board-row [Square 6] [Square 7] [Square 8]]])

(defn Game []
  [:div {:className "game"}
   [:div {:className "game-board"}
    [Board]]
   [:div {:className "game-info"}
    [:div ;; Status
     [:ol ;; TODO Zeitreise
      ]]]])

(dispatch-sync [::events/initialize-db])
(rdom/render [Game]
             (. js/document (getElementById "app")))

(ns reframe.aufgaben.core
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]
   [re-frame.core :as rf]))

(defn Square [value]
  [:button {:className "square"}
   ;; TODO Hier den Parameter anzeigen lassen!
   ])

(defn Status []
  ;; TODO
  "Next player: X")

;; Diese Komponente kann (muss nicht) so bleiben.
(defn Board []
  [:div
   [Status]
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

(defn Game []
  [:div {:className "game"}
   [:div {:className "game-board"}
    [Board]]
   [:div {:className "game-info"}
    [:div ;; Status
     [:ol ;; TODO Zeitreise
      ]]]])

(rdom/render [Game]
             (. js/document (getElementById "app")))

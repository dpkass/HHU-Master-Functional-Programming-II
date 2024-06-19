(ns reframe.loesungen.core
  (:require
   [reframe.loesungen.reagent :as r :refer [Game] :rename {Game ReagentGame}]
   [reframe.loesungen.re-frame :as re-frame :refer [Game init-db] :rename {Game ReframeGame}]
   [reagent.dom :as rdom]))

;; Initialisieren der re-frame app-db Datenbank
(init-db)

;; Hier werden beide Apps gezeigt, wird aber von den Studenten nicht verlangt.
;; Sie sollen ihre vorherige Lösung refactorn. Es wird empfohlen Version Control zu benutzen.

(rdom/render [ReagentGame]
             (. js/document (getElementById "app1")))

(rdom/render [ReframeGame]
             (. js/document (getElementById "app2")))

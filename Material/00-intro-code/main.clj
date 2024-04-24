(ns main
  (:require [clojure.data.csv :as csv]))


;; intended learning outcome:
;; Student shall be able to describe what the presented libraries/tools are useful for.

(comment


;; reader only has a limited livespan; have to force the sequence with doall
(def data (with-open [reader (clojure.java.io/reader "info1.csv")]
            (doall
              (csv/read-csv reader))))

(use 'clojure.repl)
data
(use 'clojure.pprint)
(clojure.pprint/pprint data)
(def data2 (let [[headings & data] data]
             (map (partial zipmap headings) data)))

data2
(use 'com.rpl.specter)

;; :-(
;; muss ich wohl in den deps.edn einfügen...
;; ...und meine REPL neu starten







;; Nooope! Just Chuck Testa!


;; cool hot chips
;; Clojure CLI Tools müssen recht neu sein
(use 'clojure.repl.deps)
(add-lib 'com.rpl/specter {:mvn/version "1.1.4"})

(use 'com.rpl.specter)
(doc ->)
(doc ->>)


;; Konzept: Navigator

;; man kann bestimmte Werte "markieren" und auswählen
data2
(select [ALL "matnr"] data2)
(select [ALL "corrector"] data2)
(select [ALL (pred #(= (% "corrector") "Frodo Scharf")) "matnr"] data2)



;; was man markieren kann, kann man aber auch ändern
(def data3
  (->> data2
       (transform [ALL MAP-KEYS] keyword)

       (transform [ALL MAP-VALS] (fn [x] (if (re-find #"^\d" x) (read-string x) x)))))

data3

;; Frodo Scharf hat alles zu schlecht bewertet
(transform [ALL (pred #(= (% :corrector) "Frodo Scharf")) :points] #(* 100 %) data3)





(add-lib 'vlaaad/reveal)

; depends on your JavaFX availability (also see deps.edn) 
(require '[vlaaad.reveal :as r])
;
;;; open a window that will show tapped values:
(add-tap (r/ui))
; ;; submit value to the window:
(tap> {:will-i-see-this-in-reveal-window? true})
;
(tap> data2)

;; related: Cognitect's REBL (https://docs.datomic.com/cloud/other-tools/REBL.html)


;; https://github.com/incanter/incanter
;; > Incanter is a Clojure-based, R-like statistical computing and graphics environment for the JVM. 
;; main usage: statistics, data visualisation

(require '[incanter.core :as incanter]
         '[incanter.charts :as charts]
         '[incanter.stats :as stats])

(def summed-points 
  (map (fn [[_ rows]] (reduce + (map #(Double/parseDouble %) (map #(get % "points") rows)))) 
       (group-by #(% "matnr") data2)))

(incanter/view (charts/histogram 
                 summed-points
                 :title "Punkte pro Student"
                 :x-label "erreichte Punkte"
                 :nbins 30))
;; there are many other chart types, including scatter (XY) plots, bar charts, line charts, box-plots, ...

;; statistics package includes items such as:
(stats/mean summed-points)
(stats/median summed-points)
(stats/sd summed-points)  ;; Standardabweichung
(stats/variance summed-points) ;; Quadrat der Standardabweichung



(require '[clojure.core.reducers :as rr])

(def x (range 1000000))
(do (doall x) nil) ;; force lazyseq
(time (reduce + (filter even? (map inc x))))
(time (rr/fold + (rr/filter even? (rr/map inc x))))

(add-lib 'criterium/criterium)
(require '[criterium.core :refer [bench quick-bench]])

;; dauert etwas
(do (quick-bench (rr/fold + (rr/filter even? (rr/map inc x))))
    (quick-bench (reduce + (filter even? (map inc x)))))


;; Jupyter Notebook integration
;; zu wackelig, schauen wir uns dieses Jahr nicht an

)

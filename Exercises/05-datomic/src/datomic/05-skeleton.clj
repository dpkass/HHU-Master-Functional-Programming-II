(do
  (require '[clojure.data.csv :as csv]
           '[clojure.java.io :as io])

  (defn select-indices [v idxs]
    (map (fn [idx] (v idx)) idxs)))

;; you can download + extract the datasets from imdb:
;; https://datasets.imdbws.com/
;; we use title.basics.tsv and title.ratings.tsv here.

;;["tconst" "titleType" "primaryTitle" "originalTitle" "isAdult" "startYear" "endYear" "runtimeMinutes" "genres"]

;; !!!
;; Warning: the dataset may consume quite a lot of RAM
;; with an in-memory data-base, the Java process uses ~7 GB of RAM
;; feel free to take / drop any amount from the seq (csv/read-csv ..) returns.
;; !!!

(def titles
  (with-open [reader (io/reader "src/datomic/title.basics.tsv")]
    (doall
     (transduce
      (comp
       (filter (fn [row] (or (= "titleType" (nth row 1))
                             (= "movie" (nth row 1)))))
       (map #(select-indices % [0 2 5])))
      conj
      (csv/read-csv reader :separator \tab :quote 0)))))

(take 10 titles)

(def score
  (with-open [reader (io/reader "src/datomic/title.ratings.tsv")]
    (doall
     (csv/read-csv reader :separator \tab :quote \|))))

(take 10 score)


(require '[datahike.api :as d])


;; use the filesystem as storage medium
(def cfg {:store {:backend :file :path "/tmp/fp/datomic/db"}})
;(def cfg {:store {:backend :mem}})

;; create a database at this place, per default configuration we enforce a strict
;; schema and keep all historical data
(d/create-database cfg)

(def conn (d/connect cfg))

;; the first transaction will be the schema we are using
;; you may also add this within database creation by adding :initial-tx
;; to the configuration
(d/transact conn [{:db/ident       :title
                   :db/valueType   :db.type/string
                   :db/cardinality :db.cardinality/one}
                  {:db/ident       :year
                   :db/valueType   :db.type/long
                   :db/cardinality :db.cardinality/one}
                  {:db/ident       :rating
                   :db/valueType   :db.type/ref
                   :db/cardinality :db.cardinality/many}    ;; TODO: add score from Rotten Tomatoes
                  {:db/ident       :score
                   :db/valueType   :db.type/float
                   :db/cardinality :db.cardinality/one}
                  {:db/ident       :num-votes
                   :db/valueType   :db.type/long
                   :db/cardinality :db.cardinality/one}])

(defn null-check-parse [parse-fn val]
  (if (= val "\\N") nil (parse-fn val)))
(defn to-long [val] (null-check-parse #(Long/parseLong %) val))
(defn to-float [val] (null-check-parse #(Float/parseFloat %) val))

(defn to-txs [[_heads & titles] [_heads2 & scores]]
  ;; TODO:
  ;;  hints:
  ;;  - adding attribute may look like:
  ;;  (transact conn [[:db/add -1 :name "Ivan"]
  ;;                  [:db/add -1 :likes "fries"]
  ;;                  [:db/add -1 :likes "pizza"]
  ;;                  [:db/add -1 :friend 296]])
  ;;  - strings and negative integers can be used as temporary IDs
  ;;  (in a single transaction, equal tempids will be replaced with the same unused yet entid)
  ;;  - reverse attribute name can be used if you want a created entity to become
  ;;  a value in another entity reference
  ;;  (transact conn [{:db/id  -1
  ;;                   :name   "Oleg"
  ;;                   :_friend 296}])
  ;;  equivalent to
  ;;  (transact conn [[:db/add  -1 :name   "Oleg"]
  ;;                  [:db/add 296 :friend -1]])
  (concat
   (for [[id title year] titles]
     (let [year-long (to-long year)]
       (concat
        [:db/add id :title title]
        (when year-long
          [:db/add id :year year-long]))))
   (for [[id score votes] scores]
     {:score     (to-float score)
      :num-votes (to-long votes)
      :_rating   id})))



(def txs (to-txs titles score))


;; inspect the log config of the timbre library
(do taoensso.timbre/*config*)
;; re-binding the log level to avoid massive log output
(binding
 [taoensso.timbre/*config*
  (assoc taoensso.timbre/*config* :min-level :info)]
  (d/transact conn txs)
  nil)

;; search the data
(d/q '[:find ?year
       :where [?e :year ?year]
       [?e :rating ?r]
       [?r :score 5.7]]
     @conn)

(take 5 (d/q '[:find (pull ?r [*])
               :where [?e :rating ?r]]
             @conn))
;; you might need to release the connection for specific stores
(d/release conn)

;; clean up the database if it is not need any more
(d/delete-database cfg)

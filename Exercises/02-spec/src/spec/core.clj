(ns spec.core
  (:require [clojure.spec.alpha :as s])
  (:require [clojure.spec.test.alpha :as stest]))

(comment "Task 1: (Specs)")

(comment "a) Specify a spec for a player in a card game, who should look something like this:

  {:cards/name \"Philipp\"
   :cards/hand [[3 :clubs] [:ace :spades]]}

Any arbitrary string is allowed as a name. A non-empty sequence of 2-tuples is associated under
:cards/hand. The first entry is the value of the card, the second is the suit.")

(s/def :cards/name string?)
(s/def ::card
  (s/tuple
   (into #{:ace :king :queen :jack} (range 2 11))
   #{:hearts :diamonds :clubs :spades}))
(s/def :cards/hand (s/coll-of ::card))
(s/def ::player (s/keys :req [:cards/name :cards/hand]))

(s/valid? ::player {:cards/name "Philipp"
                    :cards/hand [[3 :clubs] [:ace :spades]]})
(s/explain ::player {:cards/name 123
                     :cards/hand [[3 :club] [:ace :spade]]})


(comment "b) Write a function discard, which receives a set of players as well as a name and a
playing card. If this playing card is currently in the hand of the named player, it is discarded.")

(defn drop-card [card player]
  (update player :cards/hand #(remove #{card} %)))

(defn is-player [name player]
  (= name (:cards/name player)))

(defn map-if [pred f coll]
  (map #(if (pred %) (f %) %) coll))

(defn discard [players name card]
  (map-if (partial is-player name) (partial drop-card card) players))

(discard [{:cards/name "Philipp"
           :cards/hand [[3 :clubs] [:ace :spades]]}
          {:cards/name "Taha"
           :cards/hand [[:queen :diamond] [:king :diamond] [:ace :hearts]]}]
         "Philipp"
         [3 :clubs])


(comment "c) Annotate discard with specs, which ensure that if called correctly, a set of players
is returned by the function.")

(s/def ::players (s/coll-of ::player))
(s/fdef discard
        :args (s/cat
               :players ::players
               :name :cards/name
               :card ::card)
        :ret ::players)

(s/exercise-fn `discard)


(comment "d) Is it possible to detect a problem with generated tests?

Note: you can limit the size of the generated values with
(stest/check ‘discard {:clojure.spec.test.check/opts {:max-elements 4 :max-size 3}})
Otherwise, testing can take a very long time..")

(stest/check `discard {:clojure.spec.test.check/opts {:max-elements 4 :max-size 3}})

; no errors

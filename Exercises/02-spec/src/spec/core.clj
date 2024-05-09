(ns spec.core
  (:require [clojure.spec.alpha :as s]))

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

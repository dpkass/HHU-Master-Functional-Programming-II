(ns spec.core
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]
            [clojure.string :as str]))

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




(comment "Task 2: (Specs II)")

(comment "In exercise 9.2, a test.check generator was used to generate users with identifiers.

Write a spec for a user. A user is represented by a map containing their first- and last name and
an identifier. The identifier consists of the first two characters of the first name, the first
three characters of the last name, and three digits.

An example of a valid user is
  {:first-name \"John\", :last-name \"Wayne\", :id \"joway142\"}.

Some invalid values are:
 42
 {:first-name \"John\", :last-name \"Wayne\", :id \"cowboystud69\"} ; incorrect identifier
 {:first-name \"John\", :surname \"Wayne\", :id \"joway142\"} ; surname is invalid")

(defn subs' "safe subs" [str lo hi]
  (let [len (count str)]
    (subs str (min lo len) (min hi len))))

(defn valid-id? [first-name last-name id]
  (if (some nil? [first-name last-name id])
    false
    (let [pattern (re-pattern
                   (str/lower-case
                    (str "^"
                         (subs' first-name 0 2)
                         (subs' last-name 0 3)
                         "\\d{3}$")))]
      (re-matches pattern id))))

(s/def ::name (s/and string? (complement empty?)))
(s/def ::first-name ::name)
(s/def ::last-name ::name)
(s/def ::id (s/and string? #(re-matches #"[a-z]{2,5}\d{3}" %)))
(s/def ::user (s/and
               (s/keys :req [::first-name ::last-name ::id])
               #(valid-id? (:first-name %) (:last-name %) (:id %))))

(s/exercise ::user)

; we need a matching generator (difficult to satisfy)

(defn to-id [first-name last-name num]
  (str/lower-case (str (subs' first-name 0 2) (subs' last-name 0 3) num)))

(defn user-generator []
  (gen/fmap
   (fn [[first-name last-name id-num]]
     {:first-name first-name
      :last-name  last-name
      :id         (to-id first-name last-name id-num)})
   (gen/tuple
    (s/gen ::first-name)
    (s/gen ::last-name)
    (gen/large-integer* {:min 100 :max 999}))))


(s/def ::user
  (s/with-gen
   (s/keys :req [::first-name ::last-name ::id])
   user-generator))

(s/exercise ::user)

; to get proper names add generator for ::name

(defn string-alpha []
  (gen/fmap str/join (gen/vector (gen/char-alpha))))

(defn name-generator []
  (gen/fmap str/capitalize (string-alpha)))

(s/def ::name
  (s/with-gen
   (s/and string? (complement empty?) #(re-matches #"[A-Z][a-z]+" %))
   name-generator))

(gen/sample (string-alpha))
(gen/sample (name-generator))
(gen/sample (user-generator))
(gen/sample (s/gen ::name))
(gen/sample (s/gen ::user))

(require '[clojure.test.check :as tc]
         '[clojure.test.check.generators :as gen]
         '[clojure.test.check.properties :as prop #?@(:cljs [:include-macros true])])

(require '[clojure.spec.alpha :as s])

(s/def ::len (s/and number? pos?))
(s/def ::angle (s/and number? (fn [x] (< 0 x 360))))
(s/def ::angles
  (s/and (s/cat :alpha ::angle
                :beta ::angle
                :gamma ::angle)
         (fn [x] (= 180 (reduce + (vals x))))))
(s/def ::longest-side ::len)
(s/def ::triangle (s/keys :req [::angles ::longest-side]))

;; a triangle looks like this:
; {::angles [60 60 60] ;; sum 180, positive values
;  ::longest-side 42}


(s/explain ::triangle
          {::angles [60 120 60]
           ::longest-side 1})

;(s/exercise ::triangle)

(s/def ::menge (s/and number? pos? #(not= ##Inf %)))
(s/def ::einheit keyword?)
(s/def ::ingredient
  (s/with-gen (s/and string? not-empty)
    (fn []
      (gen/elements #{"Mehl" "Zucker" "Eier" "Butter" 
                      "Backpulver" "Wasser" "Milch"
                      "Vanillezucker" "Salz" "Quark"
                      "Öl" "Zitrone" "Banane"}))))
(s/def ::zutat (s/keys :req [::menge ::einheit ::ingredient]))
(s/def ::zutatenliste (s/coll-of ::zutat))
(use 'clojure.pprint)
(pprint (s/exercise ::zutatenliste))

(s/def ::einkaufsliste (s/and ::zutatenliste
                              #(= (count %)
                                  (count (set (map ::ingredient %))))))

(pprint (s/exercise ::einkaufsliste))

(defn scale [zutatenliste factor]
  (map #(update % ::menge * factor) zutatenliste))

(s/fdef scale 
        :args (s/cat :zutatenliste ::zutatenliste
                     :factor ::menge))
(require '[clojure.spec.test.alpha :as stest])

(stest/instrument `scale)
(scale [{::menge 100 ::einheit :foogramm ::ingredient "Mehl"}]
       42)

(s/valid? ::zutatenliste [{::menge 100 ::einheit :foogramm ::ingredient "Mehl"}])

(def zutatenliste (first (nth (s/exercise ::zutatenliste) 8)))
(scale zutatenliste 1000)











; Ein valider Reisepass besteht aus einer Map mit folgenden Einträgen:
; • ::byr (Birth Year) - a number; at least 1920 and at most 2002.
;     byr valid:   2002
;     byr invalid: 2003
(s/def ::byr (s/int-in 1920 2003))
; • ::iyr (Issue Year) - four digits; at least 2010 and at most 2020.
(s/def ::iyr (s/int-in 2010 2021))
; • ::eyr (Expiration Year) - four digits; at least 2020 and at most 2030.
(s/def ::eyr (s/int-in 2020 2031))
; • ::hgt (Height) - a string consisting of a number followed by cm or in:
;     • If cm, the number must be at least 150 and at most 193.
;     • If in, the number must be at least 59 and at most 76.
;     hgt valid:   60in
;     hgt valid:   190cm
;     hgt invalid: 190in
;     hgt invalid: 190
(require '[clojure.string])
(s/def ::hgt
  (s/and string? 
         (s/or :cm (s/and 
                     #(= 5 (count %))
                     #(try (<= 150 (parse-long (subs % 0 3)) 193) 
                           (catch Exception e (comment TODO handle exception)))
                     #(clojure.string/ends-with? % "cm"))
               :in (s/and 
                     #(= 4 (count %))
                     #(try (<= 59 (parse-long (subs % 0 2)) 76)
                           (catch Exception e (comment TODO handle exception)))
                     #(clojure.string/ends-with? % "in")))))
; • ::hcl (Hair Color) - a # followed by exactly six characters 0–9 or a–f.
;     hcl valid:   #123abc
;     hcl invalid: #123abz
;     hcl invalid: 123abc
(s/def ::hcl #(re-matches #"#[0-9a-f]{6}" %)) ;; Regex
(s/valid? ::hcl "#12345a")
; • ::ecl (Eye Color) - exactly one of: :blu :brn :gry :grn :oth.
;     ecl valid:   brn
;     ecl invalid: wat
(s/def ::ecl #{:blu :brn :gry :grn :oth})
(s/exercise ::ecl)
; • ::pid (Passport ID) - a string representing a nine-digit number,
;                         including leading zeroes.
;     pid valid:   000000001
;     pid invalid: 0123456789
(s/def ::pid #(re-matches #"[0-9]{9}" %)) ;; Regex
(s/valid? ::pid "000000010")
; • ::cid (Country ID) - ignored, missing or not.
(s/def ::cid any?)

(s/def ::passport (s/keys :req [::byr ::iyr ::eyr ::hgt
                                ::hcl ::ecl ::pid]
                          :opt [::cid]))

(s/valid? ::passport 
          {::pid "087499704" ::hgt "74in" ::ecl :grn ::iyr 2012 ::eyr 2030 ::byr 1980 ::hcl "#623a2f"})

(s/explain ::passport
           {::iyr 2019
            ::hcl "#602927" ::eyr 1967 ::hgt "170cm"
            ::ecl :grn ::pid "012533040" ::byr 1946})


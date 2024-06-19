(ns reframe.example.core
    (:require
     [reagent.core :as reagent :refer [atom]]
     [reagent.dom :as rd]
     [re-frame.core :as rf]))

;; ClojureScript ist ein Compiler für Clojure der JavaScript produziert. Das Ziel ist Front-End Web Development mit Clojure zu ermöglichen.
;;
;; Die Entwicklung mit ClojureScript läuft etwas anders ab als die Entwicklung mit Clojure. Typischerweise benutzt man heutzutage Hotloading Tools wie Figwheel oder shadow-cljs.
;; Dadurch werden Änderungen in der Datei beim speichern live zum entsprechenden Javascript kompiliert und im Browser direkt angezeigt. Man muss die Anwendung also nicht
;; bei jeder Änderung neu starten, oder die Seite neu laden. Dies stellt einen enormen Vorteil zur Webentwicklung mit anderen Sprachen dar.
;; Dafür muss ein sogenannter watcher gestartet werden, der die jeweiligen Quelldateien beobachten. In diesem Projekt können Sie den watcher mit
;; shadow-cljs -A dev watch app
;; starten.

;; ClojureScript hat mit Javascript eine ähnliche Interoperabilität wie Clojure mit Java. Javascript Funktionen stehen im Namespace js direkt zur Verfügung.
(js/console.log "Hallo Konsole!") ;; Im Browser Rechtsclick -> Inspect -> Console

;; Alerts gehen auch!
(comment
  (js/alert "Hello World!"))

;; Es wäre aber schöner, wenn wir in der Clojure Welt bleiben und einfach println aufrufen könnten. Der folgende Befehl erlaubt das.

(enable-console-print!)

;; Nun werden prints auf der Konsole im Browser angezeigt.

(println "Dieser Text wird auf der Konsole geloggt.")

;; Logging in der Konsole ist zwar gut und schön, aber in der Webseite sollte man auch etwas sehen können. Dies ist zwar möglich mit reinem ClojureScript, wird aber in der Praxis kaum
;; gemacht und ist sehr umständlich. Am Ende schreibt man Javascript mit Clojure Syntax. Das ist nicht unbedingt der Sinn der Sache.
;; Ein funktionaler Ansatz ist definitiv wünschenswert. Das Framework in der JavaScript Welt, das von der Philosophie am ähnlichsten am funktionalen Ansatz von Clojure dran ist, ist React. https://reactjs.org/
;; So ziemlich alle ClojureScript Frameworks bauen daher auf React auf.
;; Hier benutzen wir Reagent, ein weit verbreitetes minimales Interface für React.
;; React basiert auf dem Konzept von wiederverwertbaren "Komponenten".

;; Für Reagent sind Komponenten einfach Funktionen. In React üblicherweise Klassen, obwohl man Sie auch dort als Funktion schreiben kann.

;; Hier ein sehr einfaches Beispiel

(defn Hello1 []
  "Hello World") ; Versuchen Sie mal den Text zu ändern und die Datei zu speichern. Der Text im Browser sollte sich live anpassen!

;; Reagent benutz Hiccup um Html darzustellen. Der Rückgabewert einer Komponente sollte daher eine Hiccup Datenstruktur sein.  Dies sieht ungefähr so aus.

(defn Hello2 []
  [:h1 "Hello World"])

;; Html Attribute werden als Map übergeben.

(defn Hello3 []
  [:div.container ; Klassen können auch mit . Syntax spezifiziert werden
   [:p {:className "myClass" :id "myId" :style {:color "blue"}}
    "Hello " "World!"]]) ; Beliebig viele Elemente im Vektor

;; Wir können auch Parameter an die Komponenten übergeben.

(defn Hello4 [name]
  [:p (str "Hello " name "! ")])

;; Hierbei steht uns die ganze Power von Clojure zur Verfügung. Hiccup ist am Ende nur eine Datenstruktur, die wir beliebig konstruieren können.

(defn Item [item-name]
  [:li item-name])

(defn Items [items]
  [:ul
   (map Item items)])

;; Und was ist mit Zustand? Dafür können wir wie in Clojure üblich Atome benutzen. Hier benutzen wir aber spezielle Atome von Reagent, die zugehörige Komponenten
;; neu rendern wenn ihr Ihnhalt sich ändert.

(defonce app-state (atom {:text "Hello atom! "  ;; Ein reagent Atom (RAtom)
                          :count 0}))

(defn Counter1 []
  (let [{:keys [text count]} @app-state]
    [:div
     text
     "The counter has the value: " count
     [:input {:type "button" :value "+"
              :on-click #(swap! app-state update-in [:count] inc)}]]))


;; In React ist es üblich, dass Komponenten ihren eigenen Zustand lokal verwalten. Dieser Zustand kann dann auch beliebig nach verteilt werden.
;; Hier ein Beispiel aus der Reagent live demo https://reagent-project.github.io/

(defn atom-input [value]
  [:input {:type "text"
           :value @value
           :on-change #(reset! value (-> % .-target .-value))}]) ; .-target und .-value kommen von JavaScript interop

(defn shared-state []
  (let [val (atom "foo")]
    (fn []
      [:div
       [:p "The value is now: " @val]
       [:p "Change it here: " [atom-input val]]])))


;; Komponenten können beliebig genestet werden. Sie werden mit einer speziellen Syntax aufgerufen. Es sieht aus wie eine Liste, ist aber mehr oder weniger ein aufruf der Komponentenfunktion.
;; Parameter können also auch wie bei einem normalen Funktionsaufruf übergeben werden.

(defn MainReagent []
  [:div
   [Hello1]
   [Hello2]
   [Hello3]
   [Hello4 "Bob"]
   [Items]
   [Counter1]
   [shared-state]])

;; Moment... unabhängige Komponenten, die ihren eigenen Zustand verwalten und Daten mit Aktion koppeln? OOP Alarm!
;; Ein etwas funktonalere Programm Architektur wäre hier wünschenswert. Aber mit reinem Reagent müssen wir uns diese von Grund auf überlegen.
;; Kleine Programme sind so relativ einfach zu implementieren, aber dieser Ansatz skaliert eher nicht so gut. Beziehungsweise ist mit viel Arbeit verbunden.
;; Wenn man nicht sehr diszipliniert vorgeht, kann man sich in reinem Reagent leicht in Zustandsverwaltung verrennen.

;; Die Lösung: re-frame.
;; re-frame ist ein Framework, dass auf Reagent aufsattelt aber eine vernünftige Zustandsverwaltung erzwingt.
;; Dafür wird in Kauf genommen, dass kleinere Anwendungen etwas länger werden und einiges an konzeptionellem Overhead produziert wird.
;; re-frame hat eine simple, aber keineswegs einfache Architektur.
;; Aller Zustand wird an den Rand des Programms geschoben. Nämlich in ein globales RAtom namens app-db, das wie eine Datenbank behandelt wird.
;; Die Komponenten fragen die Datenbank dann mit sogenannten Subscriptions die Datenbankanfragen(Queries) ähneln nach den Werten an denen sie gerade interessiert sind.
;; Der Zustand kann dann nur noch über Events verändert werden. Ein Event triggered ein Domino system, dass den Zustand verändert (oder Seiteneffekte verursacht)
;; Dies ist nur ein sehr grober Überblick. Mehr dazu in der re-frame Dokumentation. https://day8.github.io/re-frame/a-loop/ (!!!! lesen !!!!)

;; Hier ein kleines Beispiel

(rf/reg-event-db ; Wir registrieren ein Event das nur die Datenbank verändert und keine anderen Seiteneffekte hat.
 :init-db ; Die Funktion bekommt einen Identifier für das Event
 (fn [_ _] ; Und eine Pure Funktion, die den jetzigen Status von app-db bekommt und den neuen zurückgibt.
   {:count 0})) ; app-db besteht aus geschachtelten Maps.

(rf/reg-event-db
 :inc
 (fn [db _] ; Nun interessiert uns der aktuelle Zustand von app-db. Er wird als erstes Argument übergeben.
   (update-in db [:count] inc))) ; Wir geben den neuen Zustand nach Berechnung zurück.

(defn dec-handler [db _] ;; Die Funktion im zweiten Argument nennt man einen Handler. Sie kann auch separat definiert werden.
  (update-in db [:count] dec))

(rf/reg-event-db
 :dec
 dec-handler)

(rf/reg-sub ; Wir registrieren eine mögliche Query an die zentrale app-db.
 :count ; query-id
 (fn [db _] (:count db))) ; Wir bekommen den aktuellen Zustand von app-db und holen uns die Daten wir brauchen. Dies nennt man auch einen Extraktor.

(rf/reg-sub ; Wenn wir die Daten weiter verarbeiten wollen können wir Subscriptions auch aneinander hängen. Dadurch entsteht eine Art Signal Graph.
 :double-count
 :<- [:count] ; Diese Subscription hängt von :count ab
 (fn [count _] ; Sie bekommt das Ergebnis von :count als Argument
   (* 2 count))) ; und macht dann eine Berechnung

;; Achtung sinnlose Demonstrations Funktion, die für Klarheit kein Destructuring benutzt. Nicht nachmachen!
(rf/reg-sub
 :greeting
 (fn [_ query-vector] ; Der zweite Parameter ist der Vektor mit dem die Query gestartet wurde.
   (let [_ (first query-vector) ; Die Id :name ist das erste Element. Meistens uninteressant.
         name (second query-vector)] ; Das zweite Element ist der Name den wir in der Query angegeben haben.
     (str "Hallo " name "! "))))


(defn Counter2 [] ; Ganz normale Reagent Komponente
  (let [count @(rf/subscribe [:double-count])] ; Den Zählerstand holen wir uns über registrierte Subscription
    [:div
     @(rf/subscribe [:greeting "Bob"])
     "Der Zähler hat den Stand: " count
     [:input {:type "button" :value "+"
              :on-click #(rf/dispatch [:inc])}] ; Dieser Button triggerd das :inc Event
     [:input {:type "button" :value "-"
              :on-click #(rf/dispatch [:dec])}]])) ; Dieser Button das :dec Event

(rf/dispatch [:init-db]) ; Initialisieren der Datenbank. Muss einmal am Anfang der App laufen. Daran können Sie die Zeile auskommentieren um das ständige zurücksetzen des Counters zu verhindern.

;; Jetzt müssen wir nur noch die Komponenten im DOM verankern. Wir ersetzen dafür das Element in der index.hmtl Datei mit der ID app durch unsere Komponente.
;; Dies machen wir mithilfe der render Funktion von Reagent. Diese bekommt einen Vector der Komponenten die wir rendern wollen und ein DOM Element das ersetzt werden soll.

(defn Root []
  [:div
   [MainReagent]
   [Counter2]])

(rd/render [Root]
           (. js/document (getElementById "app")))

;; Für ein umfangreicheres Beispiel siehe https://github.com/day8/re-frame/tree/master/examples/todomvc

;; Abschließende Debugging Tipps für die Aufgaben:
;; - Re-frame Fehlermeldungen findet man oft in der Browser Konsole.
;; - Den Status der re-frame Datenbank kann man in der REPL mit re-frame.db/app-db überprüfen https://day8.github.io/re-frame/FAQs/Inspecting-app-db/
;; - Manchmal wenn man bestehende SUBS oder Events umschreibt um Bugs zu beheben, muss man die Seite neu laden damit die neue Version auch benutzt wird.
;; - Es gibt gute Debugging Tools für re-frame. Zum Beispiel https://github.com/flexsurfer/re-frisk

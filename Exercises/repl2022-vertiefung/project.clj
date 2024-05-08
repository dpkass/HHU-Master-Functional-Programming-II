(defproject repl "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 ;; 15 test.check
                 ;; braucht älteres leiningen (<= 2.8.1)
                 ;[org.clojure/clojure "1.5.1"]
                 [org.clojure/test.check "0.9.0"]
                 ;; aktuelle Version: [org.clojure/test.check "0.9.0"]
                 ; [criterium "0.4.3"]

                 ;; 14 clojure.spec
                 ;[org.clojure/clojure "1.10.0"]
                 [prismatic/schema "1.0.4"]
                 [korma "0.4.3"]
                 [org.clojure/data.csv "0.1.3"]

                 ])

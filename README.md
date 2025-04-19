# Functional Programming II

**Author:** Taha El Amine Kassabi  
**Course:** Functional Programming II (SS 2023)  
**Instructor:** Prof. Dr. Michael Leuschel  
**University:** Heinrich Heine University Düsseldorf (HHU)

---

## 📚 Overview

This repository contains solutions for **Exercises 00–06**, REPL demos, and project-based assignments for the *Functional Programming II* course at HHU.
Topics span from generative testing, specs, and transducers to async channels, Datomic, and Re-frame apps in ClojureScript.

---

## 📂 Repository Structure

```
Exercises/
├── 00-intro-code               → Combinatorics, lazy streams, run-length encoding
├── 01-test-check-material      → Property-based testing with test.check, trampolines
├── 02-spec                     → clojure.spec definitions & generators
├── 03-transducers              → Stateless stream processing & chaining
├── 04-async                    → core.async with go blocks, mult, tap, timeout logic
├── 05-datomic                  → Querying graph databases using Datomic
├── lerneinheit-reframe-toolsdeps/
│   └── Re-frame Tic-Tac-Toe app in ClojureScript with full state/event handling
└── repl2022-vertiefung/       → REPL-based walkthroughs of specs, test.check, ebt

Material/                      → Lecture slides, tasks, solutions for all weeks
README.md                      → Course overview (this file)
```

---

## 🧠 Topics Covered

| Week | Key Concepts                                              |
|------|-----------------------------------------------------------|
| 00   | **Lazy sequences**, oscillators, run-length encode/decode |
| 01   | **Property-based testing**, trampolines, test.check       |
| 02   | clojure.spec, predicates, **functional design**           |
| 03   | **Transducers**, transformations, state-free composition  |
| 04   | core.async, channels, go blocks, do-or-timeout macro      |
| 05   | **Datomic** database queries, **functional persistence**  |
| 06   | Re-frame SPA using subscriptions, events, and components  |

---

## 📁 Setup

```bash
# Install Clojure CLI
brew install clojure/tools/clojure

# Start REPL (deps.edn or project.clj based)
clj
lein repl
```

ClojureScript + Re-frame requires:
```bash
npm install && npx shadow-cljs watch app
```

---

## 🚀 Usage

```bash
cd Exercises/01-test-check-material
lein test     # or run directly in REPL
```

Run a ClojureScript app (Tic Tac Toe):
```bash
cd Exercises/lerneinheit-reframe-toolsdeps/aufgaben
npm install
npx shadow-cljs watch app
# open localhost:3000 in browser
```

---

## 📅 Notes

- Exercises mix project-based tasks and REPL-driven live coding.
- Functional patterns: immutability, stateless transforms, data-centric design.
- Abstract Highlights: custom transducers, async macros, full-stack Clojure(Script).

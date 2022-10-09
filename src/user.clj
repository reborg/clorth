(ns user
  (:require [clorth.word :as w :refer [word]]
            [clorth.stdlib]
            [clorth.math]
            [clojure.main :as main]))

(def env (atom (w/env)))

(def repl-options
  [:prompt #(printf "> ")
   :read (fn [request-prompt request-exit]
           (or ({:line-start request-prompt
                 :stream-end request-exit}
                (main/skip-whitespace *in*))
               (read-line)))
   :eval (fn [s]
           (let [words (read-string (str "[" (w/handle-specials s) "]"))]
             (try
               (swap! env word words)
               (catch Exception e
                 (println (.getMessage e))))
             (:stack @env)))])

(def clorth (delay (apply main/repl repl-options)))
(println "~~~~ To start the interpreter type @clorth to stop use CTRL+D ~~~~")

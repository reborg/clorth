(ns user
  (:require [clorth.word :as w :refer [word]]
            [clorth.reader :as r]
            [clorth.stdlib]
            [clorth.math]
            [clorth.core]
            [clojure.main :as main]))

(def env (atom (w/env)))

(def repl-options
  [:prompt #(printf "clorth> ")
   :read (fn [request-prompt request-exit]
           (or ({:line-start request-prompt
                 :stream-end request-exit}
                (main/skip-whitespace *in*))
               (r/read (read-line))))
   :eval (fn [words]
           (let [io (with-out-str
                     (try
                      (swap! env word words)
                      (catch Exception e
                        (println (.getMessage e)))))]
             (when io (print (str io "\n"))))
           (:stack @env))])

(def clorth (delay (apply main/repl repl-options)))
(println "~~~~ To start the interpreter type @clorth to stop use CTRL+D ~~~~")

(ns clorth.math
  (:require [clorth.word :refer [word pop1 pop2 peek1 peek2 push]]))

(defn arity1 [env op]
  (let [op' (ns-resolve *ns* op)
        n (peek1 env)
        res (op' n)]
    (-> env
        pop1
        (update :stack push res))))

(defn arity2 [env op]
  (let [op' (ns-resolve *ns* op)
        [a b] (peek2 env)
        res (op' b a)]
    (-> env
        pop2
        (update :stack push (cond-> res (boolean? res) (if -1 0))))))

(defmethod word '+ [env [op & args]] (word (arity2 env op) args))
(defmethod word '* [env [op & args]] (word (arity2 env op) args))
(defmethod word '- [env [op & args]] (word (arity2 env op) args))
(defmethod word '/ [env [op & args]] (word (arity2 env op) args))
(defmethod word '= [env [op & args]] (word (arity2 env op) args))
(defmethod word '== [env [op & args]] (word (arity2 env op) args))
(defmethod word '< [env [op & args]] (word (arity2 env op) args))
(defmethod word '> [env [op & args]] (word (arity2 env op) args))
(defmethod word 'inc [env [op & args]] (word (arity1 env op) args))
(defmethod word 'dec [env [op & args]] (word (arity1 env op) args))

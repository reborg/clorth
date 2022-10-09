(ns clorth.math
  (:require [clorth.word :refer [word pop2 peek2 push]]))

(defn arity2 [env op]
  (let [op' (ns-resolve *ns* op)
        [a b] (peek2 env)
        res (op' b a)]
    (-> env
        pop2
        (update :stack push (cond-> res (boolean? res) (if 1 0))))))

(defmethod word '+ [env [op & args]] (word (arity2 env op) args))
(defmethod word '* [env [op & args]] (word (arity2 env op) args))
(defmethod word '- [env [op & args]] (word (arity2 env op) args))
(defmethod word '/ [env [op & args]] (word (arity2 env op) args))
(defmethod word '= [env [op & args]] (word (arity2 env op) args))
(defmethod word '== [env [op & args]] (word (arity2 env op) args))

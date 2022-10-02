(ns clorth.math
  (:require [clorth.word :refer [word pop2 peek2 push]]))

(defn arity2 [env op]
  (let [op' (ns-resolve *ns* op)
        [a b] (peek2 env)]
    (-> env pop2 (update :stack push (op' b a)))))

(defmethod word '+ [env [op & args]] (word (arity2 env op) args))
(defmethod word '* [env [op & args]] (word (arity2 env op) args))
(defmethod word '- [env [op & args]] (word (arity2 env op) args))
(defmethod word '/ [env [op & args]] (word (arity2 env op) args))

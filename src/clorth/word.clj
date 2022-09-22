(ns clorth.word
  "Common defs and functions for word definitions."
  (:require [clojure.string :as string]))

(def push conj)

(def reader-specials
  {":" "\\:"
   ";" "\\;"
   "~" "\\~"
   "." "\\."})

(defn clojurify-string [s]
  (reduce (fn [s [s1 s2]]
            (string/replace s s1 s2))
          s reader-specials))

(defn ensure!
  "Ensures that arg is not nil and `(pred arg)` is not false.
  Return arg."
  ([arg msg] (ensure! identity arg msg))
  ([pred arg msg]
   (when-not (and arg (pred arg))
     (throw (IllegalArgumentException. (format msg arg))))
   arg))

(defn popn [v n]
  (ensure! #(>= (- (count v) %) 0) n (format "[!] cannot pop %s from v size %s." n (count v)))
  (subvec v 0 (- (count v) n)))

(defn env
  "Create a new env"
  ([] (env {} [] {}))
  ([bindings] (env bindings [] {}))
  ([bindings stack] (bindings stack {}))
  ([bindings stack userwords]
   {:bindings bindings
    :stack stack
    :userwords userwords}))

(defmulti word
  (fn [_ args] (cond-> args (coll? args) first)))

(defmethod word :default
  [{userwords :userwords :as env} [arg :as args]]
  (cond
    (find userwords arg) (word (word env (userwords arg)) (rest args))
    (empty? args) env
    :else (word (update env :stack push (first args)) (rest args))))


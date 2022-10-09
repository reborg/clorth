(ns clorth.word
  "Common defs and functions for word definitions."
  (:require [clojure.string :as string])
  (:import [java.lang StackTraceElement]
           [java.util.regex Matcher]))

(def push conj)

(def reader-specials
  {":" "\\:"
   ";" "\\;"
   "~" "\\~"
   ".\" " "\\. \"" ; dot-doublequote
   #"^\.$" (Matcher/quoteReplacement "\\.")
   " . " " \\. "
   })

(defn handle-specials [s]
  (reduce (fn [s [s1 s2]]
            (println (format " @@@ s before replace is '%s', got s1 '%s' and s2 '%s' after replace: '%s'" s s1 s2 (string/replace s s1 s2)))
            (string/replace s s1 s2))
          s reader-specials))

(defn ezthrow
  "Throw a Runtime Exception with msg where the trace contains
  items matching regex and only call sites line numbers
  (not function definition line numbers removing useless repetitions)."
  [msg regex]
  (throw
   (proxy [RuntimeException] [^String msg]
     (getStackTrace ^"[Ljava.lang.StackTraceElement;" []
       (let [trace (proxy-super getStackTrace)
             jclassName (memfn getClassName)
             jfileName (memfn getFileName)
             jlineNumber (memfn getLineNumber)]
         (->> trace
              (filter #(re-find regex (jclassName %)))
              (map (juxt jclassName jfileName jlineNumber))
              (partition 2 1)
              (map (fn [[[c1 f1 l1 :as itm1] [c2 f2 l2 :as itm2]]]
                        (if (and (= c1 c2) (= f1 f2)) itm1 itm2)))
              distinct
              (map (fn [[nspace fname lnumber]]
                     (StackTraceElement. nspace "invoke" fname lnumber)))
              into-array))))))

(defn ensure!
  "Ensures that arg is not nil and `(pred arg)` is not false.
  Return arg."
  ([pred] (pred))
  ([arg msg] (ensure! identity arg msg))
  ([pred arg msg]
   (when-not (and arg (pred arg))
     (ezthrow (format (str "(!) " msg) arg) #"^clorth"))
   arg))

(defn popn
  "Pops n items from v after checking there are enought items to pop."
  [v n]
  (let [msg (format "stack underflow: attempt at pop %s but only %s available." n (count v))]
    (ensure! #(>= (- (count v) %) 0) n msg)
    (subvec v 0 (- (count v) n))))

(defn pop1 [env] (update env :stack popn 1))
(defn pop2 [env] (update env :stack popn 2))
(defn pop3 [env] (update env :stack popn 3))

(defn peekn
  "Peek n items from v without popping them. Throws
  exception if there are not enough elements to peek."
  [v n]
  (let [msg (format "stack underflow: attempt at pop %s but only %s available." n (count v))]
    (ensure! #(>= (- (count v) %) 0) n msg))
  (take n (rseq v)))

(defn peek1 [env] (first (peekn (:stack env) 1)))
(defn peek2 [env] (peekn (:stack env) 2))
(defn peek3 [env] (peekn (:stack env) 3))

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
  (fn [_ args]
    (cond-> args (coll? args) first)))

(defmethod word :default
  [{userwords :userwords :as env} [arg :as args]]
  (cond
    (find userwords arg) (word (word env (userwords arg)) (rest args))
    (empty? args) env
    :else (word (update env :stack push (first args)) (rest args))))


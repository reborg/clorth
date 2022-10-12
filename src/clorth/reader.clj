(ns clorth.reader
  "Clorth reader"
  (:require [clojure.string :as string])
  (:refer-clojure :exclude [read])
  (:import [java.util.regex Matcher]))

(def reader-specials
  {":" "\\:"
   ";" "\\;"
   "~" "\\~"
   ".\" " "\\. \"" ; dot-doublequote
   #"^\.$" (Matcher/quoteReplacement "\\.")
   " . " " \\. "
   " ." " \\."
   #"1\+" "inc"
   #"1\-" "dec"
   })

(defn handle-specials [s]
  (reduce (fn [s [s1 s2]] (string/replace s s1 s2)) s reader-specials))

(defn read
  "Takes a string of tokens and translates them
  into a vector of tokens."
  [s]
  (read-string
   (str "[" (handle-specials s) "]")))

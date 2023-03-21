@@@@@@@@@ Forth DSL

: star 42 emit;
: stars 0 do star loop;
: margin cr 30 spaces;
: dot margin star;
: line margin 5 stars;
line dot line dot dot cr
: F line dot line dot dot cr ;













@@@@@@@@@ Concatenative VS Functional

(defn plus [a b] (+ a b))
(defn times [a b] (* a b))
(defn plus-times [a b c] (times a (plus b c)))

;; Note:
;; it's called plus-times in order of evaluation
;; but it's (lexically) *times-then-plus*


(plus-times 1 2 3)
;; 5

;; It actually reads as it evaluates!

: plus + ;
: times * ;
: plus-times + * ;
1 2 3 plus-times





















@@@@@@@@@@@@@@ REPL Calculator

(ns calculator)

(defn plus [x y] (+ x y))
(defn minus [x y] (- x y))
(defn times [x y] (* x y))
(defn divide [x y] (/ x y))

(require '[clojure.main :as main])
(main/repl :init #(require '[calculator :refer :all]))

(plus 1 2)

(def repl-options
  [:init   #(require '[calculator :refer :all])
   :prompt #(printf "enter expression :> ")])

(apply main/repl repl-options)

(def repl-options
  [:prompt #(printf "enter expression :> ")
   :read   (fn [request-prompt request-exit]
             (or ({:line-start request-prompt :stream-end request-exit}
                  (main/skip-whitespace *in*))
                  (re-find #"^(\d+)([\+\-\*\/])(\d+)$" (read-line))))
   :eval   (fn [[_ x op y]] ; (4)
             (({"+" + "-" - "*" * "/" /} op)
              (Integer. x) (Integer. y)))])

(apply main/repl repl-options)

(ns clorth.stdlib
  (:require [clorth.word :refer [pop1 peek1 peek2 push word pop2 popn ensure!]]
            [clorth.math]))

(defmethod word 'drop
  [env args]
  (word (pop1 env) (rest args)))

(defmethod word 'dup
  [env args]
  (let [itm (peek1 env)]
    (word
     (update env :stack push itm)
     (rest args))))

(defmethod word 'swap
  [env args]
  (let [[itm1 itm2] (peek2 env)]
    (word
     (-> env
         pop2
         (update :stack push itm1 itm2))
     (rest args))))

(defmethod word \:
  [env [_ defword & args]]
  (let [[body [_ & cont]] (split-with (complement #{\;}) args)]
    (word (update env :userwords assoc defword body) cont)))

(defmethod word \.
  [env [_ & args]]
  (if (string? (first args))
    (do (print (first args)) (word env (rest args)))
    (do (print (peek (:stack env))) (word env args)))
  )

(defmethod word 'spaces
  [{:keys [stack] :as env} [_ & args]]
  (let [n (peek stack)
        env* (update env :stack pop)]
    (ensure! number? n (format "[!] not a number '%s'" n))
    (dotimes [_ n] (print \space))
    (word env* args)))

(defmethod word 'cr
  [env [_ & args]]
  (println)
  (word env args))

(defmethod word 'emit
  [{:keys [stack] :as env} [_ & args]]
  (let [n (peek stack)
        env* (update env :stack pop)]
    (ensure! int? n (format "[!] not a integer '%s'" n))
    (print (char n))
    (word env* args)))

(defmethod word 'do
  [{:keys [stack] :as env} [_ & args]]
  (let [from (peek (pop stack))
        to (peek stack)
        _ (ensure! int? to (format "[!] int required: cannot loop until '%s'" to))
        _ (ensure! int? from (format "[!] int required: cannot loop from '%s'" from))
        [body [_ & cont]] (split-with (complement #{'loop}) args)
        init-env (update env :stack popn 2)
        env* (reduce (fn [env _] (word env body)) init-env (range (- from to)))]
    (word env* cont)))

(defn interop [env args fname n]
  (let [f (ensure! (ns-resolve 'clorth.core fname)
                   (format "[!] Unable to resolve interop call %s" fname))
        arg (ensure! (take n (rseq (:stack env)))
                     (format "[!] Interop call requires '%s' arg but '%s' found." n (count (:stack env))))
        env* (update env :stack popn n)]
    (word (update env* :stack push (apply f arg)) args)))

(defmethod word '_ [env [_ fname & args]] (interop env args fname 1))
(defmethod word '_2 [env [_ fname & args]] (interop env args fname 2))
(defmethod word '_3 [env [_ fname & args]] (interop env args fname 3))
(defmethod word '_4 [env [_ fname & args]] (interop env args fname 4))
(defmethod word '_5 [env [_ fname & args]] (interop env args fname 5))

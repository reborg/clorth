(ns clorth.stdblib-test
  (:require [clojure.test :refer [deftest is testing]]
            [clorth.word :refer [env word]]
            [clorth.stdlib]))

(defn e [expr]
  (->> expr (word (env)) :stack))

(deftest push-test
  (is (= [1] (:stack (word (env) [1])))))

(deftest nothing-test
  (is (= [] (e []))))

(deftest stack-ops-test
  (is (= '[1 a b] (e '[1 2 3 drop drop a b])))
  (is (= [] (e '[1 2 drop drop])))
  (is (= [1 1] (e '[1 dup])))
  (is (= [2] (e '[5 10 swap /])))
  (is (thrown? RuntimeException (e '[1 swap])))
  (is (= [1 2 3 2] (e '[1 2 3 over])))
  (is (= [2 3 1] (e '[1 2 3 rot])))
  )

(deftest creating-new-words-test
  (is (= '[1 a b] (e '[\: triple drop drop drop \; 1 2 3 4 triple a b]))))

(deftest other-stdlib-test
  (is (= "     " (with-out-str (e '[5 spaces]))))
  (is (thrown? RuntimeException (e '[h spaces])))
  (is (= "**" (with-out-str (e '[42 emit 42 emit]))))
  (is (= "***" (with-out-str (e '[\: star 42 emit \; star star star]))))
  (is (= "hello" (with-out-str (e '[\. "hello"]))))
  (is (= "hi" (with-out-str (e '[hi \. 1]))))
  (is (= "123" (with-out-str (e '[1 \. 2 \. 3 \.]))))
  (is (= [] (let [state (atom nil)]
                   (with-out-str (reset! state (e '[1 \. 2 \. 3 \.])))
                   @state)))
  (is (= "hello" (with-out-str (e '[\: greet \. "hello" \; greet]))))
  )

(deftest interop-test
  (is (= [2] (e '[1 _ inc])))
  (is (= [7] (e '[2 5 _2 +])))
  (is (= [24] (e '[2 5 7 _2 + _2 *]))))

(deftest looping-test
  (is (= "*****" (with-out-str (e '[\: star 42 emit \; 5 0 do star loop])))))

(deftest native-math-test
  (is (= [14] (e '[2 5 + 2 *])))
  (is (= [2] (e '[10 5 /])))
  (is (= [2] (e '[10 5 /])))
  (is (= [-1] (e '[2 3 -]))))

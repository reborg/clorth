(ns clorth.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [clorth.word :refer [env word]]))

(defn e [expr]
  (->> expr (word (env)) :stack))

(deftest clorth-test
  (testing "push"
    (is (= [1] (:stack (word (env) [1])))))
  (testing "nothing"
    (is (= [] (e []))))
  (testing "drop"
    (is (= '[1 a b] (e '[1 2 3 drop drop a b])))
    (is (= [1 2] (e '[drop drop 1 2]))))
  (testing "new words"
    (is (= '[1 a b] (e '[\: triple drop drop drop \; 1 2 3 4 triple a b]))))
  (testing "some standard words"
    (is (= "     " (with-out-str (e '[5 spaces]))))
    (is (thrown? IllegalArgumentException (e '[h spaces])))
    (is (= "**" (with-out-str (e '[42 emit 42 emit]))))
    (is (= "***" (with-out-str (e '[\: star 42 emit \; star star star]))))
    (is (= "hello" (with-out-str (e '[\. "hello"]))))
    (is (= "hi" (with-out-str (e '[hi \. 1]))))
    (is (= "hello" (with-out-str (e '[\: greet \. "hello" \; greet]))))
    )
  (testing "interop"
    (is (= [2] (e '[1 _ inc])))
    (is (= [7] (e '[2 5 _2 +])))
    (is (= [24] (e '[2 5 7 _2 + _2 *])))
    )
  (testing "looping"
    (is (= "*****" (with-out-str (e '[\: star 42 emit \; 5 0 do star loop]))))))


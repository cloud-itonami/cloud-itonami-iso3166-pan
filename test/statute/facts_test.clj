(ns statute.facts-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is]]
            [statute.facts :as facts]))

(deftest pan-has-spec-basis
  (let [sb (facts/spec-basis "PAN")]
    (is (= 2 (count sb)))
    (is (every? #(str/starts-with? (:statute/url %) "https://s3-legispan.asamblea.gob.pa/") sb))
    (is (every? :statute/law-number sb))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["PAN" "JPN" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 1 (:covered c)))
    (is (= ["ATL" "JPN"] (:missing-jurisdictions c)))))

(deftest by-topic-filters
  (is (= ["pan.ley-81-2019-proteccion-datos"]
         (mapv :statute/id (facts/by-topic "PAN" :data-protection))))
  (is (empty? (facts/by-topic "PAN" :labor)))
  (is (empty? (facts/by-topic "ATL" :data-protection))))

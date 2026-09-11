(ns marketentry.facts-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.facts :as facts]))

(deftest pan-has-spec-basis
  (let [sb (facts/spec-basis "PAN")]
    (is (some? sb))
    (is (string? (:provenance sb)))
    (is (seq (:required-evidence sb)))
    (is (= 4 (count (:required-evidence sb))) "exactly 4 required-evidence items, not padded")
    (is (some? (facts/rep-spec-basis "PAN")))
    (is (some? (facts/corporate-number-spec-basis "PAN")))
    (is (some? (facts/retail-trade-spec-basis "PAN")))
    (is (some? (facts/dispute-forum-spec-basis "PAN")))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest required-evidence-satisfied
  (let [sb (facts/spec-basis "PAN")
        all (:required-evidence sb)]
    (is (true? (facts/required-evidence-satisfied? "PAN" all)))
    (is (not (facts/required-evidence-satisfied? "PAN" (take 1 all))))
    (is (nil? (facts/required-evidence-satisfied? "ATL" all)))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["PAN" "USA" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 1 (:covered c)))
    (is (= ["ATL" "USA"] (:missing-jurisdictions c)))))

(deftest no-fabricated-dgcp-domain
  (testing "dgcp.gob.pa is unreachable this session -- must never be cited as provenance"
    (let [sb (facts/spec-basis "PAN")]
      (is (not (re-find #"dgcp\.gob\.pa" (:provenance sb)))
          "must cite panamacompra.gob.pa / compendio.panamacompra.gob.pa instead")
      (is (re-find #"panamacompra\.gob\.pa" (:provenance sb))))))

(deftest dispute-forum-is-distinct-from-dgcp
  (testing "TACP adjudicates disputes -- a body DISTINCT from DGCP, which runs PanamaCompra"
    (let [df (facts/dispute-forum-spec-basis "PAN")]
      (is (re-find #"TACP|Tribunal Administrativo" (:dispute-forum-authority df)))
      (is (re-find #"tacp\.gob\.pa" (:dispute-forum-provenance df)))
      (is (re-find #"DISTINTO" (:dispute-forum-note df))))))

(deftest retail-trade-restriction-is-sector-conditional-by-basis
  (testing "Art. 288 spec-basis cites the Constitución, not a blanket foreign-investment rule"
    (let [rt (facts/retail-trade-spec-basis "PAN")]
      (is (re-find #"Constituci" (:retail-trade-owner-authority rt)))
      (is (re-find #"288" (:retail-trade-legal-basis rt)))
      (is (re-find #"comercio al por menor" (:retail-trade-legal-basis rt))))))

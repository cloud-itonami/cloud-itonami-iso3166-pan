(ns marketentry.governor
  "Market-Entry Compliance Governor -- the independent compliance layer
  that earns the MarketEntry-LLM the right to commit. The LLM has no
  notion of Panamanian procurement law, whether a licensed
  Panamanian resident agent is actually on file for a Sociedad
  Anónima, whether a claimed engagement fee actually equals base +
  months x rate, whether the required registration evidence has
  actually been assessed for a filing, whether Artículo 288 of the
  Constitución Política bears on a retail-trade engagement with
  non-Panamanian ownership, or when a draft stops being a draft and
  becomes a real-world PanamaCompra portal submission, so this MUST
  be a separate system able to *reject* a proposal and fall back to
  HOLD.

  `:itonami.blueprint/governor` is `:market-entry-compliance-governor`
  (shared family keyword on blueprints; this is the Panama running
  implementation of that governor for the iso3166 family, following
  the AGO template's structure).

  This blueprint's own text (README Core Contract: 'No automated
  proposal can submit a portal registration or filing the governor
  refuses, suppress a compliance record, or claim a legal/tax
  conclusion the governor has not cleared') names exactly the checks
  below.

  Checks, in priority order, ALL HARD violations: a human approver
  CANNOT override them. The confidence/actuation gate is SOFT: it
  asks a human to look (low confidence / actuation), and the human
  may approve -- but see `marketentry.phase`: for `:stake
  :actuation/draft-filing`/`:actuation/submit-filing` NO phase ever
  allows auto-commit either. Two independent layers agree that
  actuation is always a human call.

    1. Spec-basis                  -- did the jurisdiction proposal cite
                                       an OFFICIAL source
                                       (`marketentry.facts`), or invent
                                       one?
    2. Evidence incomplete         -- for `:filing/draft`/
                                       `:filing/submit`, has the
                                       jurisdiction actually been
                                       assessed with a full evidence
                                       checklist on file? For Panama
                                       this covers: Registro Público
                                       incorporation + resident agent,
                                       DGI RUC tax registration, MICI
                                       Aviso de Operación (Panama
                                       Emprende), and DGCP/PanamaCompra
                                       procurement-participation
                                       registration -- four
                                       independently-sourced
                                       required-evidence items, none
                                       fabricated.
    3. Retail-trade restriction    -- for `:filing/submit`,
                                       INDEPENDENTLY verify the
                                       Artículo 288 constitutional
                                       restriction: when the
                                       engagement's own
                                       `:sector` is `\"retail-trade\"`
                                       AND `:non-panamanian-ownership?`
                                       is true, this is a HARD
                                       violation. SECTOR-CONDITIONAL --
                                       it must NEVER fire for any
                                       other sector, even with
                                       non-Panamanian ownership; this
                                       is not a blanket foreign-
                                       investment ban. FLAGSHIP
                                       genuinely new check for the
                                       iso3166 family (grep-verified
                                       absent as a governor check
                                       function name fleet-wide at
                                       build time). Grounded in
                                       Constitución Política de la
                                       República de Panamá, Art. 288.
    4. Engagement fee mismatch     -- for `:filing/submit`,
                                       INDEPENDENTLY recompute whether
                                       the engagement's own `:claimed-
                                       fee` equals `base-fee +
                                       monthly-rate x monitoring-
                                       months` -- honest reapplication
                                       of the ground-truth-recompute
                                       discipline sibling actors use.
    5. Confidence floor / actuation
       gate                          -- LLM confidence below threshold,
                                       OR the op is `:filing/draft`/
                                       `:filing/submit` (REAL acts)
                                       -> escalate.

  Two more guards, double-draft/double-submit prevention, are enforced
  off dedicated `:drafted?`/`:submitted?` facts (never a `:status`
  value)."
  (:require [marketentry.facts :as facts]
            [marketentry.registry :as registry]
            [marketentry.store :as store]))

(def confidence-floor 0.6)

(def high-stakes
  "Stakes grave enough to always require a human, even when clean.
  Drafting a real portal package and submitting a real PanamaCompra
  portal registration are the two real-world actuation events this
  actor performs."
  #{:actuation/draft-filing :actuation/submit-filing})

;; ----------------------------- checks -----------------------------

(defn- spec-basis-violations
  "A `:jurisdiction/assess` (or `:filing/draft`/`:filing/submit`)
  proposal with no spec-basis citation is a HARD violation -- never
  invent a jurisdiction's market-entry requirements."
  [{:keys [op]} proposal]
  (when (contains? #{:jurisdiction/assess :filing/draft :filing/submit} op)
    (let [value (:value proposal)]
      (when (or (empty? (:cites proposal))
                (and (contains? value :spec-basis) (nil? (:spec-basis value))))
        [{:rule :no-spec-basis
          :detail "公式spec-basisの引用が無い提案は法域要件として扱えない"}]))))

(defn- evidence-incomplete-violations
  "For `:filing/draft`/`:filing/submit`, the jurisdiction's required
  registration evidence (Registro Público + resident agent, DGI RUC,
  MICI Aviso de Operación, DGCP/PanamaCompra registration) must
  actually be satisfied."
  [{:keys [op subject]} st]
  (when (contains? #{:filing/draft :filing/submit} op)
    (let [e (store/engagement st subject)
          assessment (store/assessment-of st subject)]
      (when-not (and assessment
                     (facts/required-evidence-satisfied?
                      (:jurisdiction e) (:checklist assessment)))
        [{:rule :evidence-incomplete
          :detail "法域の必要書類(Registro Público/代理人/RUC/Aviso de Operación/PanamaCompra登録等)が充足していない状態での提案"}]))))

(defn- retail-trade-restriction-violations
  "For `:filing/submit`, when the engagement declares `:sector`
  `\"retail-trade\"` AND `:non-panamanian-ownership? true`,
  INDEPENDENTLY verify this against Artículo 288 of the Constitución
  Política -- the flagship genuinely new check this vertical adds.
  SECTOR-CONDITIONAL: fires ONLY for retail-trade engagements with
  non-Panamanian ownership, never for any other sector (not a
  blanket foreign-ownership ban)."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (and (= (:sector e) "retail-trade")
                 (true? (:non-panamanian-ownership? e)))
        [{:rule :retail-trade-restriction
          :detail (str subject " はArt.288(comercio al por menor)対象のretail-trade案件で非パナマ資本 -- 提出提案は進められない")}]))))

(defn- engagement-fee-mismatch-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the
  engagement's own claimed fee equals base + months x rate."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when-not (registry/engagement-fee-matches-claim? e)
        [{:rule :engagement-fee-mismatch
          :detail (str subject " の申告手数料(" (:claimed-fee e)
                      ")が独立再計算値(" (registry/compute-engagement-fee e) ")と一致しない")}]))))

(defn- already-drafted-violations
  "For `:filing/draft`, refuses to draft the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/draft)
    (when (store/engagement-already-drafted? st subject)
      [{:rule :already-drafted
        :detail (str subject " は既にドラフト済み")}])))

(defn- already-submitted-violations
  "For `:filing/submit`, refuses to submit the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (when (store/engagement-already-submitted? st subject)
      [{:rule :already-submitted
        :detail (str subject " は既に提出済み")}])))

(defn check
  "Censors a MarketEntry-LLM proposal against the governor rules.
  Returns {:ok? bool :violations [..] :confidence c :escalate? bool
  :high-stakes? bool :hard? bool}."
  [request _context proposal st]
  (let [hard (into []
                   (concat (spec-basis-violations request proposal)
                           (evidence-incomplete-violations request st)
                           (retail-trade-restriction-violations request st)
                           (engagement-fee-mismatch-violations request st)
                           (already-drafted-violations request st)
                           (already-submitted-violations request st)))
        conf (:confidence proposal 0.0)
        low? (< conf confidence-floor)
        stakes? (boolean (high-stakes (:stake proposal)))
        hard? (boolean (seq hard))]
    {:ok?          (and (not hard?) (not low?) (not stakes?))
     :violations   hard
     :confidence   conf
     :hard?        hard?
     :escalate?    (and (not hard?) (or low? stakes?))
     :high-stakes? stakes?}))

(defn hold-fact
  "The audit fact written when a proposal is rejected (HOLD)."
  [request context verdict]
  {:t          :governor-hold
   :op         (:op request)
   :actor      (:actor-id context)
   :subject    (:subject request)
   :disposition :hold
   :basis      (mapv :rule (:violations verdict))
   :violations (:violations verdict)
   :confidence (:confidence verdict)})

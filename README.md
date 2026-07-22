# cloud-itonami-iso3166-pan

Open ISO 3166 Blueprint for **PAN**: Panama -- **`:implemented`**.

This repository designs **and implements** a forkable OSS business for
an independent public-sector market-entry consultant: an already-
incorporated operator (e.g. a `cloud-itonami-cofog-{code}`,
`cloud-itonami-isco-{code}`, `cloud-itonami-unspsc-{segment}` or
`cloud-itonami-{ISIC}` blueprint fork) gets a **MarketEntry-LLM**
Compliance Advisor + independent **Market-Entry Compliance Governor**
to navigate public-procurement registration, local business/tax
registration, and local-content rules in Panama, so the operator
can win and service a government contract without hiring a full in-house
compliance department.

## Checks

Five checks, in priority order, evaluated by `marketentry.governor` on
every `MarketEntry-LLM` proposal. The first four are HARD violations a
human approver cannot override; double-actuation guards are counted
separately. The confidence/actuation gate (item 5) is SOFT -- but see
Actuation below, `:filing/draft`/`:filing/submit` never auto-commit
regardless.

| # | Check | Grounds | Source |
|---|---|---|---|
| 1 | **Spec-basis** -- a `:jurisdiction/assess`/`:filing/draft`/`:filing/submit` proposal must cite an official source, never an invented one | `marketentry.facts/spec-basis` | panamacompra.gob.pa, compendio.panamacompra.gob.pa, registro-publico.gob.pa, dgi.mef.gob.pa |
| 2 | **Evidence incomplete** -- for draft/submit, the jurisdiction's full required-evidence checklist must be on file: (a) Registro Público de Panamá incorporation record (pacto social; mandatory licensed resident agent for a Sociedad Anónima), (b) DGI RUC tax-registration record, (c) MICI Aviso de Operación (Panama Emprende) commercial-license record, (d) DGCP/PanamaCompra procurement-participation registration record | `marketentry.facts/required-evidence-satisfied?` | Ley N.º 32 de 1927 (S.A.) / Ley N.º 4 de 2009 (SRL); Registro Público de Panamá; DGI/MEF; MICI/Panama Emprende; Texto Único de la Ley 22 de 2006 (per Ley 153 de 2020) |
| 3 | **Retail-trade restriction** (flagship, sector-conditional) -- for submit, INDEPENDENTLY verify Artículo 288 of the Constitución Política: fires ONLY when the engagement's own `:sector` is `"retail-trade"` AND `:non-panamanian-ownership? true`; NEVER for any other sector -- not a blanket foreign-investment ban | `marketentry.governor/retail-trade-restriction-violations` | Constitución Política de la República de Panamá, Art. 288 (comercio al por menor reservado a panameños, con excepciones históricas limitadas) |
| 4 | **Engagement fee mismatch** -- for submit, independently recompute `claimed-fee = base-fee + monthly-rate x monitoring-months` | `marketentry.registry/engagement-fee-matches-claim?` | ground-truth recompute (fleet-standard discipline) |
| 5 | **Confidence floor / actuation gate** (SOFT) -- LLM confidence below 0.6, or the op is `:filing/draft`/`:filing/submit` -> escalate to human | `marketentry.governor/check` | this vertical's own Trust Controls (`docs/business-model.md`) |

Two further double-actuation guards (`already-drafted`,
`already-submitted`) refuse to draft or submit the SAME engagement
twice, enforced off dedicated `:drafted?`/`:submitted?` booleans, never
a `:status` value.

Check 3 is deliberately **sector-conditional, not a blanket rule**:
`test/marketentry/governor_contract_test.clj`'s
`retail-trade-restriction-does-not-fire-for-other-sectors` proves a
non-Panamanian-owned engagement in a NON-retail sector proceeds through
the ordinary escalate-then-approve path with no HARD hold, while
`retail-trade-restriction-is-held-and-unoverridable` proves the SAME
ownership fact in a `"retail-trade"` engagement is an un-overridable
HARD hold. Two independent, contrasting fixtures (`eng-4`, `eng-5`) in
`marketentry.store/demo-data` exercise both branches.

**Panama's public-procurement dispute forum is a body DISTINCT from
DGCP**: the Tribunal Administrativo de Contrataciones Públicas (TACP,
https://tacp.gob.pa/) independently adjudicates bid protests and
procurement disputes; DGCP is the executive regulator that runs
PanamaCompra and issues rules. `marketentry.facts/dispute-forum-spec-basis`
exposes this distinction so no proposal conflates the two; see
`test/marketentry/facts_test.clj`'s `dispute-forum-is-distinct-from-dgcp`.

## Actuation

**Drafting a real PanamaCompra filing / portal registration and
submitting a real filing are never autonomous, at any phase, by
construction.** Two independent layers enforce this:

- `marketentry.governor`'s `high-stakes` set
  (`#{:actuation/draft-filing :actuation/submit-filing}`) always
  escalates, regardless of confidence.
- `marketentry.phase`'s phase table (`phase 0` through `phase 3`)
  never puts `:filing/draft` or `:filing/submit` in any phase's
  `:auto` set -- see `marketentry.phase`'s own docstring and
  `test/marketentry/phase_test.clj`'s `filing-submit-never-auto`, plus
  `test/marketentry/governor_contract_test.clj`'s
  `filing-draft-and-submit-never-auto-commit`.

The actor may intake an engagement, assess a jurisdiction and draft a
recommendation; a human market-entry operator is always the one who
actually files a draft or a submission. Grounded directly in this
blueprint's own [`docs/business-model.md`](docs/business-model.md) and
`marketentry.governor`'s own namespace docstring, which names this
vertical's Trust Controls verbatim: "any actual portal registration or
filing submission requires Market-Entry Compliance Governor clearance
and always escalates to human sign-off"; "a false or fabricated
regulatory-requirement claim is a HARD hold". `:filing/draft` and
`:filing/submit` apply SEQUENTIALLY to the SAME engagement record
(draft first, submit later) -- matching every sibling
`market-entry-compliance-governor` actor's own sequential shape.

## No robotics premise — digital/data service exemption

Market-entry and procurement-compliance navigation is a pure data/software
service with no physical-domain work (portal registration, document
checklists, regulatory-change monitoring) — the same exemption class as
`cloud-itonami-6310` (HR SaaS replacement) and `cloud-itonami-gtin-*`.
`blueprint.edn` sets `:itonami.blueprint/robotics false` and
`:required-technologies` lists only real capabilities (`:identity`,
`:forms`, `:dmn`, `:bpmn`, `:audit-ledger`), no `:robotics`.

## Core Contract

```text
operator intake + prior filing history
        |
        v
Compliance Advisor -> Market-Entry Compliance Governor -> filing draft, or human sign-off
        |
        v
gated portal registration / filing submission + audit ledger
```

No automated proposal can submit a portal registration or filing the
governor refuses, suppress a compliance record, or claim a legal/tax
conclusion the governor has not cleared. `:filing/submit` is never in any
phase's `:auto` set — it always requires human sign-off (mirrors
`cloud-itonami-M6910`'s `filing-submit-never-auto-at-any-phase`
invariant).

## What this is NOT

- **Not the government of Panama.** See
  [`docs/business-model.md`](docs/business-model.md) for the boundary with
  `com-etzhayyim-ooyake` (read-only civic mirror), `matsurigoto` (sovereign
  statecraft), `com-etzhayyim-toritsugi` (individual citizen concierge),
  `legal-entity.etzhayyim.com` (read-only data aggregation), and
  `cloud-itonami-M6910` (company incorporation — a different regulatory
  phase this blueprint assumes is already complete).
- **Not legal or tax advice.** Every regulatory claim must cite the
  official source and route final filings to Panamanian-licensed counsel
  or a registered agent where the law requires licensed representation.

## Capability layer

Resolves via [`kotoba-lang/iso3166`](https://github.com/kotoba-lang/iso3166)
(ISO 3166 `PAN`). Required capabilities:

- :identity
- :forms
- :dmn
- :bpmn
- :audit-ledger

See [`docs/business-model.md`](docs/business-model.md) and
[`docs/operator-guide.md`](docs/operator-guide.md).

## Run

```bash
clojure -M:dev:run     # walk a clean intake -> assess -> draft -> submit lifecycle, plus HARD-hold scenarios (retail-trade included)
clojure -M:dev:test    # governor contract · phase invariants · store parity · registry conformance · facts coverage
clojure -M:lint        # clj-kondo (errors fail; CI mirrors this)
```

## License

AGPL-3.0-or-later.

## Market-entry / statute catalogs

Governed public-sector market-entry compliance actor, same architecture
as the other `cloud-itonami-iso3166-*` siblings:

- `src/marketentry/{facts,governor,phase,sim,operation,registry,store,
  marketentryllm}.cljc` -- the actor. `facts.cljc` cites PanamaCompra
  (panamacompra.gob.pa / compendio.panamacompra.gob.pa -- the DGCP's
  own dgcp.gob.pa domain was unreachable this session and is
  deliberately NOT cited), the Texto Único de la Ley N.º 22 de 2006
  as reordenado por la Ley N.º 153 de 2020 (not the bare 2006 text),
  the Registro Público de Panamá (registro-publico.gob.pa) for
  incorporation + the mandatory resident-agent requirement for a
  Sociedad Anónima (Ley 32 de 1927), DGI (dgi.mef.gob.pa) for RUC tax
  registration, MICI's Panama Emprende platform for the Aviso de
  Operación commercial license (a THIRD, distinct required-evidence
  item -- its own domain was unreachable this session, so it is cited
  by institutional attribution only, never an unverified URL), and the
  Constitución Política's Artículo 288 for the flagship sector-
  conditional retail-trade restriction. `governor.cljc`'s flagship
  check independently verifies the Art. 288 restriction ONLY for
  `"retail-trade"`-sector engagements with `:non-panamanian-ownership?
  true` -- a check SHAPE genuinely different from siblings whose
  flagship check is an unconditional resident-representative/tax-ID
  requirement: this one is a CONSTITUTIONAL, sector-scoped restriction,
  never a blanket foreign-ownership ban (see the namespace docstrings
  and `test/marketentry/governor_contract_test.clj`'s two contrasting
  fixtures for the full honest disclosure).
- `src/statute/facts.cljc` -- general-law catalog (pre-existing, not
  modified by this Wave): Ley N.º 2 de 1916 (Código Civil / Código de
  Comercio) and Ley N.º 81 de 2019 (Protección de Datos Personales),
  both cited via LEGISPAN (s3-legispan.asamblea.gob.pa).

Every citation is WebFetch-verified against an official source this
session (panamacompra.gob.pa, compendio.panamacompra.gob.pa,
registro-publico.gob.pa, dgi.mef.gob.pa, tacp.gob.pa) or traced to the
verified-facts brief this Wave was built from; sources that were
unreachable this session (dgcp.gob.pa, panamaemprende.gob.pa) are
named explicitly rather than guessed at -- see `marketentry.facts`'s
own docstring for the full honest disclosure of which citation is a
live-verified URL vs. an institutional-name-only attribution.

## Culture catalog

Alongside the market-entry / statute catalogs, this repo carries a
**country-level regional-culture catalog** (ADR-2607171400 addendum 2,
`cloud-itonami-municipality-culture-catalog` Wave 1, in
`com-junkawasaki/root`) — national dishes, protected products, beverages,
crafts, festivals and heritage sites for Panama:

- `src/culture/facts.cljc` — the catalog, source of truth (keyed by
  uppercase ISO3, mirroring `statute.facts`).
- `schema/culture.edn` — DataScript schema.
- `data/culture-tx.edn` — derived DataScript tx-data (regenerated from
  the catalog, never hand-edited).

City-level counterparts live in the `cloud-itonami-municipality-*` repos.
Same provenance discipline as the compliance catalogs: every entry cites a
source URL that was actually fetched and read on `:culture/retrieved-at`;
summaries state only what the cited source confirms. An item not in
`culture.facts/catalog` has no spec-basis — never fabricate one.

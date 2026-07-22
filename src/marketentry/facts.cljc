(ns marketentry.facts
  "Panama market-entry catalog.

  Every field traces to an independently-confirmed official source:

    - Business registration: Registro Público de Panamá
      (https://www.registro-publico.gob.pa/) -- corporate legal
      personality is created by recording the notarized *pacto
      social* there. A licensed Panamanian resident agent (attorney)
      is MANDATORY for a Sociedad Anónima (S.A., Ley N.º 32 de 1927);
      Sociedad de Responsabilidad Limitada (SRL) is Ley N.º 4 de
      2009. Foreign branches must also record in the Public Registry.
    - Public procurement: Ley N.º 22 de 27 de junio de 2006 (creates
      the Dirección General de Contrataciones Públicas, DGCP), as
      substantially amended by Ley N.º 153 de 8 de mayo de 2020. The
      current operative text is the Texto Único ordered by Ley 153
      de 2020 (Gaceta Oficial Digital N.° 29107-A, 7 de septiembre de
      2020), regulated by Decreto Ejecutivo N.° 439 de 10 de
      septiembre de 2020. Cited via
      https://compendio.panamacompra.gob.pa/ (the DGCP's own
      dgcp.gob.pa domain was unreachable this session -- do not cite
      it). PanamaCompra (https://www.panamacompra.gob.pa/) is the
      mandatory e-procurement portal per Artículo 172 of Ley 22 de
      2006, administered by DGCP.
    - Procurement disputes: Tribunal Administrativo de Contrataciones
      Públicas (TACP, https://tacp.gob.pa/) -- a body DISTINCT from
      DGCP. DGCP is the executive regulator that runs PanamaCompra;
      TACP independently adjudicates bid protests/procurement
      disputes. Never attribute dispute adjudication to DGCP.
    - Tax registration: RUC (Registro Único de Contribuyente), issued
      by DGI (Dirección General de Ingresos), part of the Ministerio
      de Economía y Finanzas (MEF, https://dgi.mef.gob.pa/). For
      legal persons the RUC is generated at Public Registry
      incorporation.
    - Commercial operating license: Aviso de Operación, issued via
      the Panama Emprende platform under the Ministerio de Comercio
      e Industrias (MICI) -- a THIRD, distinct required-evidence
      item, separate from RUC and the Public Registry. (The Panama
      Emprende domain itself was unreachable this session --
      attribution here is by institutional name per the verified
      brief, not an independently browsed URL; do not fabricate one.)
    - Constitutional restriction: Constitución Política de Panamá,
      Artículo 288 reserves *comercio al por menor* (retail trade) to
      Panamanian nationals/legal entities (narrow historical
      exceptions). This is SECTOR-CONDITIONAL -- it only bears on
      retail-trade-sector engagements with non-Panamanian ownership,
      never a blanket restriction on foreign investment generally.

  Explicitly NOT claimed here (fabrication traps this catalog avoids):
  no hardcoded business-registration turnaround time, no bare
  \"Ley 22 de 2006\" cite for the current text (Texto Único per Ley
  153 de 2020 instead), no unqualified \"100% foreign ownership
  allowed\", no attribution of Aviso de Operación to DGI/Registro
  Público, no \"Ley 54\" investment-stability citation (not
  independently confirmed).

  A jurisdiction not in `catalog` has NO spec-basis, full stop --
  extend `catalog`, never invent an owner-authority/legal-basis/URL.")

(def catalog
  {"PAN" {:name "Panama"
          :owner-authority "Dirección General de Contrataciones Públicas (DGCP) / PanamaCompra"
          :legal-basis "Texto Único de la Ley N.º 22 de 27 de junio de 2006 (Que regula la Contratación Pública), según reordenado por la Ley N.º 153 de 8 de mayo de 2020 (Gaceta Oficial Digital N.° 29107-A, 7 de septiembre de 2020); reglamentada por el Decreto Ejecutivo N.° 439 de 10 de septiembre de 2020"
          :national-spec "PanamaCompra e-procurement portal -- registro obligatorio para entidades públicas, Artículo 172 de la Ley 22 de 2006"
          :provenance "https://www.panamacompra.gob.pa/ ; https://compendio.panamacompra.gob.pa/"
          :required-evidence ["Registro Público de Panamá incorporation record (pacto social notariado; agente residente panameño para S.A.)"
                               "DGI RUC (Registro Único de Contribuyente) tax-registration record"
                               "MICI Aviso de Operación (Panama Emprende) commercial-license record"
                               "DGCP/PanamaCompra procurement-participation registration record"]
          ;; resident-agent sub-schema -- mirrors the AGO template's
          ;; `:rep-*` triple, grounded in the Public Registry / Ley 32.
          :rep-owner-authority "Registro Público de Panamá"
          :rep-legal-basis "Agente residente panameño licenciado (abogado) MANDATORIO para constituir una Sociedad Anónima (Ley N.º 32 de 1927); Sociedad de Responsabilidad Limitada bajo Ley N.º 4 de 2009"
          :rep-provenance "https://www.registro-publico.gob.pa/"
          ;; corporate tax-id sub-schema -- mirrors the AGO template's
          ;; `:corporate-number-*` triple.
          :corporate-number-owner-authority "DGI (Dirección General de Ingresos) / Ministerio de Economía y Finanzas (MEF)"
          :corporate-number-legal-basis "RUC (Registro Único de Contribuyente) -- para personas jurídicas se genera al inscribirse en el Registro Público"
          :corporate-number-provenance "https://dgi.mef.gob.pa/"
          ;; flagship sector-conditional check -- genuinely new for
          ;; this vertical (grep-verified absent as a governor check
          ;; fleet-wide at build time): a CONSTITUTIONAL restriction,
          ;; not a procurement or tax rule, and conditional on the
          ;; engagement's own declared sector + ownership -- never a
          ;; blanket foreign-investment ban.
          :retail-trade-owner-authority "Constitución Política de la República de Panamá"
          :retail-trade-legal-basis "Artículo 288 -- reserva el comercio al por menor (retail trade) a panameños y personas jurídicas panameñas, con excepciones históricas limitadas"
          :retail-trade-provenance "Constitución Política de la República de Panamá, Art. 288 (texto constitucional citado por el brief verificado de esta tarea; ninguna URL oficial específica fue verificada de forma independiente en esta sesión -- no se fabrica una)"
          ;; dispute forum -- a DISTINCT body from DGCP, cited so the
          ;; advisor/governor never conflate the two.
          :dispute-forum-authority "Tribunal Administrativo de Contrataciones Públicas (TACP)"
          :dispute-forum-note "Órgano DISTINTO de la DGCP -- adjudica disputas/impugnaciones de contratación pública de forma independiente; la DGCP es el regulador ejecutivo que opera PanamaCompra"
          :dispute-forum-provenance "https://tacp.gob.pa/"}})

(defn spec-basis [iso3] (get catalog iso3))
(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s) missing (remove catalog iso3s)]
     {:requested (count iso3s) :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note "R0 catalog seed"})))
(defn required-evidence-satisfied? [iso3 submitted]
  (when-let [{:keys [required-evidence]} (spec-basis iso3)]
    (= (count required-evidence) (count (filter (set submitted) required-evidence)))))
(defn evidence-checklist [iso3] (:required-evidence (spec-basis iso3) []))
(defn rep-spec-basis [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:rep-owner-authority sb)
      (select-keys sb [:rep-owner-authority :rep-legal-basis :rep-provenance]))))
(defn corporate-number-spec-basis [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:corporate-number-owner-authority sb)
      (select-keys sb [:corporate-number-owner-authority :corporate-number-legal-basis :corporate-number-provenance]))))
(defn retail-trade-spec-basis
  "Spec-basis for the Art. 288 sector-conditional retail-trade
  restriction -- used by the governor's flagship check to cite an
  official basis rather than assert the restriction bare."
  [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:retail-trade-owner-authority sb)
      (select-keys sb [:retail-trade-owner-authority :retail-trade-legal-basis :retail-trade-provenance]))))
(defn dispute-forum-spec-basis
  "The procurement-dispute adjudication forum -- TACP, a body
  DISTINCT from DGCP. Exposed so callers never conflate the two."
  [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:dispute-forum-authority sb)
      (select-keys sb [:dispute-forum-authority :dispute-forum-note :dispute-forum-provenance]))))

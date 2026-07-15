(ns statute.facts
  "General-law compliance catalog for Panama (PAN). Like
  cloud-itonami-iso3166-ury/-cri, this repo had no `marketentry.facts`
  implementation yet (blueprint-only) -- this is the FIRST code-bearing
  content in this repo, self-contained with its own deps.edn. Mirrors
  cloud-itonami-iso3166-jpn/-usa/-esp/-swe/-nor/-dnk/-fin/-prt/-bel/-bra/-mex/-chl/-arg/-zaf/-col/-ury/-cri's
  `statute.facts` (ADR-2607141700, cloud-itonami-compliance-fact-federation).

  Every entry cites an OFFICIAL LEGISPAN (s3-legispan.asamblea.gob.pa,
  the National Assembly of Panama's own legislative-metadata archive)
  URL -- never fabricated. Both PDFs initially rendered as illegible
  binary streams via the first WebFetch pass, but each one's saved-PDF
  path was re-read with the Read tool, which rendered a clean LEGISPAN
  metadata cover page (Tipo de Norma / Número / Año / Fecha / Titulo /
  Gaceta Oficial / Publicada el) in every case -- a strong, structured
  primary-source format, distinct from the free-text statute PDFs seen
  elsewhere in this family.

  A third entry (Código de Trabajo, Decreto de Gabinete 252 de 1971)
  was attempted but abandoned: infojuridica.procuraduria-admon.gob.pa
  had no matching record for the numsec tried,
  organojudicial.gob.pa returned HTTP 403, and mitradel.gob.pa (the
  Ministry of Labor's own PDF) also returned HTTP 403 -- rather than
  guess at an unverified LEGISPAN S3 URL for this one, it was left out
  entirely. Only 2 entries in this catalog, matching the honesty
  discipline already established for Denmark's statute.facts.

  A law not in this table has NO spec-basis, full stop; extend
  `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of statute entries."
  {"PAN"
   [{:statute/id "pan.ley-2-1916-codigo-comercio"
     :statute/title "Ley N.º 2 de 1916 (aprueba el Código Civil, el Código de Comercio, y otros códigos)"
     :statute/jurisdiction "PAN"
     :statute/kind :law
     :statute/law-number "Ley N.º 2 de 1916"
     :statute/url "https://s3-legispan.asamblea.gob.pa/legispan/NORMAS/1910/1916/LEY/Administrador%20Legispan_02418_1916_9_7_ASAMBLEA%20NACIONAL_2.pdf"
     :statute/url-provenance :official-legispan-asamblea-gob-pa
     :statute/enacted-date "1916-08-22"
     :statute/retrieved-at "2026-07-16"
     :statute/topic #{:corporate-governance :incorporation}}
    {:statute/id "pan.ley-81-2019-proteccion-datos"
     :statute/title "Ley N.º 81 de 2019 (Sobre Protección de Datos Personales)"
     :statute/jurisdiction "PAN"
     :statute/kind :law
     :statute/law-number "Ley N.º 81 de 2019"
     :statute/url "https://s3-legispan.asamblea.gob.pa/legispan/NORMAS/2010/2019/LEY/Administrador%20Legispan_28743-A_2019_3_29_ASAMBLEA%20NACIONAL_81.pdf"
     :statute/url-provenance :official-legispan-asamblea-gob-pa
     :statute/enacted-date "2019-03-26"
     :statute/retrieved-at "2026-07-16"
     :statute/topic #{:data-protection :privacy}}]})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-pan statute.facts Wave 0 (ADR-2607141700): "
                 (count (get catalog "PAN")) " PAN statutes seeded with an "
                 "official LEGISPAN citation. Extend "
                 "`statute.facts/catalog`, never fabricate a law-id or URL.")})))

(defn by-topic [iso3 topic]
  (filterv #(contains? (:statute/topic %) topic) (spec-basis iso3)))

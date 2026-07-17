(ns culture.facts
  "Country-level regional-culture catalog for Panama (PAN) -- national
  dishes, protected products, beverages, crafts, festivals and heritage
  sites, per ADR-2607171400 addendum 2 (cloud-itonami-municipality-
  culture-catalog Wave 1, in com-junkawasaki/root). Sibling namespace to
  `marketentry.facts` / `statute.facts` (ADR-2607141700); city-level
  counterparts live in the cloud-itonami-municipality-* repos.

  Catalog is keyed by UPPERCASE ISO3 (mirrors `statute.facts`); entries
  carry no :culture/municipality (that attribute is city-level only).

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms. An item not in this table has NO spec-basis, full
  stop; extend `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of culture entries."
  {"PAN"
   [{:culture/id "pan.dish.sancocho"
     :culture/name "Sancocho"
     :culture/country "PAN"
     :culture/kind :dish
     :culture/summary "Traditional stew found across Caribbean and Latin American cuisines; the Panamanian version, sancocho de gallina, is the national dish of Panama."
     :culture/url "https://en.wikipedia.org/wiki/Sancocho"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "pan.dish.carimanola"
     :culture/name "Carimañola"
     :culture/country "PAN"
     :culture/kind :dish
     :culture/summary "Traditional fried food of yuca dough stuffed with meat or cheese, commonly found in the Caribbean coastal regions of Colombia and Panama."
     :culture/url "https://en.wikipedia.org/wiki/Carima%C3%B1ola"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "pan.dish.ceviche"
     :culture/name "Ceviche"
     :culture/country "PAN"
     :culture/kind :dish
     :culture/summary "Cold dish of marinated raw seafood shared across Latin America; in Panama it is prepared with lemon juice, chopped onion, celery, cilantro, peppers and sea salt, with corvina ceviche a popular restaurant appetizer."
     :culture/url "https://en.wikipedia.org/wiki/Ceviche"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "pan.beverage.seco-herrerano"
     :culture/name "Seco Herrerano"
     :culture/country "PAN"
     :culture/kind :beverage
     :culture/summary "Brand of aguardiente (sugarcane liquor) produced in Pesé, Panama, created by the Varela family in 1908."
     :culture/url "https://en.wikipedia.org/wiki/Seco_Herrerano"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "pan.product.geisha-coffee"
     :culture/name "Geisha coffee"
     :culture/country "PAN"
     :culture/kind :product
     :culture/summary "Coffee variety of Ethiopian origin that spread to Panama's Boquete region in the 1960s; Panamanian Geisha lots from Hacienda La Esmeralda have repeatedly set world-record auction prices."
     :culture/url "https://en.wikipedia.org/wiki/Geisha_(coffee)"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "pan.craft.mola"
     :culture/name "Mola"
     :culture/country "PAN"
     :culture/kind :craft
     :culture/summary "Hand-made textile that forms part of the traditional women's clothing of the indigenous Guna people of Panama."
     :culture/url "https://en.wikipedia.org/wiki/Mola_(art_form)"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "pan.craft.pollera"
     :culture/name "Pollera"
     :culture/country "PAN"
     :culture/kind :craft
     :culture/summary "Traditional garment that serves as Panama's national dress, crafted by specialized artisans with elaborate embroidery and lacework."
     :culture/url "https://en.wikipedia.org/wiki/Pollera"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "pan.heritage.panama-viejo"
     :culture/name "Panamá Viejo"
     :culture/country "PAN"
     :culture/kind :heritage
     :culture/summary "Archaeological site of the oldest continuously occupied European settlement on the Pacific coast of the Americas; together with the historic district of Panamá, a UNESCO World Heritage Site since 1997."
     :culture/url "https://en.wikipedia.org/wiki/Panama_Viejo"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}]})

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
      :note (str "cloud-itonami-iso3166-pan culture catalog "
                 "(ADR-2607171400 addendum 2, Wave 1): " (count (get catalog "PAN"))
                 " PAN entries, each with a fetched-and-read citation. "
                 "Extend `culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [iso3 kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis iso3)))

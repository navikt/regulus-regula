package no.nav.tsm.regulus.regula.rules.trees.validering

import no.nav.tsm.regulus.regula.payload.Aktivitet

internal data class ValideringRulePayload(
    val rulesetVersion: String?,
    val papirsykmelding: Boolean,
    val aktivitet: List<Aktivitet>,
    val legekontorOrgnr: String?,
    val behandlerFnr: String?,
    val avsenderFnr: String?,
    val pasientIdent: String,
    /**
     * Liste over nøkler av utdypende opplysninger som er besvart, f.eks:
     * ["6.5.1", "6.5.2", "6.5.3", "6.5.4"]
     */
    val besvarteUtypendeOpplysninger: List<String> = emptyList(),
)

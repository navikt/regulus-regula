package no.nav.tsm.regulus.regula.rules.trees.validering

import no.nav.tsm.regulus.regula.executor.BasePayload
import no.nav.tsm.regulus.regula.payload.Aktivitet

internal data class ValideringRulePayload(
    override val sykmeldingId: String,
    val rulesetVersion: String?,
    val papirsykmelding: Boolean,
    val aktivitet: List<Aktivitet>,
    val legekontorOrgnr: String?,
    val behandlerFnr: String,
    val avsenderFnr: String,
    val pasientIdent: String,
    val utdypendeOpplysninger: Map<String, Map<String, Map<String, String>>>?,
) : BasePayload

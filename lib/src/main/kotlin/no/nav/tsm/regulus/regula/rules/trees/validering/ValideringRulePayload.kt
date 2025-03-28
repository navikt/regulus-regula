package no.nav.tsm.regulus.regula.rules.trees.validering

import no.nav.tsm.regulus.regula.executor.BasePayload
import no.nav.tsm.regulus.regula.payload.SykmeldingPeriode

internal data class ValideringRulePayload(
    override val sykmeldingId: String,
    val rulesetVersion: String?,
    val perioder: List<SykmeldingPeriode>,
    val legekontorOrgnr: String?,
    val behandlerFnr: String,
    val avsenderFnr: String,
    val pasientIdent: String,
    val utdypendeOpplysninger: Map<String, Map<String, Map<String, String>>>?,
) : BasePayload

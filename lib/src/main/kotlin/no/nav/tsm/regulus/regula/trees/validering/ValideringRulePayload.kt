package no.nav.tsm.regulus.regula.trees.validering

import no.nav.tsm.regulus.regula.executor.BasePayload
import no.nav.tsm.regulus.regula.payload.SykmeldingPeriode

internal data class ValideringRulePayload(
    override val sykmeldingId: String,
    val rulesetVersion: String,
    val perioder: List<SykmeldingPeriode>,
    val legekontorOrgnr: String?,
    val behandlerFnr: String,
    val avsenderFnr: String,
    val pasientIdent: String,
    val utdypendeOpplysninger: Map<String, Map<String, SporsmalSvar>>,
) : BasePayload

internal data class SporsmalSvar(val sporsmal: String, val svar: String)

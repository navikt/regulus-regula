package no.nav.tsm.regulus.regula.trees.validation

import java.time.LocalDate
import no.nav.tsm.regulus.regula.executor.BasePayload

data class ValidationRulePayload(
    override val sykmeldingId: String,
    val rulesetVersion: String,
    val perioder: List<FomTom>,
    val legekontorOrgnr: String,
    val behandlerFnr: String,
    val avsenderFnr: String,
    val patientPersonNumber: String,
) : BasePayload

data class FomTom(val fom: LocalDate, val tom: LocalDate)

package no.nav.tsm.regulus.regula.trees.validation

import java.time.LocalDate

data class ValidationRulePayload(
    val rulesetVersion: String,
    val perioder: List<FomTom>,
    val legekontorOrgnr: String,
    val behandlerFnr: String,
    val avsenderFnr: String,
    val patientPersonNumber: String
)

data class FomTom(
    val fom: LocalDate,
    val tom: LocalDate
)

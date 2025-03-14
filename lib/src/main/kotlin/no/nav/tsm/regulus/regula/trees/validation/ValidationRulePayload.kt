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
    val utdypendeOpplysninger: Map<String, Map<String, SporsmalSvar>>,
) : BasePayload

data class FomTom(val fom: LocalDate, val tom: LocalDate)

data class SporsmalSvar(
    val sporsmal: String,
    val svar: String,
    val restriksjoner: List<SvarRestriksjon>,
)

enum class SvarRestriksjon(
    val codeValue: String,
    val text: String,
    val oid: String = "2.16.578.1.12.4.1.1.8134",
) {
    SKJERMET_FOR_ARBEIDSGIVER("A", "Informasjonen skal ikke vises arbeidsgiver"),
    SKJERMET_FOR_PASIENT("P", "Informasjonen skal ikke vises pasient"),
    SKJERMET_FOR_NAV("N", "Informasjonen skal ikke vises NAV"),
}

package no.nav.tsm.regulus.regula.trees.pasientUnder13

import java.time.LocalDate
import no.nav.tsm.regulus.regula.payload.BasePayload
import no.nav.tsm.regulus.regula.payload.FomTom

data class PasientUnder13RulePayload(
    override val sykmeldingId: String,
    val perioder: List<FomTom>,
    val pasientFodselsdato: LocalDate,
) : BasePayload

package no.nav.tsm.regulus.regula.trees.dato

import java.time.LocalDateTime
import no.nav.tsm.regulus.regula.payload.BasePayload
import no.nav.tsm.regulus.regula.payload.FomTom

data class DatoRulePayload(
    override val sykmeldingId: String,
    val perioder: List<FomTom>,
    val signaturdato: LocalDateTime,
) : BasePayload

package no.nav.tsm.regulus.regula.trees.periode

import java.time.LocalDateTime
import no.nav.tsm.regulus.regula.executor.BasePayload
import no.nav.tsm.regulus.regula.trees.validation.FomTom

data class PeriodeRulePayload(
    override val sykmeldingId: String,
    val perioder: List<FomTom>,
    val signaturdato: LocalDateTime,
) : BasePayload

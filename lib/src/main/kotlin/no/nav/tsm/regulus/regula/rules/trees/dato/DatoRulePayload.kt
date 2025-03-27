package no.nav.tsm.regulus.regula.rules.trees.dato

import java.time.LocalDateTime
import no.nav.tsm.regulus.regula.executor.BasePayload
import no.nav.tsm.regulus.regula.payload.SykmeldingPeriode

internal data class DatoRulePayload(
    override val sykmeldingId: String,
    val perioder: List<SykmeldingPeriode>,
    val signaturdato: LocalDateTime,
) : BasePayload

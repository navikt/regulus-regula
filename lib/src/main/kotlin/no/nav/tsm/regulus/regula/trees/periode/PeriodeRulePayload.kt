package no.nav.tsm.regulus.regula.trees.periode

import java.time.LocalDateTime
import no.nav.tsm.regulus.regula.executor.BasePayload
import no.nav.tsm.regulus.regula.payload.SykmeldingPeriode

internal data class PeriodeRulePayload(
    override val sykmeldingId: String,
    val perioder: List<SykmeldingPeriode>,
    val behandletTidspunkt: LocalDateTime,
    val receivedDate: LocalDateTime,
) : BasePayload

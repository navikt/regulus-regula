package no.nav.tsm.regulus.regula.trees.pasientUnder13

import java.time.LocalDate
import no.nav.tsm.regulus.regula.executor.BasePayload
import no.nav.tsm.regulus.regula.payload.SykmeldingPeriode

internal data class PasientUnder13RulePayload(
    override val sykmeldingId: String,
    val perioder: List<SykmeldingPeriode>,
    val pasientFodselsdato: LocalDate,
) : BasePayload

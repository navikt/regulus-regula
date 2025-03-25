package no.nav.tsm.regulus.regula.trees.tilbakedatering

import java.time.LocalDate
import java.time.LocalDateTime
import no.nav.tsm.regulus.regula.payload.BasePayload
import no.nav.tsm.regulus.regula.payload.Diagnose
import no.nav.tsm.regulus.regula.payload.SykmeldingPeriode

data class TidligereSykmelding(
    val sykmeldingId: String,
    val perioder: List<SykmeldingPeriode>,
    val hoveddiagnose: Diagnose?,
)

data class TilbakedateringRulePayload(
    override val sykmeldingId: String,
    val signaturdato: LocalDateTime,
    val perioder: List<SykmeldingPeriode>,
    val startdato: LocalDate?,
    val hoveddiagnose: Diagnose?,
    val tidligereSykmeldinger: List<TidligereSykmelding>,
    // TODO: bedre strukturering?
    val begrunnelseIkkeKontakt: String?,
) : BasePayload

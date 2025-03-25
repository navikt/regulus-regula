package no.nav.tsm.regulus.regula.trees.tilbakedatering

import java.time.LocalDate
import java.time.LocalDateTime
import no.nav.tsm.regulus.regula.payload.BasePayload
import no.nav.tsm.regulus.regula.payload.SykmeldingPeriode

data class TidligereSykmelding(val sykmeldingId: String, val perioder: List<SykmeldingPeriode>)

data class TilbakedateringRulePayload(
    override val sykmeldingId: String,
    val signaturdato: LocalDateTime,
    val perioder: List<SykmeldingPeriode>,
    val startdato: LocalDate?,
    val hoveddiagnoseSystem: String?,
    val tidligereSykmeldinger: List<TidligereSykmelding>,
    // TODO: bedre strukturering?
    val begrunnelseIkkeKontakt: String?,
    // TODO: Usikker på navn/type, kan inferres/regnes?
    val dagerForArbeidsgiverperiodeCheck: List<LocalDate>,
    val forlengelse: Boolean?,
) : BasePayload

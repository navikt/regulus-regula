package no.nav.tsm.regulus.regula.rules.trees.tilbakedatering

import java.time.LocalDateTime
import no.nav.tsm.regulus.regula.executor.BasePayload
import no.nav.tsm.regulus.regula.payload.Diagnose
import no.nav.tsm.regulus.regula.payload.SykmeldingPeriode
import no.nav.tsm.regulus.regula.payload.TidligereSykmelding

internal data class TilbakedateringRulePayload(
    override val sykmeldingId: String,
    val signaturdato: LocalDateTime,
    val perioder: List<SykmeldingPeriode>,
    val hoveddiagnose: Diagnose?,
    val tidligereSykmeldinger: List<TidligereSykmelding>,
    // TODO: bedre strukturering?
    val begrunnelseIkkeKontakt: String?,
) : BasePayload

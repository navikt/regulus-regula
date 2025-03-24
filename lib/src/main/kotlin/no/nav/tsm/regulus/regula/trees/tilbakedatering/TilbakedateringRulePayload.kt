package no.nav.tsm.regulus.regula.trees.tilbakedatering

import java.time.LocalDate
import java.time.LocalDateTime
import no.nav.tsm.regulus.regula.payload.BasePayload
import no.nav.tsm.regulus.regula.payload.FomTom

data class TilbakedateringRulePayload(
    override val sykmeldingId: String,
    val signaturdato: LocalDateTime,
    val perioder: List<FomTom>,
    val startdato: LocalDate?,
    val hoveddiagnoseSystem: String?,
    // TODO: bedre strukturering?
    val begrunnelseIkkeKontakt: String?,
    // TODO: Må dette være hele perioden med fom/tom som i syfosmregler?
    val ettersendingAv: String?,
    // TODO: Usikker på navn/type, kan inferres/regnes?
    val dagerForArbeidsgiverperiodeCheck: List<LocalDate>,
    val forlengelse: Boolean?,
) : BasePayload

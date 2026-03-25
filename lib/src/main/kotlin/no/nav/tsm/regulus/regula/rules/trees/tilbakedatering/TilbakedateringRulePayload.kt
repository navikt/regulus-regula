package no.nav.tsm.regulus.regula.rules.trees.tilbakedatering

import java.time.LocalDateTime
import no.nav.tsm.regulus.regula.payload.Aktivitet
import no.nav.tsm.regulus.regula.payload.Diagnose
import no.nav.tsm.regulus.regula.payload.TidligereSykmelding

internal data class TilbakedateringRulePayload(
    val signaturdato: LocalDateTime,
    val aktivitet: List<Aktivitet>,
    val hoveddiagnose: Diagnose?,
    val tidligereSykmeldinger: List<TidligereSykmelding>,
    val begrunnelseIkkeKontakt: String?,
)

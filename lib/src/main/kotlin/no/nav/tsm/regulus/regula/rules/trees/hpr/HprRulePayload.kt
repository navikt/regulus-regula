package no.nav.tsm.regulus.regula.rules.trees.hpr

import java.time.LocalDateTime
import no.nav.tsm.regulus.regula.payload.Aktivitet
import no.nav.tsm.regulus.regula.payload.BehandlerGodkjenning
import no.nav.tsm.regulus.regula.payload.TidligereSykmelding

internal data class HprRulePayload(
    val behandlerGodkjenninger: List<BehandlerGodkjenning>?,
    val aktivitet: List<Aktivitet>,
    val tidligereSykmeldinger: List<TidligereSykmelding>,
    val signaturdato: LocalDateTime,
)

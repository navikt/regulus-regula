package no.nav.tsm.regulus.regula.rules.trees.hpr

import java.time.LocalDateTime
import no.nav.tsm.regulus.regula.executor.BasePayload
import no.nav.tsm.regulus.regula.payload.Aktivitet
import no.nav.tsm.regulus.regula.payload.SykmelderGodkjenning
import no.nav.tsm.regulus.regula.payload.TidligereSykmelding

internal data class HprRulePayload(
    override val sykmeldingId: String,
    val sykmelderGodkjenninger: List<SykmelderGodkjenning>?,
    val aktivitet: List<Aktivitet>,
    val tidligereSykmeldinger: List<TidligereSykmelding>,
    val signaturdato: LocalDateTime,
) : BasePayload

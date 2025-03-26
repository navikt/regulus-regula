package no.nav.tsm.regulus.regula.trees.arbeidsuforhet

import no.nav.tsm.regulus.regula.executor.BasePayload
import no.nav.tsm.regulus.regula.payload.Diagnose

internal data class ArbeidsuforhetRulePayload(
    override val sykmeldingId: String,
    val hoveddiagnose: Diagnose?,
    val bidiagnoser: List<Diagnose>,
    val annenFraversArsak: AnnenFraversArsak?,
) : BasePayload

internal data class AnnenFraversArsak(val grunn: List<String>, val beskrivelse: String?)

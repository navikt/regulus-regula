package no.nav.tsm.regulus.regula.trees.arbeidsuforhet

import no.nav.tsm.regulus.regula.payload.BasePayload

data class ArbeidsuforhetRulePayload(
    override val sykmeldingId: String,
    val hoveddiagnose: Diagnose?,
    val bidiagnoser: List<Diagnose>,
    val annenFraversArsak: AnnenFraversArsak?,
) : BasePayload

data class Diagnose(val kode: String, val system: String)

data class AnnenFraversArsak(val grunn: List<String>, val beskrivelse: String?)

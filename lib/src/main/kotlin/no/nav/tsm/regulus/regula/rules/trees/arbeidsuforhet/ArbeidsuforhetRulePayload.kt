package no.nav.tsm.regulus.regula.rules.trees.arbeidsuforhet

import no.nav.tsm.regulus.regula.payload.AnnenFravarsArsak
import no.nav.tsm.regulus.regula.payload.Diagnose

internal data class ArbeidsuforhetRulePayload(
    val hoveddiagnose: Diagnose?,
    val bidiagnoser: List<Diagnose>,
    val annenFravarsArsak: AnnenFravarsArsak?,
)

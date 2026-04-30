package no.nav.tsm.regulus.regula.rules.trees.arbeidsuforhet

import no.nav.tsm.diagnoser.ICPC2
import no.nav.tsm.regulus.regula.payload.AnnenFravarsArsak
import no.nav.tsm.regulus.regula.payload.Diagnose

internal fun testArbeidsuforhetPayload(
    hoveddiagnose: Diagnose? = Diagnose(kode = "Y01", system = ICPC2.OID),
    bidiagnoser: List<Diagnose> = emptyList(),
    annenFravarsArsak: AnnenFravarsArsak? = null,
) =
    ArbeidsuforhetRulePayload(
        hoveddiagnose = hoveddiagnose,
        bidiagnoser = bidiagnoser,
        annenFravarsArsak = annenFravarsArsak,
    )

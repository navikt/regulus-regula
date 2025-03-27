package no.nav.tsm.regulus.regula.rules.trees.arbeidsuforhet

import no.nav.helse.diagnosekoder.Diagnosekoder
import no.nav.tsm.regulus.regula.payload.Diagnose

internal fun testArbeidsuforhetPayload(
    hoveddiagnose: Diagnose? = Diagnose(kode = "Y01", system = Diagnosekoder.ICPC2_CODE),
    bidiagnoser: List<Diagnose> = emptyList<Diagnose>(),
    annenFraversArsak: AnnenFraversArsak? = null,
) =
    ArbeidsuforhetRulePayload(
        sykmeldingId = "foo-bar-baz",
        hoveddiagnose = hoveddiagnose,
        bidiagnoser = bidiagnoser,
        annenFraversArsak = annenFraversArsak,
    )

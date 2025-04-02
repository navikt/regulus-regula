package no.nav.tsm.regulus.regula.dsl

import no.nav.tsm.regulus.regula.RegulaJuridiskHenvisning
import no.nav.tsm.regulus.regula.RegulaLovverk

internal enum class RuleJuridisk(val juridiskHenvisning: RegulaJuridiskHenvisning?) {
    INGEN(null),
    FOLKETRYGDLOVEN_8_3_1(
        RegulaJuridiskHenvisning(
            lovverk = RegulaLovverk.FOLKETRYGDLOVEN,
            paragraf = "8-3",
            ledd = 1,
            punktum = null,
            bokstav = null,
        )
    ),
    FOLKETRYGDLOVEN_8_4(
        RegulaJuridiskHenvisning(
            lovverk = RegulaLovverk.FOLKETRYGDLOVEN,
            paragraf = "8-4",
            ledd = 1,
            punktum = null,
            bokstav = null,
        )
    ),
    FOLKETRYGDLOVEN_8_7(
        RegulaJuridiskHenvisning(
            lovverk = RegulaLovverk.FOLKETRYGDLOVEN,
            paragraf = "8-7",
            ledd = null,
            punktum = null,
            bokstav = null,
        )
    ),
    FOLKETRYGDLOVEN_8_7_1(
        RegulaJuridiskHenvisning(
            lovverk = RegulaLovverk.FOLKETRYGDLOVEN,
            paragraf = "8-7",
            ledd = 1,
            punktum = null,
            bokstav = null,
        )
    ),
    FOLKETRYGDLOVEN_8_7_1_1(
        RegulaJuridiskHenvisning(
            lovverk = RegulaLovverk.FOLKETRYGDLOVEN,
            paragraf = "8-7",
            ledd = 1,
            punktum = 1,
            bokstav = null,
        )
    ),
    FOLKETRYGDLOVEN_8_7_2_2(
        RegulaJuridiskHenvisning(
            lovverk = RegulaLovverk.FOLKETRYGDLOVEN,
            paragraf = "8-7",
            ledd = 2,
            punktum = 2,
            bokstav = null,
        )
    ),
}

internal fun RuleJuridisk.toRegulaJuridisk(): RegulaJuridiskHenvisning? {
    return when (this) {
        RuleJuridisk.INGEN -> null
        else -> juridiskHenvisning
    }
}

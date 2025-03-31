package no.nav.tsm.regulus.regula.dsl

import no.nav.tsm.regulus.regula.juridisk.Juridisk
import no.nav.tsm.regulus.regula.juridisk.JuridiskHenvisning
import no.nav.tsm.regulus.regula.juridisk.Lovverk
import no.nav.tsm.regulus.regula.juridisk.MedJuridisk
import no.nav.tsm.regulus.regula.juridisk.UtenJuridisk

internal enum class RuleJuridisk(val juridisk: Juridisk) {
    INGEN(UtenJuridisk),
    FOLKETRYGDLOVEN_8_3_1(
        MedJuridisk(
            JuridiskHenvisning(
                lovverk = Lovverk.FOLKETRYGDLOVEN,
                paragraf = "8-3",
                ledd = 1,
                punktum = null,
                bokstav = null,
            )
        )
    ),
    FOLKETRYGDLOVEN_8_4(
        MedJuridisk(
            JuridiskHenvisning(
                lovverk = Lovverk.FOLKETRYGDLOVEN,
                paragraf = "8-4",
                ledd = 1,
                punktum = null,
                bokstav = null,
            )
        )
    ),
    FOLKETRYGDLOVEN_8_7(
        MedJuridisk(
            JuridiskHenvisning(
                lovverk = Lovverk.FOLKETRYGDLOVEN,
                paragraf = "8-7",
                ledd = null,
                punktum = null,
                bokstav = null,
            )
        )
    ),
    FOLKETRYGDLOVEN_8_7_1(
        MedJuridisk(
            JuridiskHenvisning(
                lovverk = Lovverk.FOLKETRYGDLOVEN,
                paragraf = "8-7",
                ledd = 1,
                punktum = null,
                bokstav = null,
            )
        )
    ),
    FOLKETRYGDLOVEN_8_7_1_1(
        MedJuridisk(
            JuridiskHenvisning(
                lovverk = Lovverk.FOLKETRYGDLOVEN,
                paragraf = "8-7",
                ledd = 1,
                punktum = 1,
                bokstav = null,
            )
        )
    ),
    FOLKETRYGDLOVEN_8_7_2_2(
        MedJuridisk(
            JuridiskHenvisning(
                lovverk = Lovverk.FOLKETRYGDLOVEN,
                paragraf = "8-7",
                ledd = 2,
                punktum = 2,
                bokstav = null,
            )
        )
    ),
}

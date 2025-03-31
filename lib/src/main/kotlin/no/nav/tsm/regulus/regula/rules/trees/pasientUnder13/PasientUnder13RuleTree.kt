package no.nav.tsm.regulus.regula.rules.trees.pasientUnder13

import no.nav.tsm.regulus.regula.dsl.RuleStatus.INVALID
import no.nav.tsm.regulus.regula.dsl.RuleStatus.OK
import no.nav.tsm.regulus.regula.dsl.no
import no.nav.tsm.regulus.regula.dsl.tree
import no.nav.tsm.regulus.regula.dsl.yes
import no.nav.tsm.regulus.regula.juridisk.JuridiskHenvisning
import no.nav.tsm.regulus.regula.juridisk.Lovverk
import no.nav.tsm.regulus.regula.juridisk.MedJuridisk

internal val pasientUnder13RuleTree =
    tree<PasientUnder13Rule>(PasientUnder13Rule.PASIENT_YNGRE_ENN_13) {
        yes(INVALID, PasientUnder13Rule.Outcomes.PASIENT_YNGRE_ENN_13)
        no(OK)
    } to
        MedJuridisk(
            JuridiskHenvisning(
                lovverk = Lovverk.FOLKETRYGDLOVEN,
                paragraf = "8-3",
                ledd = 1,
                punktum = null,
                bokstav = null,
            )
        )

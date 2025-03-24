package no.nav.tsm.regulus.regula.trees.pasientUnder13

import no.nav.tsm.regulus.regula.dsl.tree
import no.nav.tsm.regulus.regula.executor.MedJuridisk
import no.nav.tsm.regulus.regula.executor.RuleStatus.INVALID
import no.nav.tsm.regulus.regula.executor.RuleStatus.OK
import no.nav.tsm.regulus.regula.executor.no
import no.nav.tsm.regulus.regula.executor.yes
import no.nav.tsm.regulus.regula.juridisk.JuridiskHenvisning
import no.nav.tsm.regulus.regula.juridisk.Lovverk

val pasientUnder13RuleTree =
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

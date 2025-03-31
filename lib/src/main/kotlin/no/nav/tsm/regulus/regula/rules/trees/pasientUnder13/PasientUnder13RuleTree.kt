package no.nav.tsm.regulus.regula.rules.trees.pasientUnder13

import no.nav.tsm.regulus.regula.dsl.RuleJuridisk
import no.nav.tsm.regulus.regula.dsl.TreeNode.LeafNode.*
import no.nav.tsm.regulus.regula.dsl.tree

internal val pasientUnder13RuleTree =
    tree(PasientUnder13Rule.PASIENT_YNGRE_ENN_13) {
        yes(
            INVALID(
                PasientUnder13Rule.Outcomes.PASIENT_YNGRE_ENN_13,
                RuleJuridisk.FOLKETRYGDLOVEN_8_3_1,
            )
        )
        no(OK(RuleJuridisk.FOLKETRYGDLOVEN_8_3_1))
    }

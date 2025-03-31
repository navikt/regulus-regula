package no.nav.tsm.regulus.regula.rules.trees.legeSuspensjon

import no.nav.tsm.regulus.regula.dsl.TreeNode.LeafNode.*
import no.nav.tsm.regulus.regula.dsl.tree
import no.nav.tsm.regulus.regula.juridisk.UtenJuridisk

internal val legeSuspensjonRuleTree =
    tree(LegeSuspensjonRule.BEHANDLER_SUSPENDERT) {
        yes(INVALID(LegeSuspensjonRule.Outcomes.BEHANDLER_SUSPENDERT))
        no(OK())
    } to UtenJuridisk

package no.nav.tsm.regulus.regula.trees.legeSuspensjon

import no.nav.tsm.regulus.regula.dsl.tree
import no.nav.tsm.regulus.regula.executor.RuleStatus
import no.nav.tsm.regulus.regula.executor.no
import no.nav.tsm.regulus.regula.executor.yes
import no.nav.tsm.regulus.regula.juridisk.UtenJuridisk

internal val legeSuspensjonRuleTree =
    tree<LegeSuspensjonRule>(LegeSuspensjonRule.BEHANDLER_SUSPENDERT) {
        yes(RuleStatus.INVALID, LegeSuspensjonRule.Outcomes.BEHANDLER_SUSPENDERT)
        no(RuleStatus.OK)
    } to UtenJuridisk

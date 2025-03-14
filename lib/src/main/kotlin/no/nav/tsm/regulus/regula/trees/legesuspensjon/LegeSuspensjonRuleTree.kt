package no.nav.tsm.regulus.regula.trees.legesuspensjon

import no.nav.tsm.regulus.regula.dsl.tree
import no.nav.tsm.regulus.regula.executor.RuleResult
import no.nav.tsm.regulus.regula.executor.RuleStatus
import no.nav.tsm.regulus.regula.executor.UtenJuridisk
import no.nav.tsm.regulus.regula.executor.no
import no.nav.tsm.regulus.regula.executor.yes

val legeSuspensjonRuleTree =
    tree<LegeSuspensjonRule, RuleResult>(LegeSuspensjonRule.BEHANDLER_SUSPENDERT) {
        yes(RuleStatus.INVALID, LegeSuspensjonRule.Outcomes.BEHANDLER_SUSPENDERT)
        no(RuleStatus.OK)
    } to UtenJuridisk

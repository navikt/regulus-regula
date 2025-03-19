package no.nav.tsm.regulus.regula.trees.periode

import no.nav.tsm.regulus.regula.dsl.tree
import no.nav.tsm.regulus.regula.executor.RuleResult
import no.nav.tsm.regulus.regula.executor.RuleStatus.*
import no.nav.tsm.regulus.regula.executor.UtenJuridisk
import no.nav.tsm.regulus.regula.executor.no
import no.nav.tsm.regulus.regula.executor.yes

val periodeRuleTree =
    tree<PeriodeRule, RuleResult>(PeriodeRule.FREMDATERT) {
        yes(INVALID, PeriodeRule.Outcomes.FREMDATERT)
        no(PeriodeRule.TILBAKEDATERT_MER_ENN_3_AR) {
            yes(INVALID, PeriodeRule.Outcomes.TILBAKEDATERT_MER_ENN_3_AR)
            no(PeriodeRule.TOTAL_VARIGHET_OVER_ETT_AAR) {
                yes(INVALID, PeriodeRule.Outcomes.TOTAL_VARIGHET_OVER_ETT_AAR)
                no(OK)
            }
        }
    } to UtenJuridisk

package no.nav.tsm.regulus.regula.rules.trees.dato

import no.nav.tsm.regulus.regula.dsl.RuleStatus.*
import no.nav.tsm.regulus.regula.dsl.no
import no.nav.tsm.regulus.regula.dsl.tree
import no.nav.tsm.regulus.regula.dsl.yes
import no.nav.tsm.regulus.regula.juridisk.UtenJuridisk

internal val datoRuleTree =
    tree<DatoRule>(DatoRule.FREMDATERT) {
        yes(INVALID, DatoRule.Outcomes.FREMDATERT)
        no(DatoRule.TILBAKEDATERT_MER_ENN_3_AR) {
            yes(INVALID, DatoRule.Outcomes.TILBAKEDATERT_MER_ENN_3_AR)
            no(DatoRule.TOTAL_VARIGHET_OVER_ETT_AAR) {
                yes(INVALID, DatoRule.Outcomes.TOTAL_VARIGHET_OVER_ETT_AAR)
                no(OK)
            }
        }
    } to UtenJuridisk

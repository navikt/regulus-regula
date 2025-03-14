package no.nav.tsm.regulus.regula.trees.hpr

import no.nav.tsm.regulus.regula.dsl.RuleNode
import no.nav.tsm.regulus.regula.dsl.tree
import no.nav.tsm.regulus.regula.executor.MedJuridisk
import no.nav.tsm.regulus.regula.executor.RuleResult
import no.nav.tsm.regulus.regula.executor.RuleStatus.INVALID
import no.nav.tsm.regulus.regula.executor.RuleStatus.OK
import no.nav.tsm.regulus.regula.executor.no
import no.nav.tsm.regulus.regula.executor.yes
import no.nav.tsm.regulus.regula.juridisk.JuridiskHenvisning
import no.nav.tsm.regulus.regula.juridisk.Lovverk

val hprRuleTree =
    tree<HprRule, RuleResult>(HprRule.BEHANDLER_GYLIDG_I_HPR) {
        no(INVALID, HprRule.Outcomes.BEHANDLER_IKKE_GYLDIG_I_HPR)
        yes(HprRule.BEHANDLER_HAR_AUTORISASJON_I_HPR) {
            no(INVALID, HprRule.Outcomes.BEHANDLER_MANGLER_AUTORISASJON_I_HPR)
            yes(HprRule.BEHANDLER_ER_LEGE_I_HPR) {
                yes(OK)
                no(HprRule.BEHANDLER_ER_TANNLEGE_I_HPR) {
                    yes(OK)
                    no(HprRule.BEHANDLER_ER_MANUELLTERAPEUT_I_HPR) {
                        yesThenSykefravarOver12Uker()
                        no(HprRule.BEHANDLER_ER_FT_MED_TILLEGSKOMPETANSE_I_HPR) {
                            yesThenSykefravarOver12Uker()
                            no(HprRule.BEHANDLER_ER_KI_MED_TILLEGSKOMPETANSE_I_HPR) {
                                yesThenSykefravarOver12Uker()
                                no(INVALID, HprRule.Outcomes.BEHANDLER_IKKE_LE_KI_MT_TL_FT_I_HPR)
                            }
                        }
                    }
                }
            }
        }
    } to
        MedJuridisk(
            JuridiskHenvisning(
                lovverk = Lovverk.FOLKETRYGDLOVEN,
                paragraf = "8-7",
                ledd = 1,
                punktum = null,
                bokstav = null,
            )
        )

private fun RuleNode<HprRule, RuleResult>.yesThenSykefravarOver12Uker() {
    yes(HprRule.SYKEFRAVAR_OVER_12_UKER) {
        yes(INVALID, HprRule.Outcomes.BEHANDLER_MT_FT_KI_OVER_12_UKER)
        no(OK)
    }
}

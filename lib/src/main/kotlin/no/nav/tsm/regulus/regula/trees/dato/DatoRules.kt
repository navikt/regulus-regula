package no.nav.tsm.regulus.regula.trees.dato

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import no.nav.tsm.regulus.regula.dsl.RuleOutput
import no.nav.tsm.regulus.regula.executor.TreeExecutor
import no.nav.tsm.regulus.regula.utils.earliestFom
import no.nav.tsm.regulus.regula.utils.latestTom

internal class DatoRules(periodePayload: DatoRulePayload) :
    TreeExecutor<DatoRule, DatoRulePayload>(datoRuleTree, periodePayload) {
    override fun getRule(rule: DatoRule) = getDatoRule(rule)
}

private fun getDatoRule(rules: DatoRule): DatoRuleFn {
    return when (rules) {
        DatoRule.FREMDATERT -> Rules.fremdatertOver30Dager
        DatoRule.TILBAKEDATERT_MER_ENN_3_AR -> Rules.tilbakeDatertOver3Ar
        DatoRule.TOTAL_VARIGHET_OVER_ETT_AAR -> Rules.varighetOver1AAr
    }
}

private typealias DatoRuleFn = (payload: DatoRulePayload) -> RuleOutput<DatoRule>

private val Rules =
    object {
        val fremdatertOver30Dager: DatoRuleFn = { payload ->
            val forsteFomDato = payload.perioder.earliestFom()
            val genereringsTidspunkt = payload.signaturdato

            val fremdatert = forsteFomDato > genereringsTidspunkt.plusDays(30).toLocalDate()

            RuleOutput(
                ruleInputs =
                    mapOf(
                        "genereringsTidspunkt" to genereringsTidspunkt,
                        "fom" to forsteFomDato,
                        "fremdatert" to fremdatert,
                    ),
                rule = DatoRule.FREMDATERT,
                ruleResult = fremdatert,
            )
        }

        val varighetOver1AAr: DatoRuleFn = { payload ->
            val forsteFomDato = payload.perioder.earliestFom()
            val sisteTomDato = payload.perioder.latestTom()

            val varighetOver1AAr = ChronoUnit.DAYS.between(forsteFomDato, sisteTomDato) > 365

            RuleOutput(
                ruleInputs =
                    mapOf(
                        "fom" to forsteFomDato,
                        "tom" to sisteTomDato,
                        "varighetOver1AAr" to varighetOver1AAr,
                    ),
                rule = DatoRule.TOTAL_VARIGHET_OVER_ETT_AAR,
                ruleResult = varighetOver1AAr,
            )
        }

        val tilbakeDatertOver3Ar: DatoRuleFn = { payload ->
            val forsteFomDato = payload.perioder.earliestFom()
            val tilbakeDatertMerEnn3AAr = forsteFomDato.isBefore(LocalDate.now().minusYears(3))
            val genereringsTidspunkt = payload.signaturdato

            RuleOutput(
                ruleInputs =
                    mapOf(
                        "genereringsTidspunkt" to genereringsTidspunkt,
                        "fom" to forsteFomDato,
                        "tilbakeDatertMerEnn3AAr" to tilbakeDatertMerEnn3AAr,
                    ),
                rule = DatoRule.TILBAKEDATERT_MER_ENN_3_AR,
                ruleResult = tilbakeDatertMerEnn3AAr,
            )
        }
    }

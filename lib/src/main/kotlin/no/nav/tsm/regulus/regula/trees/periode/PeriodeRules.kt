package no.nav.tsm.regulus.regula.trees.periode

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import no.nav.tsm.regulus.regula.dsl.RuleOutput
import no.nav.tsm.regulus.regula.executor.TreeExecutor
import no.nav.tsm.regulus.regula.utils.earliestFom
import no.nav.tsm.regulus.regula.utils.latestTom

class PeriodeRules(periodePayload: PeriodeRulePayload) :
    TreeExecutor<PeriodeRule, PeriodeRulePayload>(periodeRuleTree, periodePayload) {
    override fun getRule(rule: PeriodeRule) = getPeriodeRule(rule)
}

fun getPeriodeRule(rules: PeriodeRule): PeriodeRuleFn {
    return when (rules) {
        PeriodeRule.FREMDATERT -> Rules.fremdatertOver30Dager
        PeriodeRule.TILBAKEDATERT_MER_ENN_3_AR -> Rules.tilbakeDatertOver3Ar
        PeriodeRule.TOTAL_VARIGHET_OVER_ETT_AAR -> Rules.varighetOver1AAr
    }
}

typealias PeriodeRuleFn = (payload: PeriodeRulePayload) -> RuleOutput<PeriodeRule>

private val Rules =
    object {

        val fremdatertOver30Dager: PeriodeRuleFn = { payload ->
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
                rule = PeriodeRule.FREMDATERT,
                ruleResult = fremdatert,
            )
        }

        val varighetOver1AAr: PeriodeRuleFn = { payload ->
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
                rule = PeriodeRule.TOTAL_VARIGHET_OVER_ETT_AAR,
                ruleResult = varighetOver1AAr,
            )
        }

        val tilbakeDatertOver3Ar: PeriodeRuleFn = { payload ->
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
                rule = PeriodeRule.TILBAKEDATERT_MER_ENN_3_AR,
                ruleResult = tilbakeDatertMerEnn3AAr,
            )
        }
    }

package no.nav.tsm.regulus.regula.rules.trees.pasientUnder13

import no.nav.tsm.regulus.regula.dsl.RuleOutput
import no.nav.tsm.regulus.regula.executor.ExecutionMode
import no.nav.tsm.regulus.regula.executor.TreeExecutor
import no.nav.tsm.regulus.regula.utils.latestTom

internal class PasientUnder13Rules(payload: PasientUnder13RulePayload, mode: ExecutionMode) :
    TreeExecutor<PasientUnder13Rule, PasientUnder13RulePayload>(
        pasientUnder13RuleTree,
        payload,
        mode,
    ) {
    override fun getRule(rule: PasientUnder13Rule): PasientUnder13RuleFn =
        getPasientUnder13Rule(rule)
}

private fun getPasientUnder13Rule(rules: PasientUnder13Rule): PasientUnder13RuleFn {
    return when (rules) {
        PasientUnder13Rule.PASIENT_YNGRE_ENN_13 -> Rules.pasientUnder13Aar
    }
}

private typealias PasientUnder13RuleFn =
    (payload: PasientUnder13RulePayload) -> RuleOutput<PasientUnder13Rule>

private val Rules =
    object {
        val pasientUnder13Aar: PasientUnder13RuleFn = { payload ->
            val sisteTomDato = payload.perioder.latestTom()
            val pasientFodselsdato = payload.pasientFodselsdato

            val pasientUnder13Aar = sisteTomDato < pasientFodselsdato.plusYears(13)

            RuleOutput(
                ruleInputs = mapOf("pasientUnder13Aar" to pasientUnder13Aar),
                rule = PasientUnder13Rule.PASIENT_YNGRE_ENN_13,
                ruleResult = pasientUnder13Aar,
            )
        }
    }

package no.nav.tsm.regulus.regula.trees.arbeidsuforhet

import no.nav.helse.diagnosekoder.Diagnosekoder
import no.nav.tsm.regulus.regula.dsl.RuleOutput
import no.nav.tsm.regulus.regula.executor.TreeExecutor

class ArbeidsuforhetRules(val payload: ArbeidsuforhetRulePayload) :
    TreeExecutor<ArbeidsuforhetRule, ArbeidsuforhetRulePayload>(arbeidsuforhetRuleTree, payload) {
    override fun getRule(rule: ArbeidsuforhetRule): ArbeidsuforhetRuleFn =
        getArbeidsuforhetRule(rule)
}

fun getArbeidsuforhetRule(rules: ArbeidsuforhetRule): ArbeidsuforhetRuleFn {
    return when (rules) {
        ArbeidsuforhetRule.ICPC_2_Z_DIAGNOSE -> Rules.icpc2ZDiagnose
        ArbeidsuforhetRule.HOVEDDIAGNOSE_MANGLER -> Rules.manglerHovedDiagnose
        ArbeidsuforhetRule.FRAVAERSGRUNN_MANGLER -> Rules.manglerAnnenFravarsArsak
        ArbeidsuforhetRule.UGYLDIG_KODEVERK_FOR_HOVEDDIAGNOSE -> Rules.ugyldigKodeVerkHovedDiagnose
        ArbeidsuforhetRule.UGYLDIG_KODEVERK_FOR_BIDIAGNOSE -> Rules.ugyldigKodeVerkBiDiagnose
    }
}

private typealias ArbeidsuforhetRuleFn =
    (payload: ArbeidsuforhetRulePayload) -> RuleOutput<ArbeidsuforhetRule>

private val Rules =
    object {
        val icpc2ZDiagnose: ArbeidsuforhetRuleFn = { payload ->
            val hoveddiagnose = payload.hoveddiagnose

            val icpc2ZDiagnose =
                hoveddiagnose != null &&
                    hoveddiagnose.isICPC2() &&
                    hoveddiagnose.kode.startsWith("Z")

            RuleOutput(
                ruleInputs = mapOf("icpc2ZDiagnose" to icpc2ZDiagnose),
                rule = ArbeidsuforhetRule.ICPC_2_Z_DIAGNOSE,
                ruleResult = icpc2ZDiagnose,
            )
        }

        val manglerHovedDiagnose: ArbeidsuforhetRuleFn = { payload ->
            val hovedDiagnoseErNull = payload.hoveddiagnose == null

            RuleOutput(
                ruleInputs = mapOf("hoveddiagnoseMangler" to hovedDiagnoseErNull),
                rule = ArbeidsuforhetRule.HOVEDDIAGNOSE_MANGLER,
                ruleResult = hovedDiagnoseErNull,
            )
        }

        val manglerAnnenFravarsArsak: ArbeidsuforhetRuleFn = { payload ->
            val annenFraversArsak = payload.annenFraversArsak

            val fraversgrunnMangler =
                (annenFraversArsak?.let { it.grunn.isEmpty() && it.beskrivelse.isNullOrBlank() }
                    ?: true)

            RuleOutput(
                ruleInputs = mapOf("fraversgrunnMangler" to fraversgrunnMangler),
                rule = ArbeidsuforhetRule.FRAVAERSGRUNN_MANGLER,
                ruleResult = fraversgrunnMangler,
            )
        }

        val ugyldigKodeVerkHovedDiagnose: ArbeidsuforhetRuleFn = { payload ->
            val hoveddiagnose = payload.hoveddiagnose

            val ugyldigKodeverkHovedDiagnose =
                (hoveddiagnose?.system !in
                    arrayOf(Diagnosekoder.ICPC2_CODE, Diagnosekoder.ICD10_CODE) ||
                    hoveddiagnose?.let { diagnose ->
                        if (diagnose.isICPC2()) {
                            Diagnosekoder.icpc2.containsKey(diagnose.kode)
                        } else {
                            Diagnosekoder.icd10.containsKey(diagnose.kode)
                        }
                    } != true)

            RuleOutput(
                ruleInputs = mapOf("ugyldigKodeverkHovedDiagnose" to ugyldigKodeverkHovedDiagnose),
                rule = ArbeidsuforhetRule.UGYLDIG_KODEVERK_FOR_HOVEDDIAGNOSE,
                ruleResult = ugyldigKodeverkHovedDiagnose,
            )
        }

        val ugyldigKodeVerkBiDiagnose: ArbeidsuforhetRuleFn = { payload ->
            val biDiagnoser = payload.bidiagnoser

            val ugyldigKodeVerkBiDiagnose =
                !biDiagnoser.all { diagnose ->
                    if (diagnose.isICPC2()) {
                        Diagnosekoder.icpc2.containsKey(diagnose.kode)
                    } else {
                        Diagnosekoder.icd10.containsKey(diagnose.kode)
                    }
                }

            RuleOutput(
                ruleInputs = mapOf("ugyldigKodeVerkBiDiagnose" to ugyldigKodeVerkBiDiagnose),
                rule = ArbeidsuforhetRule.UGYLDIG_KODEVERK_FOR_BIDIAGNOSE,
                ruleResult = ugyldigKodeVerkBiDiagnose,
            )
        }
    }

private fun Diagnose.isICPC2(): Boolean = system == Diagnosekoder.ICPC2_CODE

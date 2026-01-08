package no.nav.tsm.regulus.regula.rules.trees.arbeidsuforhet

import no.nav.tsm.diagnoser.ICD10
import no.nav.tsm.diagnoser.ICPC2
import no.nav.tsm.diagnoser.ICPC2B
import no.nav.tsm.diagnoser.toICPC2
import no.nav.tsm.regulus.regula.dsl.RuleOutput
import no.nav.tsm.regulus.regula.executor.TreeExecutor
import no.nav.tsm.regulus.regula.payload.Diagnose

internal class ArbeidsuforhetRules(val payload: ArbeidsuforhetRulePayload) :
    TreeExecutor<ArbeidsuforhetRule, ArbeidsuforhetRulePayload>(
        "Arbeidsuførhet",
        arbeidsuforhetRuleTree,
        payload,
    ) {
    override fun getRule(rule: ArbeidsuforhetRule): ArbeidsuforhetRuleFn =
        getArbeidsuforhetRule(rule)
}

private fun getArbeidsuforhetRule(rules: ArbeidsuforhetRule): ArbeidsuforhetRuleFn {
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
            val annenFravarsArsak = payload.annenFravarsArsak

            val fraversgrunnMangler =
                (annenFravarsArsak?.let { it.grunn.isEmpty() && it.beskrivelse.isNullOrBlank() }
                    ?: true)

            RuleOutput(
                ruleInputs = mapOf("fraversgrunnMangler" to fraversgrunnMangler),
                rule = ArbeidsuforhetRule.FRAVAERSGRUNN_MANGLER,
                ruleResult = fraversgrunnMangler,
            )
        }

        val ugyldigKodeVerkHovedDiagnose: ArbeidsuforhetRuleFn = { payload ->
            val hoveddiagnose = payload.hoveddiagnose
            val validDiagnoseSystemOids = arrayOf(ICPC2.OID, ICPC2B.OID, ICD10.OID)

            if (hoveddiagnose == null || hoveddiagnose.system !in validDiagnoseSystemOids) {
                RuleOutput(
                    ruleInputs = mapOf("diagnoseSystem" to (hoveddiagnose?.system ?: "Ikke satt")),
                    rule = ArbeidsuforhetRule.UGYLDIG_KODEVERK_FOR_HOVEDDIAGNOSE,
                    ruleResult = true,
                )
            } else {
                RuleOutput(
                    ruleInputs =
                        mapOf(
                            "diagnoseSystem" to (hoveddiagnose.system),
                            "diagnoseKode" to (hoveddiagnose.kode),
                        ),
                    rule = ArbeidsuforhetRule.UGYLDIG_KODEVERK_FOR_HOVEDDIAGNOSE,
                    ruleResult = !hoveddiagnose.isValidKode(),
                )
            }
        }

        val ugyldigKodeVerkBiDiagnose: ArbeidsuforhetRuleFn = { payload ->
            val biDiagnoser = payload.bidiagnoser

            val ugyldigKodeVerkBiDiagnose = !biDiagnoser.all { it.isValidKode() }

            RuleOutput(
                ruleInputs = mapOf("ugyldigKodeVerkBiDiagnose" to ugyldigKodeVerkBiDiagnose),
                rule = ArbeidsuforhetRule.UGYLDIG_KODEVERK_FOR_BIDIAGNOSE,
                ruleResult = ugyldigKodeVerkBiDiagnose,
            )
        }
    }

/** Automatically validates ICPC2B as its ICPC2-parent code. */
private fun Diagnose.isValidKode(): Boolean =
    when (this.system) {
        ICD10.OID -> ICD10[this.kode] != null
        ICPC2.OID -> ICPC2[this.kode] != null
        ICPC2B.OID -> ICPC2B[this.kode]?.toICPC2() != null
        else -> false
    }

private fun Diagnose.isICPC2(): Boolean = system == ICPC2.OID

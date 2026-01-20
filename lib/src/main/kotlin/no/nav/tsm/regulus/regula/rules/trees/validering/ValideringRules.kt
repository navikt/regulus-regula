package no.nav.tsm.regulus.regula.rules.trees.validering

import no.nav.tsm.regulus.regula.dsl.RuleOutput
import no.nav.tsm.regulus.regula.executor.TreeExecutor
import no.nav.tsm.regulus.regula.rules.shared.getStartdatoFromTidligereSykmeldinger
import no.nav.tsm.regulus.regula.utils.daysBetween
import no.nav.tsm.regulus.regula.utils.earliestFom
import no.nav.tsm.regulus.regula.utils.latestTom

internal class ValideringRules(validationRulePayload: ValideringRulePayload) :
    TreeExecutor<ValideringRule, ValideringRulePayload>(
        "Strukturell validering",
        valideringRuleTree,
        validationRulePayload,
    ) {
    override fun getRule(rule: ValideringRule) = getValideringRule(rule)
}

private fun getValideringRule(rules: ValideringRule): ValideringRuleFn =
    when (rules) {
        ValideringRule.UGYLDIG_REGELSETTVERSJON -> Rules.ugyldigRegelsettversjon
        ValideringRule.MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39 ->
            Rules.manglendeDynamiskesporsmaalversjon2uke39

        ValideringRule.UGYLDIG_ORGNR_LENGDE -> Rules.ugyldingOrgNummerLengde
        ValideringRule.AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR -> Rules.avsenderSammeSomPasient
        ValideringRule.BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR -> Rules.behandlerSammeSomPasient
        ValideringRule.PAPIRSYKMELDING -> Rules.papirsykmelding
    }

private typealias ValideringRuleFn = (payload: ValideringRulePayload) -> RuleOutput<ValideringRule>

private val Rules =
    object {
        val ugyldigRegelsettversjon: ValideringRuleFn = { payload ->
            val rulesetVersion = payload.rulesetVersion

            RuleOutput(
                ruleInputs = mapOf("rulesetVersion" to (rulesetVersion ?: "null")),
                rule = ValideringRule.UGYLDIG_REGELSETTVERSJON,
                ruleResult = rulesetVersion !in arrayOf(null, "", "1", "2", "3"),
            )
        }

        val manglendeDynamiskesporsmaalversjon2uke39: ValideringRuleFn = { payload ->
            val rulesetVersion = payload.rulesetVersion
            val sykmeldingPerioder = payload.aktivitet
            val utdypendeOpplysinger = payload.utdypendeOpplysninger
            val tidligereSykmeldinger = payload.tidligereSykmeldinger

            val tidligsteFom = sykmeldingPerioder.earliestFom()
            val sisteTom = sykmeldingPerioder.latestTom()
            val startdato =
                getStartdatoFromTidligereSykmeldinger(tidligsteFom, tidligereSykmeldinger)

            val shouldHaveAllSporsmals =
                rulesetVersion == "2" && daysBetween(startdato, sisteTom) > 273
            val manglendeDynamiskesporsmaalversjon2uke39 =
                if (shouldHaveAllSporsmals && utdypendeOpplysinger != null) {
                    val group65Answers = utdypendeOpplysinger["6.5"]?.map { it.key } ?: emptyList()

                    !group65Answers.containsAll(listOf("6.5.1", "6.5.2", "6.5.3", "6.5.4"))
                } else {
                    false
                }

            RuleOutput(
                ruleInputs =
                    mapOf(
                        "rulesetVersion" to (rulesetVersion ?: "null"),
                        "sykmeldingPerioder" to sykmeldingPerioder,
                        "utdypendeOpplysninger" to (payload.utdypendeOpplysninger ?: "null"),
                    ),
                rule = ValideringRule.MANGLENDE_DYNAMISKE_SPOERSMAL_VERSJON2_UKE_39,
                ruleResult = manglendeDynamiskesporsmaalversjon2uke39,
            )
        }

        val ugyldingOrgNummerLengde: ValideringRuleFn = { payload ->
            val legekontorOrgnr = payload.legekontorOrgnr
            val ugyldingOrgNummerLengde = legekontorOrgnr != null && legekontorOrgnr.length != 9

            RuleOutput(
                ruleInputs = mapOf("legekontorOrgnr" to (legekontorOrgnr ?: "")),
                rule = ValideringRule.UGYLDIG_ORGNR_LENGDE,
                ruleResult = ugyldingOrgNummerLengde,
            )
        }

        val avsenderSammeSomPasient: ValideringRuleFn = { payload ->
            val avsenderFnr = payload.avsenderFnr
            val pasientIdent = payload.pasientIdent

            val avsenderSammeSomPasient = avsenderFnr == pasientIdent

            RuleOutput(
                ruleInputs =
                    mapOf("avsenderFnr" to (avsenderFnr ?: "null"), "pasientIdent" to pasientIdent),
                rule = ValideringRule.AVSENDER_FNR_ER_SAMME_SOM_PASIENT_FNR,
                ruleResult = avsenderSammeSomPasient,
            )
        }

        val behandlerSammeSomPasient: ValideringRuleFn = { payload ->
            val behandlerFnr = payload.behandlerFnr
            val pasientFodselsNummer = payload.pasientIdent

            val behandlerSammeSomPasient = behandlerFnr == pasientFodselsNummer

            RuleOutput(
                ruleInputs =
                    mapOf("behandlerFnr" to behandlerFnr, "pasientIdent" to pasientFodselsNummer),
                rule = ValideringRule.BEHANDLER_FNR_ER_SAMME_SOM_PASIENT_FNR,
                ruleResult = behandlerSammeSomPasient,
            )
        }

        val papirsykmelding: ValideringRuleFn = { payload ->
            val papirsykmelding = payload.papirsykmelding

            RuleOutput(
                ruleInputs = mapOf("papirsykmelding" to papirsykmelding),
                rule = ValideringRule.PAPIRSYKMELDING,
                ruleResult = papirsykmelding,
            )
        }
    }

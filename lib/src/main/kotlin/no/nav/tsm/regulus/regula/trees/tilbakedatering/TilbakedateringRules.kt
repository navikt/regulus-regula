package no.nav.tsm.regulus.regula.trees.tilbakedatering

import java.time.temporal.ChronoUnit
import no.nav.helse.diagnosekoder.Diagnosekoder
import no.nav.tsm.regulus.regula.dsl.RuleOutput
import no.nav.tsm.regulus.regula.executor.TreeExecutor
import no.nav.tsm.regulus.regula.trees.tilbakedatering.extras.Forlengelse
import no.nav.tsm.regulus.regula.trees.tilbakedatering.extras.isEttersending
import no.nav.tsm.regulus.regula.trees.tilbakedatering.extras.isForlengelse
import no.nav.tsm.regulus.regula.utils.earliestFom
import no.nav.tsm.regulus.regula.utils.latestTom

class TilbakedateringRules(payload: TilbakedateringRulePayload) :
    TreeExecutor<TilbakedateringRule, TilbakedateringRulePayload>(
        tilbakedateringRuleTree,
        payload,
    ) {
    override fun getRule(rule: TilbakedateringRule): TilbakedateringRuleFn =
        getTilbakedateringRule(rule)
}

private fun getTilbakedateringRule(rules: TilbakedateringRule): TilbakedateringRuleFn {
    return when (rules) {
        TilbakedateringRule.ARBEIDSGIVERPERIODE -> Rules.arbeidsgiverperiode
        TilbakedateringRule.BEGRUNNELSE_MIN_1_ORD -> Rules.begrunnelse_min_1_ord
        TilbakedateringRule.BEGRUNNELSE_MIN_3_ORD -> Rules.begrunnelse_min_3_ord
        TilbakedateringRule.ETTERSENDING -> Rules.ettersending
        TilbakedateringRule.FORLENGELSE -> Rules.forlengelse
        TilbakedateringRule.SPESIALISTHELSETJENESTEN -> Rules.spesialisthelsetjenesten
        TilbakedateringRule.TILBAKEDATERING -> Rules.tilbakedatering
        TilbakedateringRule.TILBAKEDATERT_INNTIL_4_DAGER -> Rules.tilbakedateringInntil4Dager
        TilbakedateringRule.TILBAKEDATERT_INNTIL_8_DAGER -> Rules.tilbakedateringInntil8Dager
        TilbakedateringRule.TILBAKEDATERT_MINDRE_ENN_1_MAANED ->
            Rules.tilbakedateringMindreEnn1Maaned
    }
}

private typealias TilbakedateringRuleFn =
    (payload: TilbakedateringRulePayload) -> RuleOutput<TilbakedateringRule>

private val Rules =
    object {
        val tilbakedatering: TilbakedateringRuleFn = { payload ->
            val fom = payload.perioder.earliestFom()
            val genereringstidspunkt = payload.signaturdato.toLocalDate()

            RuleOutput(
                ruleInputs = mapOf("fom" to fom, "genereringstidspunkt" to genereringstidspunkt),
                rule = TilbakedateringRule.TILBAKEDATERING,
                ruleResult = genereringstidspunkt.isAfter(fom),
            )
        }

        val tilbakedateringInntil4Dager: TilbakedateringRuleFn = { payload ->
            val fom = payload.perioder.earliestFom()
            val genereringstidspunkt = payload.signaturdato.toLocalDate()
            val daysBetween = ChronoUnit.DAYS.between(fom, genereringstidspunkt)

            RuleOutput(
                ruleInputs = mapOf("fom" to fom, "genereringstidspunkt" to genereringstidspunkt),
                rule = TilbakedateringRule.TILBAKEDATERT_INNTIL_4_DAGER,
                ruleResult = daysBetween <= 4,
            )
        }

        val tilbakedateringMindreEnn1Maaned: TilbakedateringRuleFn = { payload ->
            val fom = payload.perioder.earliestFom()
            val genereringstidspunkt = payload.signaturdato.toLocalDate()
            RuleOutput(
                ruleInputs = mapOf("fom" to fom, "genereringstidspunkt" to genereringstidspunkt),
                rule = TilbakedateringRule.TILBAKEDATERT_MINDRE_ENN_1_MAANED,
                ruleResult = genereringstidspunkt.isBefore(fom.plusMonths(1).plusDays(1)),
            )
        }

        val tilbakedateringInntil8Dager: TilbakedateringRuleFn = { payload ->
            val fom = payload.perioder.earliestFom()
            val genereringstidspunkt = payload.signaturdato.toLocalDate()
            RuleOutput(
                ruleInputs = mapOf("fom" to fom, "genereringstidspunkt" to genereringstidspunkt),
                rule = TilbakedateringRule.TILBAKEDATERT_INNTIL_8_DAGER,
                ruleResult = genereringstidspunkt.isBefore(fom.plusDays(9)),
            )
        }

        val arbeidsgiverperiode: TilbakedateringRuleFn = { payload ->
            val tom = payload.perioder.latestTom()
            val dager = payload.dagerForArbeidsgiverperiodeCheck
            val arbeidsgiverperiodeNy = dager.size < 17

            RuleOutput(
                ruleInputs =
                    mapOf(
                        "tom" to tom,
                        "dagerForArbeidsgiverperiode" to dager.sorted(),
                        "arbeidsgiverperiode" to arbeidsgiverperiodeNy,
                    ),
                rule = TilbakedateringRule.ARBEIDSGIVERPERIODE,
                ruleResult = arbeidsgiverperiodeNy,
            )
        }

        val begrunnelse_min_1_ord: TilbakedateringRuleFn = { payload ->
            val begrunnelse = payload.begrunnelseIkkeKontakt ?: ""
            val wordCount = getNumberOfWords(begrunnelse)
            val result = wordCount >= 1
            RuleOutput(
                ruleInputs = mapOf("begrunnelse" to "$wordCount ord"),
                rule = TilbakedateringRule.BEGRUNNELSE_MIN_1_ORD,
                ruleResult = result,
            )
        }

        val begrunnelse_min_3_ord: TilbakedateringRuleFn = { payload ->
            val begrunnelse = payload.begrunnelseIkkeKontakt ?: ""
            val wordCount = getNumberOfWords(begrunnelse)
            val result = wordCount >= 3
            RuleOutput(
                ruleInputs = mapOf("begrunnelse" to "$wordCount ord"),
                rule = TilbakedateringRule.BEGRUNNELSE_MIN_3_ORD,
                ruleResult = result,
            )
        }

        val ettersending: TilbakedateringRuleFn = { payload ->
            val ettersendingAv =
                isEttersending(
                    sykmeldingId = payload.sykmeldingId,
                    perioder = payload.perioder,
                    harMedisinskVurdering = payload.hoveddiagnose != null,
                    tidligereSykmeldinger = payload.tidligereSykmeldinger,
                )

            val result = ettersendingAv != null
            val ruleInputs = mutableMapOf<String, Any>()
            if (ettersendingAv != null) {
                ruleInputs["ettersending"] = ettersendingAv
            }
            RuleOutput(
                ruleInputs = ruleInputs,
                rule = TilbakedateringRule.ETTERSENDING,
                ruleResult = result,
            )
        }

        val forlengelse: TilbakedateringRuleFn = { payload ->
            val forlengelseAv: Forlengelse? =
                isForlengelse(
                        perioder = payload.perioder,
                        hoveddiagnose = payload.hoveddiagnose,
                        tidligereSykmeldinger = payload.tidligereSykmeldinger,
                    )
                    .firstOrNull()

            val ruleInputs = mutableMapOf<String, Any>()
            if (forlengelseAv != null) {
                ruleInputs["forlengelse"] = forlengelseAv
            }

            val result = forlengelseAv != null
            RuleOutput(
                ruleInputs = ruleInputs,
                rule = TilbakedateringRule.FORLENGELSE,
                ruleResult = result,
            )
        }

        val spesialisthelsetjenesten: TilbakedateringRuleFn = { payload ->
            val spesialhelsetjenesten = payload.hoveddiagnose?.system === Diagnosekoder.ICD10_CODE

            RuleOutput(
                ruleInputs =
                    mapOf(
                        "diagnosesystem" to (payload.hoveddiagnose?.system ?: ""),
                        "spesialisthelsetjenesten" to spesialhelsetjenesten,
                    ),
                rule = TilbakedateringRule.SPESIALISTHELSETJENESTEN,
                ruleResult = spesialhelsetjenesten,
            )
        }
    }

fun getNumberOfWords(input: String?): Int {
    return input?.trim()?.split(" ")?.filter { containsLetters(it) }?.size ?: 0
}

fun containsLetters(text: String): Boolean {
    return text.contains("""[A-Za-z]""".toRegex())
}

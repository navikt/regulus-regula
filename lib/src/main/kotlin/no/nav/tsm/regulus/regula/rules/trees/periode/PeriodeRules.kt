package no.nav.tsm.regulus.regula.rules.trees.periode

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import no.nav.tsm.regulus.regula.dsl.RuleOutput
import no.nav.tsm.regulus.regula.executor.TreeExecutor
import no.nav.tsm.regulus.regula.payload.Aktivitet
import no.nav.tsm.regulus.regula.payload.SykmeldingPeriodeType
import no.nav.tsm.regulus.regula.utils.daysBetween
import no.nav.tsm.regulus.regula.utils.workdaysBetween

internal class PeriodeRules(payload: PeriodeRulePayload) :
    TreeExecutor<PeriodeRule, PeriodeRulePayload>(periodeRuleTree, payload) {
    override fun getRule(rule: PeriodeRule): (PeriodeRulePayload) -> RuleOutput<PeriodeRule> =
        getPeriodeRule(rule)
}

private fun getPeriodeRule(rules: PeriodeRule): PeriodeRuleFn {
    return when (rules) {
        PeriodeRule.PERIODER_MANGLER -> Rules.periodeMangler
        PeriodeRule.FRADATO_ETTER_TILDATO -> Rules.fraDatoEtterTilDato
        PeriodeRule.OVERLAPPENDE_PERIODER -> Rules.overlappendePerioder
        PeriodeRule.OPPHOLD_MELLOM_PERIODER -> Rules.oppholdMellomPerioder
        PeriodeRule.IKKE_DEFINERT_PERIODE -> Rules.ikkeDefinertPeriode
        PeriodeRule.BEHANDLINGSDATO_ETTER_MOTTATTDATO -> Rules.behandslingsDatoEtterMottatDato
        PeriodeRule.AVVENTENDE_SYKMELDING_KOMBINERT -> Rules.avventendeKombinert
        PeriodeRule.MANGLENDE_INNSPILL_TIL_ARBEIDSGIVER -> Rules.manglendeInnspillArbeidsgiver
        PeriodeRule.AVVENTENDE_SYKMELDING_OVER_16_DAGER -> Rules.avventendeOver16Dager
        PeriodeRule.FOR_MANGE_BEHANDLINGSDAGER_PER_UKE -> Rules.forMangeBehandlingsDagerPrUke
        PeriodeRule.GRADERT_SYKMELDING_OVER_99_PROSENT -> Rules.gradertOver99Prosent
        PeriodeRule.GRADERT_SYKMELDING_0_PROSENT -> Rules.gradert0Prosent
        PeriodeRule.SYKMELDING_MED_BEHANDLINGSDAGER -> Rules.inneholderBehandlingsDager
    }
}

private typealias PeriodeRuleFn = (payload: PeriodeRulePayload) -> RuleOutput<PeriodeRule>

private val Rules =
    object {
        val periodeMangler: PeriodeRuleFn = { payload ->
            val perioder = payload.aktivitet
            val periodeMangler = perioder.isEmpty()

            RuleOutput(
                ruleInputs = mapOf("perioder" to perioder),
                rule = PeriodeRule.PERIODER_MANGLER,
                ruleResult = periodeMangler,
            )
        }

        val fraDatoEtterTilDato: PeriodeRuleFn = { payload ->
            val perioder = payload.aktivitet

            val fraDatoEtterTilDato = perioder.any { it.fom.isAfter(it.tom) }

            RuleOutput(
                ruleInputs = mapOf("perioder" to perioder),
                rule = PeriodeRule.FRADATO_ETTER_TILDATO,
                ruleResult = fraDatoEtterTilDato,
            )
        }

        val overlappendePerioder: PeriodeRuleFn = { payload ->
            val perioder = payload.aktivitet

            val overlappendePerioder =
                perioder.any { periodA ->
                    perioder
                        .filter { periodB -> periodB != periodA }
                        .any { periodB -> periodA.fom in periodB || periodA.tom in periodB }
                }

            RuleOutput(
                ruleInputs = mapOf("perioder" to perioder),
                rule = PeriodeRule.OVERLAPPENDE_PERIODER,
                ruleResult = overlappendePerioder,
            )
        }

        val oppholdMellomPerioder: PeriodeRuleFn = { payload ->
            val periodeRanges = payload.aktivitet.sortedBy { it.fom }.map { it.fom to it.tom }

            var oppholdMellomPerioder = false
            for (i in 1 until periodeRanges.size) {
                oppholdMellomPerioder =
                    workdaysBetween(periodeRanges[i - 1].second, periodeRanges[i].first) > 0
                if (oppholdMellomPerioder == true) {
                    break
                }
            }

            RuleOutput(
                ruleInputs = mapOf("perioder" to payload.aktivitet),
                rule = PeriodeRule.OPPHOLD_MELLOM_PERIODER,
                ruleResult = oppholdMellomPerioder,
            )
        }

        val ikkeDefinertPeriode: PeriodeRuleFn = { payload ->
            val perioder = payload.aktivitet
            val ikkeDefinertPeriode = perioder.any { it.type == SykmeldingPeriodeType.INVALID }

            RuleOutput(
                ruleInputs = mapOf("perioder" to perioder),
                rule = PeriodeRule.IKKE_DEFINERT_PERIODE,
                ruleResult = ikkeDefinertPeriode,
            )
        }

        val behandslingsDatoEtterMottatDato: PeriodeRuleFn = { payload ->
            val behandletTidspunkt = payload.behandletTidspunkt
            val receivedDate = payload.mottattDato

            val behandslingsDatoEtterMottatDato = behandletTidspunkt > receivedDate.plusDays(1)

            RuleOutput(
                ruleInputs =
                    mapOf("behandslingsDatoEtterMottatDato" to behandslingsDatoEtterMottatDato),
                rule = PeriodeRule.BEHANDLINGSDATO_ETTER_MOTTATTDATO,
                ruleResult = behandslingsDatoEtterMottatDato,
            )
        }

        val avventendeKombinert: PeriodeRuleFn = { payload ->
            val perioder = payload.aktivitet

            val antallAvventende = perioder.count { it.type == SykmeldingPeriodeType.AVVENTENDE }
            val avventendeKombinert = antallAvventende != 0 && perioder.size > 1

            RuleOutput(
                ruleInputs =
                    mapOf(
                        "antallAvventende" to antallAvventende,
                        "antallPerioder" to perioder.size,
                    ),
                rule = PeriodeRule.AVVENTENDE_SYKMELDING_KOMBINERT,
                ruleResult = avventendeKombinert,
            )
        }

        val manglendeInnspillArbeidsgiver: PeriodeRuleFn = { payload ->
            val perioder = payload.aktivitet
            val manglendeInnspillArbeidsgiver =
                perioder.any {
                    it is Aktivitet.Avventende &&
                        it.avventendeInnspillTilArbeidsgiver?.trim().isNullOrEmpty()
                }

            RuleOutput(
                ruleInputs =
                    mapOf(
                        // TODO: Dårlig etterlevelse, samme som output
                        "manglendeInnspillArbeidsgiver" to manglendeInnspillArbeidsgiver
                    ),
                rule = PeriodeRule.MANGLENDE_INNSPILL_TIL_ARBEIDSGIVER,
                ruleResult = manglendeInnspillArbeidsgiver,
            )
        }

        val avventendeOver16Dager: PeriodeRuleFn = { payload ->
            val perioder = payload.aktivitet

            val avventendeOver16Dager =
                perioder
                    .filter { it.type == SykmeldingPeriodeType.AVVENTENDE }
                    .any { (daysBetween(it.fom, it.tom)) > 16 }

            RuleOutput(
                ruleInputs = mapOf("avventendeOver16Dager" to avventendeOver16Dager),
                rule = PeriodeRule.AVVENTENDE_SYKMELDING_OVER_16_DAGER,
                ruleResult = avventendeOver16Dager,
            )
        }

        val forMangeBehandlingsDagerPrUke: PeriodeRuleFn = { payload ->
            val perioder = payload.aktivitet

            val forMangeBehandlingsDagerPrUke =
                perioder.any {
                    it is Aktivitet.Behandlingsdager &&
                        it.behandlingsdager > it.startedWeeksBetween()
                }

            RuleOutput(
                ruleInputs =
                    mapOf("forMangeBehandlingsDagerPrUke" to forMangeBehandlingsDagerPrUke),
                rule = PeriodeRule.FOR_MANGE_BEHANDLINGSDAGER_PER_UKE,
                ruleResult = forMangeBehandlingsDagerPrUke,
            )
        }

        val gradertOver99Prosent: PeriodeRuleFn = { payload ->
            val perioder = payload.aktivitet

            val gradertOver99Prosent = perioder.any { it is Aktivitet.Gradert && it.grad > 99 }

            RuleOutput(
                ruleInputs =
                    mapOf(
                        // TODO: Dårlig etterlevelse
                        "gradertOver99Prosent" to gradertOver99Prosent
                    ),
                rule = PeriodeRule.GRADERT_SYKMELDING_OVER_99_PROSENT,
                ruleResult = gradertOver99Prosent,
            )
        }

        val gradert0Prosent: PeriodeRuleFn = { payload ->
            val gradertePerioder = payload.aktivitet.filterIsInstance<Aktivitet.Gradert>()
            val gradert0Prosent = gradertePerioder.any { it.grad == 0 }

            RuleOutput(
                ruleInputs = mapOf("gradertePerioder" to gradertePerioder),
                rule = PeriodeRule.GRADERT_SYKMELDING_0_PROSENT,
                ruleResult = gradert0Prosent,
            )
        }

        val inneholderBehandlingsDager: PeriodeRuleFn = { payload ->
            val perioder = payload.aktivitet

            val inneholderBehandlingsDager =
                perioder.any { it.type == SykmeldingPeriodeType.BEHANDLINGSDAGER }

            RuleOutput(
                ruleInputs = mapOf("inneholderBehandlingsDager" to inneholderBehandlingsDager),
                rule = PeriodeRule.SYKMELDING_MED_BEHANDLINGSDAGER,
                ruleResult = inneholderBehandlingsDager,
            )
        }
    }

private fun ClosedRange<LocalDate>.startedWeeksBetween(): Int =
    ChronoUnit.WEEKS.between(start, endInclusive).toInt() + 1

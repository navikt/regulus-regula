package no.nav.tsm.regulus.regula.trees.periodvalidering

import no.nav.tsm.regulus.regula.dsl.tree
import no.nav.tsm.regulus.regula.executor.RuleStatus.*
import no.nav.tsm.regulus.regula.executor.UtenJuridisk
import no.nav.tsm.regulus.regula.executor.no
import no.nav.tsm.regulus.regula.executor.yes

val periodLogicRuleTree =
    tree<PeriodLogicRule>(PeriodLogicRule.PERIODER_MANGLER) {
        yes(INVALID, PeriodLogicRule.Outcomes.PERIODER_MANGLER)
        no(PeriodLogicRule.FRADATO_ETTER_TILDATO) {
            yes(INVALID, PeriodLogicRule.Outcomes.FRADATO_ETTER_TILDATO)
            no(PeriodLogicRule.OVERLAPPENDE_PERIODER) {
                yes(INVALID, PeriodLogicRule.Outcomes.OVERLAPPENDE_PERIODER)
                no(PeriodLogicRule.OPPHOLD_MELLOM_PERIODER) {
                    yes(INVALID, PeriodLogicRule.Outcomes.OPPHOLD_MELLOM_PERIODER)
                    no(PeriodLogicRule.IKKE_DEFINERT_PERIODE) {
                        yes(INVALID, PeriodLogicRule.Outcomes.IKKE_DEFINERT_PERIODE)
                        no(PeriodLogicRule.BEHANDLINGSDATO_ETTER_MOTTATTDATO) {
                            yes(INVALID, PeriodLogicRule.Outcomes.BEHANDLINGSDATO_ETTER_MOTTATTDATO)
                            no(PeriodLogicRule.AVVENTENDE_SYKMELDING_KOMBINERT) {
                                yes(
                                    INVALID,
                                    PeriodLogicRule.Outcomes.AVVENTENDE_SYKMELDING_KOMBINERT,
                                )
                                no(PeriodLogicRule.MANGLENDE_INNSPILL_TIL_ARBEIDSGIVER) {
                                    yes(
                                        INVALID,
                                        PeriodLogicRule.Outcomes.MANGLENDE_INNSPILL_TIL_ARBEIDSGIVER,
                                    )
                                    no(PeriodLogicRule.AVVENTENDE_SYKMELDING_OVER_16_DAGER) {
                                        yes(
                                            INVALID,
                                            PeriodLogicRule.Outcomes
                                                .AVVENTENDE_SYKMELDING_OVER_16_DAGER,
                                        )
                                        no(PeriodLogicRule.FOR_MANGE_BEHANDLINGSDAGER_PER_UKE) {
                                            yes(
                                                INVALID,
                                                PeriodLogicRule.Outcomes
                                                    .FOR_MANGE_BEHANDLINGSDAGER_PER_UKE,
                                            )
                                            no(PeriodLogicRule.GRADERT_SYKMELDING_OVER_99_PROSENT) {
                                                yes(
                                                    INVALID,
                                                    PeriodLogicRule.Outcomes
                                                        .GRADERT_SYKMELDING_OVER_99_PROSENT,
                                                )
                                                no(
                                                    PeriodLogicRule.SYKMELDING_MED_BEHANDLINGSDAGER
                                                ) {
                                                    yes(
                                                        MANUAL_PROCESSING,
                                                        PeriodLogicRule.Outcomes
                                                            .SYKMELDING_MED_BEHANDLINGSDAGER,
                                                    )
                                                    no(OK)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } to UtenJuridisk

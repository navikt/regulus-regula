package no.nav.tsm.regulus.regula.rules.trees.periode

import no.nav.tsm.regulus.regula.dsl.RuleJuridisk
import no.nav.tsm.regulus.regula.dsl.RuleOutcome
import no.nav.tsm.regulus.regula.dsl.TreeNode.LeafNode
import no.nav.tsm.regulus.regula.dsl.tree

internal val periodeRuleTree =
    tree(PeriodeRule.PERIODER_MANGLER) {
        yes(INVALID(PeriodeRule.Outcomes.PERIODER_MANGLER))
        no(PeriodeRule.FRADATO_ETTER_TILDATO) {
            yes(INVALID(PeriodeRule.Outcomes.FRADATO_ETTER_TILDATO))
            no(PeriodeRule.OVERLAPPENDE_PERIODER) {
                yes(INVALID(PeriodeRule.Outcomes.OVERLAPPENDE_PERIODER))
                no(PeriodeRule.OPPHOLD_MELLOM_PERIODER) {
                    yes(INVALID(PeriodeRule.Outcomes.OPPHOLD_MELLOM_PERIODER))
                    no(PeriodeRule.IKKE_DEFINERT_PERIODE) {
                        yes(INVALID(PeriodeRule.Outcomes.IKKE_DEFINERT_PERIODE))
                        no(PeriodeRule.BEHANDLINGSDATO_ETTER_MOTTATTDATO) {
                            yes(INVALID(PeriodeRule.Outcomes.BEHANDLINGSDATO_ETTER_MOTTATTDATO))
                            no(PeriodeRule.AVVENTENDE_SYKMELDING_KOMBINERT) {
                                yes(INVALID(PeriodeRule.Outcomes.AVVENTENDE_SYKMELDING_KOMBINERT))
                                no(PeriodeRule.MANGLENDE_INNSPILL_TIL_ARBEIDSGIVER) {
                                    yes(
                                        INVALID(
                                            PeriodeRule.Outcomes.MANGLENDE_INNSPILL_TIL_ARBEIDSGIVER
                                        )
                                    )
                                    no(PeriodeRule.AVVENTENDE_SYKMELDING_OVER_16_DAGER) {
                                        yes(
                                            INVALID(
                                                PeriodeRule.Outcomes
                                                    .AVVENTENDE_SYKMELDING_OVER_16_DAGER
                                            )
                                        )
                                        no(PeriodeRule.FOR_MANGE_BEHANDLINGSDAGER_PER_UKE) {
                                            yes(
                                                INVALID(
                                                    PeriodeRule.Outcomes
                                                        .FOR_MANGE_BEHANDLINGSDAGER_PER_UKE
                                                )
                                            )
                                            no(PeriodeRule.GRADERT_SYKMELDING_OVER_99_PROSENT) {
                                                yes(
                                                    INVALID(
                                                        PeriodeRule.Outcomes
                                                            .GRADERT_SYKMELDING_OVER_99_PROSENT
                                                    )
                                                )
                                                no(PeriodeRule.GRADERT_SYKMELDING_0_PROSENT) {
                                                    yes(
                                                        INVALID(
                                                            PeriodeRule.Outcomes
                                                                .GRADERT_SYKMELDING_0_PROSENT
                                                        )
                                                    )
                                                    no(
                                                        PeriodeRule.SYKMELDING_MED_BEHANDLINGSDAGER
                                                    ) {
                                                        yes(
                                                            MANUAL(
                                                                PeriodeRule.Outcomes
                                                                    .SYKMELDING_MED_BEHANDLINGSDAGER
                                                            )
                                                        )
                                                        no(OK())
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
        }
    }

private fun INVALID(outcome: RuleOutcome): LeafNode.INVALID<PeriodeRule> =
    LeafNode.INVALID(outcome, RuleJuridisk.INGEN)

private fun MANUAL(outcome: RuleOutcome): LeafNode.MANUAL<PeriodeRule> =
    LeafNode.MANUAL(outcome, RuleJuridisk.INGEN)

private fun OK(): LeafNode.OK<PeriodeRule> = LeafNode.OK(RuleJuridisk.INGEN)

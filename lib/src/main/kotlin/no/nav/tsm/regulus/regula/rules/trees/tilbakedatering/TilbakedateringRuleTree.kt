package no.nav.tsm.regulus.regula.rules.trees.tilbakedatering

import no.nav.tsm.regulus.regula.dsl.RuleJuridisk
import no.nav.tsm.regulus.regula.dsl.TreeNode.LeafNode.*
import no.nav.tsm.regulus.regula.dsl.tree

internal val tilbakedateringRuleTree =
    tree(TilbakedateringRule.TILBAKEDATERING) {
        yes(TilbakedateringRule.SPESIALISTHELSETJENESTEN) {
            yes(OK(RuleJuridisk.FOLKETRYGDLOVEN_8_7_1_1))
            no(TilbakedateringRule.ETTERSENDING) {
                yes(OK(RuleJuridisk.FOLKETRYGDLOVEN_8_7_1_1))
                no(TilbakedateringRule.TILBAKEDATERT_INNTIL_4_DAGER) {
                    yes(OK(RuleJuridisk.FOLKETRYGDLOVEN_8_7_2_2))
                    no(TilbakedateringRule.TILBAKEDATERT_INNTIL_8_DAGER) {
                        yes(TilbakedateringRule.BEGRUNNELSE_MIN_1_ORD) {
                            yes(OK(RuleJuridisk.FOLKETRYGDLOVEN_8_7_2_2))
                            no(TilbakedateringRule.FORLENGELSE) {
                                yes(OK(RuleJuridisk.FOLKETRYGDLOVEN_8_7_1_1))
                                no(
                                    INVALID(
                                        TilbakedateringRule.Outcomes.INNTIL_8_DAGER,
                                        RuleJuridisk.FOLKETRYGDLOVEN_8_7_1_1,
                                    )
                                )
                            }
                        }
                        no(TilbakedateringRule.TILBAKEDATERT_MINDRE_ENN_1_MAANED) {
                            yes(TilbakedateringRule.BEGRUNNELSE_MIN_1_ORD) {
                                yes(TilbakedateringRule.FORLENGELSE) {
                                    yes(OK(RuleJuridisk.FOLKETRYGDLOVEN_8_7_1_1))
                                    no(TilbakedateringRule.ARBEIDSGIVERPERIODE) {
                                        yes(OK(RuleJuridisk.FOLKETRYGDLOVEN_8_7_2_2))
                                        no(
                                            MANUAL(
                                                TilbakedateringRule.Outcomes
                                                    .MINDRE_ENN_1_MAANED_MED_BEGRUNNELSE,
                                                RuleJuridisk.FOLKETRYGDLOVEN_8_7,
                                            )
                                        )
                                    }
                                }
                                no(
                                    INVALID(
                                        TilbakedateringRule.Outcomes.MINDRE_ENN_1_MAANED,
                                        RuleJuridisk.FOLKETRYGDLOVEN_8_7_1_1,
                                    )
                                )
                            }
                            no(TilbakedateringRule.BEGRUNNELSE_MIN_3_ORD) {
                                yes(
                                    MANUAL(
                                        TilbakedateringRule.Outcomes.OVER_1_MND_MED_BEGRUNNELSE,
                                        RuleJuridisk.FOLKETRYGDLOVEN_8_7,
                                    )
                                )
                                no(
                                    INVALID(
                                        TilbakedateringRule.Outcomes.OVER_1_MND,
                                        RuleJuridisk.FOLKETRYGDLOVEN_8_7,
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
        no(OK(RuleJuridisk.FOLKETRYGDLOVEN_8_7_1_1))
    }

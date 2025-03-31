package no.nav.tsm.regulus.regula.rules.trees.tilbakedatering

import no.nav.tsm.regulus.regula.dsl.RuleStatus.*
import no.nav.tsm.regulus.regula.dsl.no
import no.nav.tsm.regulus.regula.dsl.tree
import no.nav.tsm.regulus.regula.dsl.yes
import no.nav.tsm.regulus.regula.juridisk.JuridiskHenvisning
import no.nav.tsm.regulus.regula.juridisk.Lovverk
import no.nav.tsm.regulus.regula.juridisk.MedJuridisk

internal val tilbakedateringRuleTree =
    tree<TilbakedateringRule>(TilbakedateringRule.TILBAKEDATERING) {
        yes(TilbakedateringRule.ETTERSENDING) {
            yes(OK)
            no(TilbakedateringRule.TILBAKEDATERT_INNTIL_4_DAGER) {
                yes(OK)
                no(TilbakedateringRule.TILBAKEDATERT_INNTIL_8_DAGER) {
                    yes(TilbakedateringRule.BEGRUNNELSE_MIN_1_ORD) {
                        yes(OK)
                        no(TilbakedateringRule.FORLENGELSE) {
                            yes(OK)
                            no(TilbakedateringRule.SPESIALISTHELSETJENESTEN) {
                                yes(OK)
                                no(INVALID, TilbakedateringRule.Outcomes.INNTIL_8_DAGER)
                            }
                        }
                    }
                    no(TilbakedateringRule.TILBAKEDATERT_MINDRE_ENN_1_MAANED) {
                        yes(TilbakedateringRule.BEGRUNNELSE_MIN_1_ORD) {
                            yes(TilbakedateringRule.FORLENGELSE) {
                                yes(OK)
                                no(TilbakedateringRule.ARBEIDSGIVERPERIODE) {
                                    yes(OK)
                                    no(TilbakedateringRule.SPESIALISTHELSETJENESTEN) {
                                        yes(OK)
                                        no(
                                            MANUAL_PROCESSING,
                                            TilbakedateringRule.Outcomes
                                                .MINDRE_ENN_1_MAANED_MED_BEGRUNNELSE,
                                        )
                                    }
                                }
                            }
                            no(TilbakedateringRule.SPESIALISTHELSETJENESTEN) {
                                yes(OK)
                                no(INVALID, TilbakedateringRule.Outcomes.MINDRE_ENN_1_MAANED)
                            }
                        }
                        no(TilbakedateringRule.BEGRUNNELSE_MIN_3_ORD) {
                            yes(
                                MANUAL_PROCESSING,
                                TilbakedateringRule.Outcomes.OVER_1_MND_MED_BEGRUNNELSE,
                            )
                            no(TilbakedateringRule.SPESIALISTHELSETJENESTEN) {
                                yes(
                                    MANUAL_PROCESSING,
                                    TilbakedateringRule.Outcomes.OVER_1_MND_SPESIALISTHELSETJENESTEN,
                                )
                                no(INVALID, TilbakedateringRule.Outcomes.OVER_1_MND)
                            }
                        }
                    }
                }
            }
        }
        no(OK)
    } to
        MedJuridisk(
            JuridiskHenvisning(
                lovverk = Lovverk.FOLKETRYGDLOVEN,
                paragraf = "8-7",
                ledd = 2,
                punktum = null,
                bokstav = null,
            )
        )

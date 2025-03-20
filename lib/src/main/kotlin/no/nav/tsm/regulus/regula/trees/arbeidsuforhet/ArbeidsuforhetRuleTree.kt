package no.nav.tsm.regulus.regula.trees.arbeidsuforhet

import no.nav.tsm.regulus.regula.dsl.tree
import no.nav.tsm.regulus.regula.executor.MedJuridisk
import no.nav.tsm.regulus.regula.executor.RuleStatus.INVALID
import no.nav.tsm.regulus.regula.executor.RuleStatus.OK
import no.nav.tsm.regulus.regula.executor.no
import no.nav.tsm.regulus.regula.executor.yes
import no.nav.tsm.regulus.regula.juridisk.JuridiskHenvisning
import no.nav.tsm.regulus.regula.juridisk.Lovverk

val arbeidsuforhetRuleTree =
    tree<ArbeidsuforhetRule>(ArbeidsuforhetRule.HOVEDDIAGNOSE_MANGLER) {
        yes(ArbeidsuforhetRule.FRAVAERSGRUNN_MANGLER) {
            yes(INVALID, ArbeidsuforhetRule.Outcomes.FRAVAERSGRUNN_MANGLER)
            no(ArbeidsuforhetRule.UGYLDIG_KODEVERK_FOR_BIDIAGNOSE) {
                yes(INVALID, ArbeidsuforhetRule.Outcomes.UGYLDIG_KODEVERK_FOR_BIDIAGNOSE)
                no(OK)
            }
        }
        no(ArbeidsuforhetRule.UGYLDIG_KODEVERK_FOR_HOVEDDIAGNOSE) {
            yes(INVALID, ArbeidsuforhetRule.Outcomes.UGYLDIG_KODEVERK_FOR_HOVEDDIAGNOSE)
            no(ArbeidsuforhetRule.ICPC_2_Z_DIAGNOSE) {
                yes(INVALID, ArbeidsuforhetRule.Outcomes.ICPC_2_Z_DIAGNOSE)
                no(ArbeidsuforhetRule.UGYLDIG_KODEVERK_FOR_BIDIAGNOSE) {
                    yes(INVALID, ArbeidsuforhetRule.Outcomes.UGYLDIG_KODEVERK_FOR_BIDIAGNOSE)
                    no(OK)
                }
            }
        }
    } to
        MedJuridisk(
            JuridiskHenvisning(
                lovverk = Lovverk.FOLKETRYGDLOVEN,
                paragraf = "8-4",
                ledd = 1,
                punktum = null,
                bokstav = null,
            )
        )

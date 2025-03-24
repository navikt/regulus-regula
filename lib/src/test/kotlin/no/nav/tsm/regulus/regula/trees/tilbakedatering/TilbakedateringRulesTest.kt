package no.nav.tsm.regulus.regula.trees.tilbakedatering

import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import no.nav.helse.diagnosekoder.Diagnosekoder
import no.nav.tsm.regulus.regula.executor.RuleStatus
import no.nav.tsm.regulus.regula.payload.FomTom
import no.nav.tsm.regulus.regula.trees.assertPath
import no.nav.tsm.regulus.regula.trees.debugPath
import no.nav.tsm.regulus.regula.trees.tilbakedatering.TilbakedateringRule.*
import no.nav.tsm.regulus.regula.utils.allDaysBetween
import no.nav.tsm.regulus.regula.utils.earliestFom
import no.nav.tsm.regulus.regula.utils.latestTom
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

class TilbakedateringRulesTest {

    @Nested
    @DisplayName("Test tilbakedateringsregler mindre enn 9 dager")
    inner class TestTilbakedateringsreglerMindreEnn9Dager {
        @Test
        fun `ikke tilbakedatert, Status OK`() {
            val payload =
                testTilbakedateringRulePayload(
                    perioder = testPeriode(0, 1),
                    signaturdato = LocalDateTime.now(),
                )

            val (result) = TilbakedateringRules(payload).execute()

            assertEquals(result.treeResult.status, RuleStatus.OK)
            assertPath(result.rulePath, listOf(TILBAKEDATERING to false))

            assertEquals(
                result.ruleInputs,
                mapOf(
                    "fom" to payload.perioder.earliestFom(),
                    "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                ),
            )

            assertNull(result.treeResult.ruleOutcome)
        }

        @Test
        fun `tilbakedatert med en dag`() {
            val payload =
                testTilbakedateringRulePayload(
                    perioder = testPeriode(-1, 0),
                    signaturdato = LocalDateTime.now(),
                )

            val (result) = TilbakedateringRules(payload).execute()

            assertEquals(result.treeResult.status, RuleStatus.OK)
            assertPath(
                result.rulePath,
                listOf(
                    TILBAKEDATERING to true,
                    ETTERSENDING to false,
                    TILBAKEDATERT_INNTIL_4_DAGER to true,
                ),
            )

            assertEquals(
                result.ruleInputs,
                mapOf(
                    "fom" to payload.perioder.earliestFom(),
                    "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                ),
            )
            assertNull(result.treeResult.ruleOutcome)
        }

        @Test
        fun `tilbakedatert forlengelse med ettersending`() {
            val payload =
                testTilbakedateringRulePayload(
                    perioder = testPeriode(-8, -7),
                    signaturdato = LocalDateTime.now(),
                    ettersendingAv = "ettersendt-id",
                )

            val (result) = TilbakedateringRules(payload).execute()
            assertEquals(result.treeResult.status, RuleStatus.OK)
            assertPath(result.rulePath, listOf(TILBAKEDATERING to true, ETTERSENDING to true))

            assertEquals(
                result.ruleInputs,
                mapOf(
                    "fom" to payload.perioder.earliestFom(),
                    "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                    "ettersending" to "ettersendt-id",
                ),
            )

            assertNull(result.treeResult.ruleOutcome)
        }

        @Test
        fun `tilbakedatert forlengelse uten ettersending`() {
            val payload =
                testTilbakedateringRulePayload(
                    perioder = testPeriode(-3, 0),
                    signaturdato = LocalDateTime.now(),
                    begrunnelseIkkeKontakt = "det går bra",
                )

            val (result) = TilbakedateringRules(payload).execute()

            assertEquals(result.treeResult.status, RuleStatus.OK)
            assertPath(
                result.rulePath,
                listOf(
                    TILBAKEDATERING to true,
                    ETTERSENDING to false,
                    TILBAKEDATERT_INNTIL_4_DAGER to true,
                ),
            )

            assertEquals(
                result.ruleInputs,
                mapOf(
                    "fom" to payload.perioder.earliestFom(),
                    "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                ),
            )
        }

        @Nested
        @DisplayName("Tilbakedatert inntil 8 dager")
        inner class Tilbakedatert {

            @Test
            fun `Med begrunnelse OK`() {
                val payload =
                    testTilbakedateringRulePayload(
                        perioder = testPeriode(-5, 0),
                        signaturdato = LocalDateTime.now(),
                        begrunnelseIkkeKontakt = "Det er begrunna!",
                    )

                val (result) = TilbakedateringRules(payload).execute()

                assertEquals(result.treeResult.status, RuleStatus.OK)
                assertPath(
                    result.rulePath,
                    listOf(
                        TILBAKEDATERING to true,
                        ETTERSENDING to false,
                        TILBAKEDATERT_INNTIL_4_DAGER to false,
                        TILBAKEDATERT_INNTIL_8_DAGER to true,
                        BEGRUNNELSE_MIN_1_ORD to true,
                    ),
                )

                assertEquals(
                    result.ruleInputs,
                    mapOf(
                        "fom" to payload.perioder.earliestFom(),
                        "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                        "begrunnelse" to "3 ord",
                    ),
                )
                assertEquals(result.treeResult.status, RuleStatus.OK)
            }

            @Test
            fun `Uten begrunnelse, Invalid`() {
                val payload =
                    testTilbakedateringRulePayload(
                        perioder = testPeriode(-5, 0),
                        signaturdato = LocalDateTime.now(),
                        begrunnelseIkkeKontakt = null,
                    )

                val (result) = TilbakedateringRules(payload).execute()
                assertEquals(result.treeResult.status, RuleStatus.INVALID)
                assertPath(
                    result.rulePath,
                    listOf(
                        TILBAKEDATERING to true,
                        ETTERSENDING to false,
                        TILBAKEDATERT_INNTIL_4_DAGER to false,
                        TILBAKEDATERT_INNTIL_8_DAGER to true,
                        BEGRUNNELSE_MIN_1_ORD to false,
                        FORLENGELSE to false,
                        SPESIALISTHELSETJENESTEN to false,
                    ),
                )

                assertEquals(
                    result.ruleInputs,
                    mapOf(
                        "fom" to payload.perioder.earliestFom(),
                        "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                        "begrunnelse" to "0 ord",
                        "diagnosesystem" to "",
                        "spesialisthelsetjenesten" to false,
                    ),
                )

                assertEquals(result.treeResult.ruleOutcome, Outcomes.INNTIL_8_DAGER)
            }

            @Test
            fun `Forlengelse, OK`() {
                val payload =
                    testTilbakedateringRulePayload(
                        perioder = testPeriode(-5, 0),
                        signaturdato = LocalDateTime.now(),
                        ettersendingAv = null,
                        begrunnelseIkkeKontakt = null,
                        forlengelse = true,
                    )

                val (result) = TilbakedateringRules(payload).execute()

                assertEquals(result.treeResult.status, RuleStatus.OK)
                assertPath(
                    result.rulePath,
                    listOf(
                        TILBAKEDATERING to true,
                        ETTERSENDING to false,
                        TILBAKEDATERT_INNTIL_4_DAGER to false,
                        TILBAKEDATERT_INNTIL_8_DAGER to true,
                        BEGRUNNELSE_MIN_1_ORD to false,
                        FORLENGELSE to true,
                    ),
                )

                assertEquals(
                    result.ruleInputs,
                    mapOf(
                        "fom" to payload.perioder.earliestFom(),
                        "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                        "begrunnelse" to "0 ord",
                        "forlengelse" to true,
                    ),
                )

                assertNull(result.treeResult.ruleOutcome)
            }

            @Test
            fun `Ikke forlengelse, INVALID`() {
                val payload =
                    testTilbakedateringRulePayload(
                        perioder = testPeriode(-5, 0),
                        signaturdato = LocalDateTime.now(),
                        ettersendingAv = null,
                        begrunnelseIkkeKontakt = null,
                        forlengelse = null,
                    )

                val (result) = TilbakedateringRules(payload).execute()

                assertEquals(result.treeResult.status, RuleStatus.INVALID)
                assertPath(
                    result.rulePath,
                    listOf(
                        TILBAKEDATERING to true,
                        ETTERSENDING to false,
                        TILBAKEDATERT_INNTIL_4_DAGER to false,
                        TILBAKEDATERT_INNTIL_8_DAGER to true,
                        BEGRUNNELSE_MIN_1_ORD to false,
                        FORLENGELSE to false,
                        SPESIALISTHELSETJENESTEN to false,
                    ),
                )

                assertEquals(
                    result.ruleInputs,
                    mapOf(
                        "fom" to payload.perioder.earliestFom(),
                        "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                        "begrunnelse" to "0 ord",
                        "diagnosesystem" to "",
                        "spesialisthelsetjenesten" to false,
                    ),
                )
            }

            @Test
            fun `Ikke forlengelse, men fra spesialishelsetjenesten, OK`() {
                val payload =
                    testTilbakedateringRulePayload(
                        perioder = testPeriode(-5, 0),
                        signaturdato = LocalDateTime.now(),
                        ettersendingAv = null,
                        begrunnelseIkkeKontakt = null,
                        forlengelse = null,
                        hoveddiagnoseSystem = Diagnosekoder.ICD10_CODE,
                    )

                val (result) = TilbakedateringRules(payload).execute()

                assertEquals(result.treeResult.status, RuleStatus.OK)
                assertPath(
                    result.rulePath,
                    listOf(
                        TILBAKEDATERING to true,
                        ETTERSENDING to false,
                        TILBAKEDATERT_INNTIL_4_DAGER to false,
                        TILBAKEDATERT_INNTIL_8_DAGER to true,
                        BEGRUNNELSE_MIN_1_ORD to false,
                        FORLENGELSE to false,
                        SPESIALISTHELSETJENESTEN to true,
                    ),
                )

                assertEquals(
                    result.ruleInputs,
                    mapOf(
                        "fom" to payload.perioder.earliestFom(),
                        "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                        "begrunnelse" to "0 ord",
                        "diagnosesystem" to Diagnosekoder.ICD10_CODE,
                        "spesialisthelsetjenesten" to true,
                    ),
                )
            }
        }

        @Nested
        @DisplayName("Test tilbakedatering mellog 8 og 30 dager")
        inner class TestTilbakedateringMellog8Og30Dager {

            @Nested
            @DisplayName("Uten Begrunnelse")
            inner class UtenBegrunnelse {
                @Test
                fun `Fra Spesialhelsetjenesten, OK`() {
                    val payload =
                        testTilbakedateringRulePayload(
                            perioder = testPeriode(-9, 0),
                            signaturdato = LocalDateTime.now(),
                            ettersendingAv = null,
                            begrunnelseIkkeKontakt = null,
                            forlengelse = null,
                            hoveddiagnoseSystem = Diagnosekoder.ICD10_CODE,
                        )
                    val (result) = TilbakedateringRules(payload).execute()

                    assertEquals(result.treeResult.status, RuleStatus.OK)
                    assertPath(
                        result.rulePath,
                        listOf(
                            TILBAKEDATERING to true,
                            ETTERSENDING to false,
                            TILBAKEDATERT_INNTIL_4_DAGER to false,
                            TILBAKEDATERT_INNTIL_8_DAGER to false,
                            TILBAKEDATERT_MINDRE_ENN_1_MAANED to true,
                            BEGRUNNELSE_MIN_1_ORD to false,
                            SPESIALISTHELSETJENESTEN to true,
                        ),
                    )
                    assertEquals(
                        result.ruleInputs,
                        mapOf(
                            "fom" to payload.perioder.earliestFom(),
                            "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                            "begrunnelse" to "0 ord",
                            "diagnosesystem" to Diagnosekoder.ICD10_CODE,
                            "spesialisthelsetjenesten" to true,
                        ),
                    )
                }

                @Test
                fun `Ikke fra spesialhelsetjenesten, INVALID`() {
                    val payload =
                        testTilbakedateringRulePayload(
                            perioder = testPeriode(-9, 0),
                            signaturdato = LocalDateTime.now(),
                            ettersendingAv = null,
                            begrunnelseIkkeKontakt = null,
                            forlengelse = null,
                        )

                    val (result) = TilbakedateringRules(payload).execute()

                    assertEquals(result.treeResult.status, RuleStatus.INVALID)
                    assertPath(
                        result.rulePath,
                        listOf(
                            TILBAKEDATERING to true,
                            ETTERSENDING to false,
                            TILBAKEDATERT_INNTIL_4_DAGER to false,
                            TILBAKEDATERT_INNTIL_8_DAGER to false,
                            TILBAKEDATERT_MINDRE_ENN_1_MAANED to true,
                            BEGRUNNELSE_MIN_1_ORD to false,
                            SPESIALISTHELSETJENESTEN to false,
                        ),
                    )

                    assertEquals(
                        result.ruleInputs,
                        mapOf(
                            "fom" to payload.perioder.earliestFom(),
                            "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                            "begrunnelse" to "0 ord",
                            "diagnosesystem" to "",
                            "spesialisthelsetjenesten" to false,
                        ),
                    )

                    assertEquals(result.treeResult.ruleOutcome, Outcomes.MINDRE_ENN_1_MAANED)
                }
            }

            @Nested
            @DisplayName("Med Begrunnelse")
            inner class MedBegrunnelse {
                @Test
                fun `ikke god nok begrunnelse, INVALID`() {
                    val payload =
                        testTilbakedateringRulePayload(
                            perioder = testPeriode(-9, 0),
                            signaturdato = LocalDateTime.now(),
                            ettersendingAv = null,
                            begrunnelseIkkeKontakt = "12344123112341232....,,,..12",
                            forlengelse = null,
                        )
                    val (result) = TilbakedateringRules(payload).execute()

                    assertEquals(result.treeResult.status, RuleStatus.INVALID)
                    assertPath(
                        result.rulePath,
                        listOf(
                            TILBAKEDATERING to true,
                            ETTERSENDING to false,
                            TILBAKEDATERT_INNTIL_4_DAGER to false,
                            TILBAKEDATERT_INNTIL_8_DAGER to false,
                            TILBAKEDATERT_MINDRE_ENN_1_MAANED to true,
                            BEGRUNNELSE_MIN_1_ORD to false,
                            SPESIALISTHELSETJENESTEN to false,
                        ),
                    )

                    assertEquals(
                        result.ruleInputs,
                        mapOf(
                            "fom" to payload.perioder.earliestFom(),
                            "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                            "begrunnelse" to "0 ord",
                            "diagnosesystem" to "",
                            "spesialisthelsetjenesten" to false,
                        ),
                    )
                }

                @Test
                fun `Forlengelse, OK`() {
                    val payload =
                        testTilbakedateringRulePayload(
                            perioder = testPeriode(-9, 0),
                            signaturdato = LocalDateTime.now(),
                            ettersendingAv = null,
                            begrunnelseIkkeKontakt = "abcdefghijklmnopq",
                            forlengelse = true,
                        )
                    val (result) = TilbakedateringRules(payload).execute()

                    assertEquals(result.treeResult.status, RuleStatus.OK)
                    assertPath(
                        result.rulePath,
                        listOf(
                            TILBAKEDATERING to true,
                            ETTERSENDING to false,
                            TILBAKEDATERT_INNTIL_4_DAGER to false,
                            TILBAKEDATERT_INNTIL_8_DAGER to false,
                            TILBAKEDATERT_MINDRE_ENN_1_MAANED to true,
                            BEGRUNNELSE_MIN_1_ORD to true,
                            FORLENGELSE to true,
                        ),
                    )

                    assertEquals(
                        result.ruleInputs,
                        mapOf(
                            "fom" to payload.perioder.earliestFom(),
                            "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                            "begrunnelse" to "1 ord",
                            "forlengelse" to true,
                        ),
                    )
                }

                @Test
                @Disabled
                fun `Ikke forlengelse, MANUELL`() {
                    val payload =
                        testTilbakedateringRulePayload(
                            perioder = testPeriode(-9, 0),
                            signaturdato = LocalDateTime.now(),
                            ettersendingAv = null,
                            begrunnelseIkkeKontakt = "abcdefghijklmnopq",
                            forlengelse = null,
                            // TODO: Business logikk må fikses
                            dagerForArbeidsgiverperiodeCheck = listOf(),
                        )

                    val (result) = TilbakedateringRules(payload).execute()

                    result.debugPath()

                    assertEquals(result.treeResult.status, RuleStatus.MANUAL_PROCESSING)
                    assertPath(
                        result.rulePath,
                        listOf(
                            TILBAKEDATERING to true,
                            ETTERSENDING to false,
                            TILBAKEDATERT_INNTIL_4_DAGER to false,
                            TILBAKEDATERT_INNTIL_8_DAGER to false,
                            TILBAKEDATERT_MINDRE_ENN_1_MAANED to true,
                            BEGRUNNELSE_MIN_1_ORD to true,
                            FORLENGELSE to false,
                            ARBEIDSGIVERPERIODE to false,
                            SPESIALISTHELSETJENESTEN to false,
                        ),
                    )

                    assertEquals(
                        result.ruleInputs,
                        mapOf(
                            "fom" to payload.perioder.earliestFom(),
                            "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                            "begrunnelse" to "1 ord",
                            "forlengelse" to false,
                        ),
                    )
                }

                @Test
                @Disabled
                fun `Innenfor arbeidsgiverperioden, OK`() {
                    val payload =
                        testTilbakedateringRulePayload(
                            perioder = testPeriode(-9, 0),
                            signaturdato = LocalDateTime.now(),
                            ettersendingAv = null,
                            begrunnelseIkkeKontakt = "abcdefghijklmnopq",
                            forlengelse = null,
                            // TODO: Business logikk må fikses
                            dagerForArbeidsgiverperiodeCheck = listOf(),
                        )

                    val (result) = TilbakedateringRules(payload).execute()

                    assertEquals(result.treeResult.status, RuleStatus.OK)
                    assertPath(
                        result.rulePath,
                        listOf(
                            TILBAKEDATERING to true,
                            ETTERSENDING to false,
                            TILBAKEDATERT_INNTIL_4_DAGER to false,
                            TILBAKEDATERT_INNTIL_8_DAGER to false,
                            TILBAKEDATERT_MINDRE_ENN_1_MAANED to true,
                            BEGRUNNELSE_MIN_1_ORD to true,
                            FORLENGELSE to false,
                            ARBEIDSGIVERPERIODE to true,
                        ),
                    )

                    assertEquals(
                        result.ruleInputs,
                        mapOf(
                            "fom" to payload.perioder.earliestFom(),
                            "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                            "begrunnelse" to "1 ord",
                            "forlengelse" to false,
                        ),
                    )
                }

                @Test
                @Disabled
                fun `Utenfor arbeidsgiverperioden, MANUELL`() {
                    val payload =
                        testTilbakedateringRulePayload(
                            perioder = testPeriode(-9, 0),
                            signaturdato = LocalDateTime.now(),
                            ettersendingAv = null,
                            begrunnelseIkkeKontakt = "abcdefghijklmnopq",
                            forlengelse = null,
                            // TODO: Business logikk må fikses
                            dagerForArbeidsgiverperiodeCheck = listOf(),
                        )

                    val (result) = TilbakedateringRules(payload).execute()

                    assertEquals(result.treeResult.status, RuleStatus.MANUAL_PROCESSING)
                    assertPath(
                        result.rulePath,
                        listOf(
                            TILBAKEDATERING to true,
                            ETTERSENDING to false,
                            TILBAKEDATERT_INNTIL_4_DAGER to false,
                            TILBAKEDATERT_INNTIL_8_DAGER to false,
                            TILBAKEDATERT_MINDRE_ENN_1_MAANED to true,
                            BEGRUNNELSE_MIN_1_ORD to true,
                            FORLENGELSE to false,
                            ARBEIDSGIVERPERIODE to false,
                            SPESIALISTHELSETJENESTEN to false,
                        ),
                    )

                    assertEquals(
                        result.ruleInputs,
                        mapOf(
                            "fom" to payload.perioder.earliestFom(),
                            "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                            "begrunnelse" to "1 ord",
                            "forlengelse" to false,
                        ),
                    )
                }

                @Test
                @Disabled
                fun `Utenfor arbeidsgiverperioden andre sykmelding, MANUELL`() {
                    val payload =
                        testTilbakedateringRulePayload(
                            perioder = testPeriode(-9, 0),
                            signaturdato = LocalDateTime.now(),
                            ettersendingAv = null,
                            begrunnelseIkkeKontakt = "abcdefghijklmnopq",
                            forlengelse = null,
                            // TODO: Business logikk må fikses
                            dagerForArbeidsgiverperiodeCheck = listOf(),
                        )

                    /*
                    val dager =
                        sykmeldingService.allDaysBetween(
                            sykmelding.perioder.sortedFOMDate().first(),
                            sykmelding.perioder.sortedTOMDate().last(),
                        ) +
                            sykmeldingService
                                .allDaysBetween(
                                    LocalDate.now().minusDays(20),
                                    LocalDate.now().minusDays(3),
                                )
                                .sortedDescending()
                                .take(17)
                     */

                    val (result) = TilbakedateringRules(payload).execute()

                    assertEquals(result.treeResult.status, RuleStatus.MANUAL_PROCESSING)
                    assertPath(
                        result.rulePath,
                        listOf(
                            TILBAKEDATERING to true,
                            ETTERSENDING to false,
                            TILBAKEDATERT_INNTIL_4_DAGER to false,
                            TILBAKEDATERT_INNTIL_8_DAGER to false,
                            TILBAKEDATERT_MINDRE_ENN_1_MAANED to true,
                            BEGRUNNELSE_MIN_1_ORD to true,
                            FORLENGELSE to false,
                            ARBEIDSGIVERPERIODE to false,
                            SPESIALISTHELSETJENESTEN to false,
                        ),
                    )

                    assertEquals(
                        result.ruleInputs,
                        mapOf(
                            "fom" to payload.perioder.earliestFom(),
                            "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                            "begrunnelse" to "1 ord",
                            "forlengelse" to false,
                        ),
                    )
                }

                @Test
                fun `Fra spesialisthelsetjenesten, OK`() {
                    val dagerForArbeidsgiverperiodeCheck =
                        allDaysBetween(LocalDate.now().minusDays(20), LocalDate.now().minusDays(3))
                    val payload =
                        testTilbakedateringRulePayload(
                            perioder = testPeriode(-9, 0),
                            signaturdato = LocalDateTime.now(),
                            ettersendingAv = null,
                            begrunnelseIkkeKontakt = "abcdefghijklmnopq",
                            forlengelse = null,
                            hoveddiagnoseSystem = Diagnosekoder.ICD10_CODE,
                            // TODO: Business logikk må fikses
                            dagerForArbeidsgiverperiodeCheck = dagerForArbeidsgiverperiodeCheck,
                        )

                    val (result) = TilbakedateringRules(payload).execute()

                    assertEquals(result.treeResult.status, RuleStatus.OK)
                    assertPath(
                        result.rulePath,
                        listOf(
                            TILBAKEDATERING to true,
                            ETTERSENDING to false,
                            TILBAKEDATERT_INNTIL_4_DAGER to false,
                            TILBAKEDATERT_INNTIL_8_DAGER to false,
                            TILBAKEDATERT_MINDRE_ENN_1_MAANED to true,
                            BEGRUNNELSE_MIN_1_ORD to true,
                            FORLENGELSE to false,
                            ARBEIDSGIVERPERIODE to false,
                            SPESIALISTHELSETJENESTEN to true,
                        ),
                    )

                    assertEquals(
                        result.ruleInputs,
                        mapOf(
                            "fom" to payload.perioder.earliestFom(),
                            "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                            "begrunnelse" to "1 ord",
                            "tom" to payload.perioder.latestTom(),
                            "arbeidsgiverperiode" to false,
                            "dagerForArbeidsgiverperiode" to dagerForArbeidsgiverperiodeCheck,
                            "diagnosesystem" to Diagnosekoder.ICD10_CODE,
                            "spesialisthelsetjenesten" to true,
                        ),
                    )
                }
            }
        }

        @Nested
        @DisplayName("Over 1 måned")
        inner class Over1Maned {
            @Test
            fun `Med begrunnelse, MANUELL`() {
                val payload =
                    testTilbakedateringRulePayload(
                        perioder = testPeriode(-31, 0),
                        signaturdato = LocalDateTime.now(),
                        ettersendingAv = null,
                        begrunnelseIkkeKontakt = "Veldig bra begrunnels!",
                        forlengelse = null,
                    )

                val (result) = TilbakedateringRules(payload).execute()

                result.debugPath()

                assertEquals(result.treeResult.status, RuleStatus.MANUAL_PROCESSING)
                assertPath(
                    result.rulePath,
                    listOf(
                        TILBAKEDATERING to true,
                        ETTERSENDING to false,
                        TILBAKEDATERT_INNTIL_4_DAGER to false,
                        TILBAKEDATERT_INNTIL_8_DAGER to false,
                        TILBAKEDATERT_MINDRE_ENN_1_MAANED to false,
                        BEGRUNNELSE_MIN_3_ORD to true,
                    ),
                )

                assertEquals(
                    result.ruleInputs,
                    mapOf(
                        "fom" to payload.perioder.earliestFom(),
                        "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                        "begrunnelse" to "3 ord",
                    ),
                )
            }

            @Test
            fun `Ikke god nok begrunnelse, INVALID`() {
                val payload =
                    testTilbakedateringRulePayload(
                        perioder = testPeriode(-31, 0),
                        signaturdato = LocalDateTime.now(),
                        ettersendingAv = null,
                        begrunnelseIkkeKontakt = "Dårlig begrunnels>:(",
                        forlengelse = null,
                    )
                val (result) = TilbakedateringRules(payload).execute()

                assertEquals(result.treeResult.status, RuleStatus.INVALID)
                assertPath(
                    result.rulePath,
                    listOf(
                        TILBAKEDATERING to true,
                        ETTERSENDING to false,
                        TILBAKEDATERT_INNTIL_4_DAGER to false,
                        TILBAKEDATERT_INNTIL_8_DAGER to false,
                        TILBAKEDATERT_MINDRE_ENN_1_MAANED to false,
                        BEGRUNNELSE_MIN_3_ORD to false,
                        SPESIALISTHELSETJENESTEN to false,
                    ),
                )

                assertEquals(
                    result.ruleInputs,
                    mapOf(
                        "fom" to payload.perioder.earliestFom(),
                        "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                        "begrunnelse" to "2 ord",
                        "diagnosesystem" to "",
                        "spesialisthelsetjenesten" to false,
                    ),
                )
            }

            @Test
            fun `Fra spesialisthelsetjenesten, MANUELL`() {
                val payload =
                    testTilbakedateringRulePayload(
                        perioder = testPeriode(-31, 0),
                        signaturdato = LocalDateTime.now(),
                        ettersendingAv = null,
                        begrunnelseIkkeKontakt = "abcdefghijklmno",
                        forlengelse = null,
                        hoveddiagnoseSystem = Diagnosekoder.ICD10_CODE,
                    )
                val (result) = TilbakedateringRules(payload).execute()

                assertEquals(result.treeResult.status, RuleStatus.MANUAL_PROCESSING)
                assertPath(
                    result.rulePath,
                    listOf(
                        TILBAKEDATERING to true,
                        ETTERSENDING to false,
                        TILBAKEDATERT_INNTIL_4_DAGER to false,
                        TILBAKEDATERT_INNTIL_8_DAGER to false,
                        TILBAKEDATERT_MINDRE_ENN_1_MAANED to false,
                        BEGRUNNELSE_MIN_3_ORD to false,
                        SPESIALISTHELSETJENESTEN to true,
                    ),
                )

                assertEquals(
                    result.ruleInputs,
                    mapOf(
                        "fom" to payload.perioder.earliestFom(),
                        "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                        "begrunnelse" to "1 ord",
                        "diagnosesystem" to Diagnosekoder.ICD10_CODE,
                        "spesialisthelsetjenesten" to true,
                    ),
                )
            }

            @Test
            fun `mer enn 1 måned og 32 dager`() {
                val payload =
                    testTilbakedateringRulePayload(
                        perioder =
                            listOf(
                                FomTom(
                                    fom = LocalDate.of(2024, 7, 30),
                                    tom = LocalDate.of(2024, 7, 31),
                                )
                            ),
                        signaturdato = LocalDate.of(2024, 8, 31).atStartOfDay(),
                        ettersendingAv = null,
                        begrunnelseIkkeKontakt = "abcdefghijklmno",
                        forlengelse = null,
                        hoveddiagnoseSystem = Diagnosekoder.ICPC2_CODE,
                    )

                val (result) = TilbakedateringRules(payload).execute()

                assertEquals(result.treeResult.status, RuleStatus.INVALID)
                assertPath(
                    result.rulePath,
                    listOf(
                        TILBAKEDATERING to true,
                        ETTERSENDING to false,
                        TILBAKEDATERT_INNTIL_4_DAGER to false,
                        TILBAKEDATERT_INNTIL_8_DAGER to false,
                        TILBAKEDATERT_MINDRE_ENN_1_MAANED to false,
                        BEGRUNNELSE_MIN_3_ORD to false,
                        SPESIALISTHELSETJENESTEN to false,
                    ),
                )

                assertEquals(
                    result.ruleInputs,
                    mapOf(
                        "fom" to payload.perioder.earliestFom(),
                        "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                        "begrunnelse" to "1 ord",
                        "diagnosesystem" to Diagnosekoder.ICPC2_CODE,
                        "spesialisthelsetjenesten" to false,
                    ),
                )
            }

            @Test
            fun `mindre enn én måned, men 31 dager`() {
                val payload =
                    testTilbakedateringRulePayload(
                        perioder =
                            listOf(
                                FomTom(
                                    fom = LocalDate.of(2024, 7, 30),
                                    tom = LocalDate.of(2024, 7, 31),
                                )
                            ),
                        signaturdato = LocalDate.of(2024, 8, 30).atStartOfDay(),
                        begrunnelseIkkeKontakt = "abcghgfgh",
                    )

                val (result) = TilbakedateringRules(payload).execute()

                assertEquals(result.treeResult.status, RuleStatus.OK)
                assertPath(
                    result.rulePath,
                    listOf(
                        TILBAKEDATERING to true,
                        ETTERSENDING to false,
                        TILBAKEDATERT_INNTIL_4_DAGER to false,
                        TILBAKEDATERT_INNTIL_8_DAGER to false,
                        TILBAKEDATERT_MINDRE_ENN_1_MAANED to true,
                        BEGRUNNELSE_MIN_1_ORD to true,
                        FORLENGELSE to false,
                        ARBEIDSGIVERPERIODE to true,
                    ),
                )

                assertEquals(
                    result.ruleInputs,
                    mapOf(
                        "fom" to payload.perioder.earliestFom(),
                        "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                        "begrunnelse" to "1 ord",
                        "tom" to payload.perioder.latestTom(),
                        "arbeidsgiverperiode" to true,
                        "dagerForArbeidsgiverperiode" to listOf<LocalDate>(),
                    ),
                )
            }

            @Test
            fun `ikke mindre enn én måned, men 29 dager`() {
                val payload =
                    testTilbakedateringRulePayload(
                        perioder =
                            listOf(
                                FomTom(
                                    fom = LocalDate.of(2024, 1, 28),
                                    tom = LocalDate.of(2024, 2, 1),
                                )
                            ),
                        signaturdato = LocalDate.of(2024, 2, 29).atStartOfDay(),
                        begrunnelseIkkeKontakt = "abcghgfgh",
                        hoveddiagnoseSystem = Diagnosekoder.ICD10_CODE,
                    )

                val (result) = TilbakedateringRules(payload).execute()

                assertEquals(result.treeResult.status, RuleStatus.MANUAL_PROCESSING)
                assertPath(
                    result.rulePath,
                    listOf(
                        TILBAKEDATERING to true,
                        ETTERSENDING to false,
                        TILBAKEDATERT_INNTIL_4_DAGER to false,
                        TILBAKEDATERT_INNTIL_8_DAGER to false,
                        TILBAKEDATERT_MINDRE_ENN_1_MAANED to false,
                        BEGRUNNELSE_MIN_3_ORD to false,
                        SPESIALISTHELSETJENESTEN to true,
                    ),
                )

                assertEquals(
                    result.ruleInputs,
                    mapOf(
                        "fom" to payload.perioder.earliestFom(),
                        "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                        "begrunnelse" to "1 ord",
                        "diagnosesystem" to Diagnosekoder.ICD10_CODE,
                        "spesialisthelsetjenesten" to true,
                    ),
                )
            }
        }
    }
}

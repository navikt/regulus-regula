package no.nav.tsm.regulus.regula.trees.tilbakedatering

import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import no.nav.helse.diagnosekoder.Diagnosekoder
import no.nav.tsm.regulus.regula.executor.RuleStatus
import no.nav.tsm.regulus.regula.payload.Diagnose
import no.nav.tsm.regulus.regula.payload.SykmeldingPeriode
import no.nav.tsm.regulus.regula.testutils.february
import no.nav.tsm.regulus.regula.trees.assertPath
import no.nav.tsm.regulus.regula.trees.tilbakedatering.TilbakedateringRule.*
import no.nav.tsm.regulus.regula.trees.tilbakedatering.extras.Ettersendelse
import no.nav.tsm.regulus.regula.trees.tilbakedatering.extras.Forlengelse
import no.nav.tsm.regulus.regula.utils.earliestFom
import no.nav.tsm.regulus.regula.utils.latestTom
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
        fun `tilbakedatert med en dag går fint`() {
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
        fun `tilbakedatert inntil 4 dager går fint`() {
            val payload =
                testTilbakedateringRulePayload(
                    perioder = testPeriode(-4, 0),
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
        }

        @Test
        fun `tilbakedatert forlengelse med ettersending`() {
            // Re-create periods, to ensure that equality is not based on reference
            val makePerioder = { testPeriode(-8, -7) }
            val payload =
                testTilbakedateringRulePayload(
                    perioder = makePerioder(),
                    signaturdato = LocalDateTime.now(),
                    tidligereSykmeldinger =
                        listOf(
                            TidligereSykmelding(
                                sykmeldingId = "dette-er-ettersendelse",
                                perioder = makePerioder(),
                                hoveddiagnose = null,
                            )
                        ),
                    hoveddiagnose = Diagnose("X01", Diagnosekoder.ICPC2_CODE),
                )

            val (result) = TilbakedateringRules(payload).execute()

            assertEquals(result.treeResult.status, RuleStatus.OK)
            assertPath(result.rulePath, listOf(TILBAKEDATERING to true, ETTERSENDING to true))

            assertEquals(
                result.ruleInputs,
                mapOf(
                    "fom" to payload.perioder.earliestFom(),
                    "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                    "ettersending" to
                        Ettersendelse(
                            sykmeldingId = "dette-er-ettersendelse",
                            fom = makePerioder().first().fom,
                            tom = makePerioder().first().tom,
                            gradert = null,
                        ),
                ),
            )

            assertNull(result.treeResult.ruleOutcome)
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
                        "diagnosesystem" to Diagnosekoder.ICPC2_CODE,
                        "spesialisthelsetjenesten" to false,
                    ),
                )

                assertEquals(result.treeResult.ruleOutcome, Outcomes.INNTIL_8_DAGER)
            }

            @Test
            fun `Forlengelse, OK`() {
                val payload =
                    testTilbakedateringRulePayload(
                        perioder =
                            listOf(
                                SykmeldingPeriode.AktivitetIkkeMulig(
                                    fom = 16.february(2023),
                                    tom = 28.february(2023),
                                )
                            ),
                        signaturdato = 24.february(2023).atStartOfDay(),
                        begrunnelseIkkeKontakt = null,
                        hoveddiagnose = Diagnose("X01", Diagnosekoder.ICPC2_CODE),
                        tidligereSykmeldinger =
                            listOf(
                                TidligereSykmelding(
                                    sykmeldingId = "dette-er-en-forlengelse",
                                    perioder =
                                        listOf(
                                            SykmeldingPeriode.AktivitetIkkeMulig(
                                                fom = 1.february(2023),
                                                tom = 15.february(2023),
                                            )
                                        ),
                                    hoveddiagnose = Diagnose("X01", Diagnosekoder.ICPC2_CODE),
                                )
                            ),
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
                        "forlengelse" to
                            Forlengelse(
                                sykmeldingId = "dette-er-en-forlengelse",
                                fom = 1.february(2023),
                                tom = 15.february(2023),
                                gradert = null,
                            ),
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
                        begrunnelseIkkeKontakt = null,
                        tidligereSykmeldinger = listOf(),
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
                        "diagnosesystem" to Diagnosekoder.ICPC2_CODE,
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
                        begrunnelseIkkeKontakt = null,
                        tidligereSykmeldinger = emptyList(),
                        hoveddiagnose = Diagnose("X01", Diagnosekoder.ICD10_CODE),
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
                            begrunnelseIkkeKontakt = null,
                            hoveddiagnose = Diagnose("X01", Diagnosekoder.ICD10_CODE),
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
                            "diagnosesystem" to Diagnosekoder.ICPC2_CODE,
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
                            begrunnelseIkkeKontakt = "12344123112341232....,,,..12",
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
                            "diagnosesystem" to Diagnosekoder.ICPC2_CODE,
                            "spesialisthelsetjenesten" to false,
                        ),
                    )
                }

                @Test
                fun `Forlengelse, OK`() {
                    val payload =
                        testTilbakedateringRulePayload(
                            perioder =
                                listOf(
                                    SykmeldingPeriode.AktivitetIkkeMulig(
                                        fom = 16.february(2023),
                                        tom = 28.february(2023),
                                    )
                                ),
                            signaturdato = 26.february(2023).atStartOfDay(),
                            tidligereSykmeldinger =
                                listOf(
                                    TidligereSykmelding(
                                        sykmeldingId = "dette-er-en-forlengelse",
                                        perioder =
                                            listOf(
                                                SykmeldingPeriode.AktivitetIkkeMulig(
                                                    fom = 1.february(2023),
                                                    tom = 15.february(2023),
                                                )
                                            ),
                                        hoveddiagnose = Diagnose("X01", Diagnosekoder.ICPC2_CODE),
                                    )
                                ),
                            begrunnelseIkkeKontakt = "abcdefghijklmnopq",
                            hoveddiagnose = Diagnose("X01", Diagnosekoder.ICPC2_CODE),
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
                            "forlengelse" to
                                Forlengelse(
                                    sykmeldingId = "dette-er-en-forlengelse",
                                    fom = 1.february(2023),
                                    tom = 15.february(2023),
                                    gradert = null,
                                ),
                        ),
                    )
                }

                @Test
                fun `Ikke forlengelse, MANUELL`() {
                    val payload =
                        testTilbakedateringRulePayload(
                            perioder = testPeriode(-16, 0),
                            signaturdato = LocalDateTime.now(),
                            begrunnelseIkkeKontakt = "abcdefghijklmnopq",
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
                        result.ruleInputs - "dagerForArbeidsgiverperiode",
                        mapOf(
                            "fom" to payload.perioder.earliestFom(),
                            "tom" to payload.perioder.latestTom(),
                            "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                            "begrunnelse" to "1 ord",
                            "diagnosesystem" to Diagnosekoder.ICPC2_CODE,
                            "spesialisthelsetjenesten" to false,
                        ),
                    )
                }

                @Test
                fun `Innenfor arbeidsgiverperioden, OK`() {
                    val payload =
                        testTilbakedateringRulePayload(
                            perioder = testPeriode(-9, 0),
                            signaturdato = LocalDateTime.now(),
                            begrunnelseIkkeKontakt = "abcdefghijklmnopq",
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
                        result.ruleInputs - "dagerForArbeidsgiverperiode",
                        mapOf(
                            "fom" to payload.perioder.earliestFom(),
                            "tom" to payload.perioder.latestTom(),
                            "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                            "begrunnelse" to "1 ord",
                        ),
                    )
                }

                @Test
                fun `Utenfor arbeidsgiverperioden, MANUELL`() {
                    val payload =
                        testTilbakedateringRulePayload(
                            perioder = testPeriode(-19, 0),
                            signaturdato = LocalDateTime.now(),
                            begrunnelseIkkeKontakt = "abcdefghijklmnopq",
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
                        result.ruleInputs - "dagerForArbeidsgiverperiode",
                        mapOf(
                            "fom" to payload.perioder.earliestFom(),
                            "tom" to payload.perioder.latestTom(),
                            "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                            "begrunnelse" to "1 ord",
                            "diagnosesystem" to Diagnosekoder.ICPC2_CODE,
                            "spesialisthelsetjenesten" to false,
                        ),
                    )
                }

                @Test
                fun `Utenfor arbeidsgiverperioden andre sykmelding, MANUELL`() {
                    val hoveddiagnose = Diagnose("X01", Diagnosekoder.ICPC2_CODE)
                    val payload =
                        testTilbakedateringRulePayload(
                            perioder = testPeriode(0, 2),
                            signaturdato = LocalDateTime.now().plusDays(10),
                            begrunnelseIkkeKontakt = "abcdefghijklmnopq",
                            hoveddiagnose = hoveddiagnose,
                            tidligereSykmeldinger =
                                listOf(
                                    TidligereSykmelding(
                                        sykmeldingId = "tidligere-sykmelding-1",
                                        perioder =
                                            listOf(
                                                SykmeldingPeriode.AktivitetIkkeMulig(
                                                    fom = LocalDate.now().minusDays(20),
                                                    tom = LocalDate.now().minusDays(3),
                                                )
                                            ),
                                        hoveddiagnose = hoveddiagnose,
                                    )
                                ),
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
                        result.ruleInputs - "dagerForArbeidsgiverperiode",
                        mapOf(
                            "fom" to payload.perioder.earliestFom(),
                            "tom" to payload.perioder.latestTom(),
                            "diagnosesystem" to Diagnosekoder.ICPC2_CODE,
                            "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                            "begrunnelse" to "1 ord",
                            "spesialisthelsetjenesten" to false,
                        ),
                    )
                }

                @Test
                fun `Fra spesialisthelsetjenesten, OK`() {
                    val payload =
                        testTilbakedateringRulePayload(
                            perioder = testPeriode(-19, 0),
                            signaturdato = LocalDateTime.now(),
                            begrunnelseIkkeKontakt = "abcdefghijklmnopq",
                            hoveddiagnose = Diagnose("X01", Diagnosekoder.ICD10_CODE),
                            tidligereSykmeldinger = listOf(),
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
                        result.ruleInputs - "dagerForArbeidsgiverperiode",
                        mapOf(
                            "fom" to payload.perioder.earliestFom(),
                            "genereringstidspunkt" to payload.signaturdato.toLocalDate(),
                            "begrunnelse" to "1 ord",
                            "tom" to payload.perioder.latestTom(),
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
                        begrunnelseIkkeKontakt = "Veldig bra begrunnels!",
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
                        begrunnelseIkkeKontakt = "Dårlig begrunnels>:(",
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
                        "diagnosesystem" to Diagnosekoder.ICPC2_CODE,
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
                        begrunnelseIkkeKontakt = "abcdefghijklmno",
                        hoveddiagnose = Diagnose("X01", Diagnosekoder.ICD10_CODE),
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
                                SykmeldingPeriode.AktivitetIkkeMulig(
                                    fom = LocalDate.of(2024, 7, 30),
                                    tom = LocalDate.of(2024, 7, 31),
                                )
                            ),
                        signaturdato = LocalDate.of(2024, 8, 31).atStartOfDay(),
                        begrunnelseIkkeKontakt = "abcdefghijklmno",
                        hoveddiagnose = Diagnose("X01", Diagnosekoder.ICPC2_CODE),
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
                                SykmeldingPeriode.AktivitetIkkeMulig(
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
                        "dagerForArbeidsgiverperiode" to
                            listOf<LocalDate>(LocalDate.of(2024, 7, 30), LocalDate.of(2024, 7, 31)),
                    ),
                )
            }

            @Test
            fun `ikke mindre enn én måned, men 29 dager`() {
                val payload =
                    testTilbakedateringRulePayload(
                        perioder =
                            listOf(
                                SykmeldingPeriode.AktivitetIkkeMulig(
                                    fom = LocalDate.of(2024, 1, 28),
                                    tom = LocalDate.of(2024, 2, 1),
                                )
                            ),
                        signaturdato = LocalDate.of(2024, 2, 29).atStartOfDay(),
                        begrunnelseIkkeKontakt = "abcghgfgh",
                        hoveddiagnose = Diagnose("X01", Diagnosekoder.ICD10_CODE),
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

package no.nav.tsm.regulus.regula.trees.periode

import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import no.nav.tsm.regulus.regula.executor.RuleStatus
import no.nav.tsm.regulus.regula.payload.SykmeldingPeriode
import no.nav.tsm.regulus.regula.trees.assertPath

class PeriodeRulesTest {
    @Test
    fun `Alt er ok, Status OK`() {
        val perioder =
            listOf(
                testAktivitetIkkeMuligPeriode(
                    fom = LocalDate.now(),
                    tom = LocalDate.now().plusDays(15),
                )
            )

        val payload = testPeriodeRulePayload(perioder = perioder)

        val (result) = PeriodeRules(payload).execute()

        assertEquals(result.treeResult.status, RuleStatus.OK)
        assertPath(
            result.rulePath,
            listOf(
                PeriodeRule.PERIODER_MANGLER to false,
                PeriodeRule.FRADATO_ETTER_TILDATO to false,
                PeriodeRule.OVERLAPPENDE_PERIODER to false,
                PeriodeRule.OPPHOLD_MELLOM_PERIODER to false,
                PeriodeRule.IKKE_DEFINERT_PERIODE to false,
                PeriodeRule.BEHANDLINGSDATO_ETTER_MOTTATTDATO to false,
                PeriodeRule.AVVENTENDE_SYKMELDING_KOMBINERT to false,
                PeriodeRule.MANGLENDE_INNSPILL_TIL_ARBEIDSGIVER to false,
                PeriodeRule.AVVENTENDE_SYKMELDING_OVER_16_DAGER to false,
                PeriodeRule.FOR_MANGE_BEHANDLINGSDAGER_PER_UKE to false,
                PeriodeRule.GRADERT_SYKMELDING_OVER_99_PROSENT to false,
                PeriodeRule.SYKMELDING_MED_BEHANDLINGSDAGER to false,
            ),
        )
        assertEquals(
            result.ruleInputs,
            mapOf(
                "perioder" to perioder,
                "behandslingsDatoEtterMottatDato" to false,
                "antallAvventende" to 0,
                "antallPerioder" to 1,
                "manglendeInnspillArbeidsgiver" to false,
                "avventendeOver16Dager" to false,
                "forMangeBehandlingsDagerPrUke" to false,
                "gradertOver99Prosent" to false,
                "inneholderBehandlingsDager" to false,
            ),
        )
        assertNull(result.treeResult.ruleOutcome)
    }

    @Test
    fun `Periode mangler, Status INVALID`() {
        val payload = testPeriodeRulePayload(perioder = listOf())
        val (result) = PeriodeRules(payload).execute()

        assertEquals(result.treeResult.status, RuleStatus.INVALID)
        assertPath(result.rulePath, listOf(PeriodeRule.PERIODER_MANGLER to true))

        assertEquals(result.ruleInputs, mapOf("perioder" to emptyList<SykmeldingPeriode>()))

        assertEquals(result.treeResult.ruleOutcome, PeriodeRule.Outcomes.PERIODER_MANGLER)
    }

    @Test
    fun `Fra dato er etter til dato, Status INVALID`() {
        val perioder =
            listOf(
                testAktivitetIkkeMuligPeriode(
                    fom = LocalDate.of(2018, 1, 9),
                    tom = LocalDate.of(2018, 1, 7),
                )
            )

        val payload = testPeriodeRulePayload(perioder = perioder)

        val (result) = PeriodeRules(payload).execute()

        assertEquals(result.treeResult.status, RuleStatus.INVALID)
        assertPath(
            result.rulePath,
            listOf(PeriodeRule.PERIODER_MANGLER to false, PeriodeRule.FRADATO_ETTER_TILDATO to true),
        )

        assertEquals(result.ruleInputs, mapOf("perioder" to perioder))

        assertEquals(result.treeResult.ruleOutcome, PeriodeRule.Outcomes.FRADATO_ETTER_TILDATO)
    }

    @Test
    fun `Overlapp i perioder, Status INVALID`() {
        val perioder =
            listOf(
                testAktivitetIkkeMuligPeriode(
                    fom = LocalDate.of(2018, 1, 7),
                    tom = LocalDate.of(2018, 1, 9),
                ),
                testAktivitetIkkeMuligPeriode(
                    fom = LocalDate.of(2018, 1, 8),
                    tom = LocalDate.of(2018, 1, 12),
                ),
            )

        val payload = testPeriodeRulePayload(perioder = perioder)

        val (result) = PeriodeRules(payload).execute()

        assertEquals(result.treeResult.status, RuleStatus.INVALID)
        assertPath(
            result.rulePath,
            listOf(
                PeriodeRule.PERIODER_MANGLER to false,
                PeriodeRule.FRADATO_ETTER_TILDATO to false,
                PeriodeRule.OVERLAPPENDE_PERIODER to true,
            ),
        )

        assertEquals(result.ruleInputs, mapOf("perioder" to perioder))

        assertEquals(result.treeResult.ruleOutcome, PeriodeRule.Outcomes.OVERLAPPENDE_PERIODER)
    }

    @Test
    fun `Opphold mellom perioder, Status INVALID`() {
        val perioder =
            listOf(
                testAktivitetIkkeMuligPeriode(
                    fom = LocalDate.of(2018, 1, 1),
                    tom = LocalDate.of(2018, 1, 3),
                ),
                testAktivitetIkkeMuligPeriode(
                    fom = LocalDate.of(2018, 1, 5),
                    tom = LocalDate.of(2018, 1, 9),
                ),
                testAktivitetIkkeMuligPeriode(
                    fom = LocalDate.of(2018, 1, 10),
                    tom = LocalDate.of(2018, 1, 11),
                ),
                testAktivitetIkkeMuligPeriode(
                    fom = LocalDate.of(2018, 1, 15),
                    tom = LocalDate.of(2018, 1, 20),
                ),
            )

        val payload = testPeriodeRulePayload(perioder = perioder)

        val (result) = PeriodeRules(payload).execute()

        assertEquals(result.treeResult.status, RuleStatus.INVALID)
        assertPath(
            result.rulePath,
            listOf(
                PeriodeRule.PERIODER_MANGLER to false,
                PeriodeRule.FRADATO_ETTER_TILDATO to false,
                PeriodeRule.OVERLAPPENDE_PERIODER to false,
                PeriodeRule.OPPHOLD_MELLOM_PERIODER to true,
            ),
        )

        assertEquals(result.ruleInputs, mapOf("perioder" to perioder))

        assertEquals(result.treeResult.ruleOutcome, PeriodeRule.Outcomes.OPPHOLD_MELLOM_PERIODER)
    }

    @Test
    fun `Ikke definert periode, Status INVALID`() {
        val perioder =
            listOf(
                SykmeldingPeriode.Ugyldig(
                    fom = LocalDate.of(2018, 2, 1),
                    tom = LocalDate.of(2018, 2, 2),
                )
            )

        val payload = testPeriodeRulePayload(perioder = perioder)

        val (result) = PeriodeRules(payload).execute()

        assertEquals(result.treeResult.status, RuleStatus.INVALID)
        assertPath(
            result.rulePath,
            listOf(
                PeriodeRule.PERIODER_MANGLER to false,
                PeriodeRule.FRADATO_ETTER_TILDATO to false,
                PeriodeRule.OVERLAPPENDE_PERIODER to false,
                PeriodeRule.OPPHOLD_MELLOM_PERIODER to false,
                PeriodeRule.IKKE_DEFINERT_PERIODE to true,
            ),
        )

        assertEquals(result.ruleInputs, mapOf("perioder" to perioder))

        assertEquals(result.treeResult.ruleOutcome, PeriodeRule.Outcomes.IKKE_DEFINERT_PERIODE)
    }

    @Test
    fun `BehandlingsDato etter mottatDato, Status INVALID`() {
        testAktivitetIkkeMuligPeriode()

        val payload =
            testPeriodeRulePayload(
                behandletTidspunkt = LocalDateTime.now().plusDays(2),
                receivedDate = LocalDateTime.now(),
                perioder =
                    listOf(
                        testAktivitetIkkeMuligPeriode(
                            fom = LocalDate.now(),
                            tom = LocalDate.now().plusDays(15),
                        )
                    ),
            )

        val (result) = PeriodeRules(payload).execute()

        assertEquals(result.treeResult.status, RuleStatus.INVALID)
        assertPath(
            result.rulePath,
            listOf(
                PeriodeRule.PERIODER_MANGLER to false,
                PeriodeRule.FRADATO_ETTER_TILDATO to false,
                PeriodeRule.OVERLAPPENDE_PERIODER to false,
                PeriodeRule.OPPHOLD_MELLOM_PERIODER to false,
                PeriodeRule.IKKE_DEFINERT_PERIODE to false,
                PeriodeRule.BEHANDLINGSDATO_ETTER_MOTTATTDATO to true,
            ),
        )

        assertEquals(
            result.ruleInputs,
            mapOf("perioder" to payload.perioder, "behandslingsDatoEtterMottatDato" to true),
        )

        assertEquals(
            result.treeResult.ruleOutcome,
            PeriodeRule.Outcomes.BEHANDLINGSDATO_ETTER_MOTTATTDATO,
        )
    }

    @Test
    fun `Avvendte kombinert med annen type periode, Status INVALID`() {
        val perioder =
            listOf(
                testAvventendePeriode(
                    fom = LocalDate.now(),
                    tom = LocalDate.now().plusDays(5),
                    avventendeInnspill = "Bør gå minst mulig på jobb",
                ),
                testAktivitetIkkeMuligPeriode(
                    fom = LocalDate.now().plusDays(6),
                    tom = LocalDate.now().plusDays(10),
                ),
            )

        val payload = testPeriodeRulePayload(perioder = perioder)

        val (result) = PeriodeRules(payload).execute()

        assertEquals(result.treeResult.status, RuleStatus.INVALID)
        assertPath(
            result.rulePath,
            listOf(
                PeriodeRule.PERIODER_MANGLER to false,
                PeriodeRule.FRADATO_ETTER_TILDATO to false,
                PeriodeRule.OVERLAPPENDE_PERIODER to false,
                PeriodeRule.OPPHOLD_MELLOM_PERIODER to false,
                PeriodeRule.IKKE_DEFINERT_PERIODE to false,
                PeriodeRule.BEHANDLINGSDATO_ETTER_MOTTATTDATO to false,
                PeriodeRule.AVVENTENDE_SYKMELDING_KOMBINERT to true,
            ),
        )

        assertEquals(
            result.ruleInputs,
            mapOf(
                "perioder" to perioder,
                "behandslingsDatoEtterMottatDato" to false,
                "antallAvventende" to 1,
                "antallPerioder" to 2,
            ),
        )

        assertEquals(
            result.treeResult.ruleOutcome,
            PeriodeRule.Outcomes.AVVENTENDE_SYKMELDING_KOMBINERT,
        )
    }

    @Test
    fun `Manglende innstill til arbeidsgiver, Status INVALID`() {
        val perioder =
            listOf(
                testAvventendePeriode(
                    fom = LocalDate.now(),
                    tom = LocalDate.now().plusDays(5),
                    avventendeInnspill = "      ",
                )
            )

        val payload = testPeriodeRulePayload(perioder = perioder)

        val (result) = PeriodeRules(payload).execute()

        assertEquals(result.treeResult.status, RuleStatus.INVALID)
        assertPath(
            result.rulePath,
            listOf(
                PeriodeRule.PERIODER_MANGLER to false,
                PeriodeRule.FRADATO_ETTER_TILDATO to false,
                PeriodeRule.OVERLAPPENDE_PERIODER to false,
                PeriodeRule.OPPHOLD_MELLOM_PERIODER to false,
                PeriodeRule.IKKE_DEFINERT_PERIODE to false,
                PeriodeRule.BEHANDLINGSDATO_ETTER_MOTTATTDATO to false,
                PeriodeRule.AVVENTENDE_SYKMELDING_KOMBINERT to false,
                PeriodeRule.MANGLENDE_INNSPILL_TIL_ARBEIDSGIVER to true,
            ),
        )

        assertEquals(
            result.ruleInputs,
            mapOf(
                "perioder" to perioder,
                "behandslingsDatoEtterMottatDato" to false,
                "antallAvventende" to 1,
                "antallPerioder" to 1,
                "manglendeInnspillArbeidsgiver" to true,
            ),
        )

        assertEquals(
            result.treeResult.ruleOutcome,
            PeriodeRule.Outcomes.MANGLENDE_INNSPILL_TIL_ARBEIDSGIVER,
        )
    }

    @Test
    fun `Avventende over 16 dager, Status INVALID`() {
        val perioder =
            listOf(
                testAvventendePeriode(
                    fom = LocalDate.now(),
                    tom = LocalDate.now().plusDays(17),
                    avventendeInnspill = "Bør gå minst mulig på jobb",
                )
            )
        val payload = testPeriodeRulePayload(perioder = perioder)

        val (result) = PeriodeRules(payload).execute()

        assertEquals(result.treeResult.status, RuleStatus.INVALID)
        assertPath(
            result.rulePath,
            listOf(
                PeriodeRule.PERIODER_MANGLER to false,
                PeriodeRule.FRADATO_ETTER_TILDATO to false,
                PeriodeRule.OVERLAPPENDE_PERIODER to false,
                PeriodeRule.OPPHOLD_MELLOM_PERIODER to false,
                PeriodeRule.IKKE_DEFINERT_PERIODE to false,
                PeriodeRule.BEHANDLINGSDATO_ETTER_MOTTATTDATO to false,
                PeriodeRule.AVVENTENDE_SYKMELDING_KOMBINERT to false,
                PeriodeRule.MANGLENDE_INNSPILL_TIL_ARBEIDSGIVER to false,
                PeriodeRule.AVVENTENDE_SYKMELDING_OVER_16_DAGER to true,
            ),
        )

        assertEquals(
            result.ruleInputs,
            mapOf(
                "perioder" to perioder,
                "behandslingsDatoEtterMottatDato" to false,
                "antallAvventende" to 1,
                "antallPerioder" to 1,
                "manglendeInnspillArbeidsgiver" to false,
                "avventendeOver16Dager" to true,
            ),
        )

        assertEquals(
            result.treeResult.ruleOutcome,
            PeriodeRule.Outcomes.AVVENTENDE_SYKMELDING_OVER_16_DAGER,
        )
    }

    @Test
    fun `For mange behandlingsdager pr uke, Status INVALID`() {
        val perioder =
            listOf(
                testBehandlingsdagerPeriode(
                    fom = LocalDate.now(),
                    tom = LocalDate.now(),
                    behandlingsdager = 2,
                )
            )

        val payload = testPeriodeRulePayload(perioder = perioder)

        val (result) = PeriodeRules(payload).execute()

        assertEquals(result.treeResult.status, RuleStatus.INVALID)
        assertPath(
            result.rulePath,
            listOf(
                PeriodeRule.PERIODER_MANGLER to false,
                PeriodeRule.FRADATO_ETTER_TILDATO to false,
                PeriodeRule.OVERLAPPENDE_PERIODER to false,
                PeriodeRule.OPPHOLD_MELLOM_PERIODER to false,
                PeriodeRule.IKKE_DEFINERT_PERIODE to false,
                PeriodeRule.BEHANDLINGSDATO_ETTER_MOTTATTDATO to false,
                PeriodeRule.AVVENTENDE_SYKMELDING_KOMBINERT to false,
                PeriodeRule.MANGLENDE_INNSPILL_TIL_ARBEIDSGIVER to false,
                PeriodeRule.AVVENTENDE_SYKMELDING_OVER_16_DAGER to false,
                PeriodeRule.FOR_MANGE_BEHANDLINGSDAGER_PER_UKE to true,
            ),
        )

        assertEquals(
            result.ruleInputs,
            mapOf(
                "perioder" to perioder,
                "behandslingsDatoEtterMottatDato" to false,
                "antallAvventende" to 0,
                "antallPerioder" to 1,
                "manglendeInnspillArbeidsgiver" to false,
                "avventendeOver16Dager" to false,
                "forMangeBehandlingsDagerPrUke" to true,
            ),
        )

        assertEquals(
            result.treeResult.ruleOutcome,
            PeriodeRule.Outcomes.FOR_MANGE_BEHANDLINGSDAGER_PER_UKE,
        )
    }

    @Test
    fun `Gradert over 99 prosent, Status INVALID`() {
        val perioder =
            listOf(testGradertPeriode(fom = LocalDate.now(), tom = LocalDate.now(), grad = 100))

        val payload = testPeriodeRulePayload(perioder = perioder)

        val (result) = PeriodeRules(payload).execute()

        assertEquals(result.treeResult.status, RuleStatus.INVALID)
        assertPath(
            result.rulePath,
            listOf(
                PeriodeRule.PERIODER_MANGLER to false,
                PeriodeRule.FRADATO_ETTER_TILDATO to false,
                PeriodeRule.OVERLAPPENDE_PERIODER to false,
                PeriodeRule.OPPHOLD_MELLOM_PERIODER to false,
                PeriodeRule.IKKE_DEFINERT_PERIODE to false,
                PeriodeRule.BEHANDLINGSDATO_ETTER_MOTTATTDATO to false,
                PeriodeRule.AVVENTENDE_SYKMELDING_KOMBINERT to false,
                PeriodeRule.MANGLENDE_INNSPILL_TIL_ARBEIDSGIVER to false,
                PeriodeRule.AVVENTENDE_SYKMELDING_OVER_16_DAGER to false,
                PeriodeRule.FOR_MANGE_BEHANDLINGSDAGER_PER_UKE to false,
                PeriodeRule.GRADERT_SYKMELDING_OVER_99_PROSENT to true,
            ),
        )

        assertEquals(
            result.ruleInputs,
            mapOf(
                "perioder" to perioder,
                "behandslingsDatoEtterMottatDato" to false,
                "antallAvventende" to 0,
                "antallPerioder" to 1,
                "manglendeInnspillArbeidsgiver" to false,
                "avventendeOver16Dager" to false,
                "forMangeBehandlingsDagerPrUke" to false,
                "gradertOver99Prosent" to true,
            ),
        )

        assertEquals(
            result.treeResult.ruleOutcome,
            PeriodeRule.Outcomes.GRADERT_SYKMELDING_OVER_99_PROSENT,
        )
    }

    @Test
    fun `Inneholder behandlingsdager, Status MANUAL_PROCESSING`() {
        val perioder =
            listOf(
                testBehandlingsdagerPeriode(
                    fom = LocalDate.now(),
                    tom = LocalDate.now(),
                    behandlingsdager = 1,
                )
            )

        val payload = testPeriodeRulePayload(perioder = perioder)

        val (result) = PeriodeRules(payload).execute()

        assertEquals(result.treeResult.status, RuleStatus.MANUAL_PROCESSING)
        assertPath(
            result.rulePath,
            listOf(
                PeriodeRule.PERIODER_MANGLER to false,
                PeriodeRule.FRADATO_ETTER_TILDATO to false,
                PeriodeRule.OVERLAPPENDE_PERIODER to false,
                PeriodeRule.OPPHOLD_MELLOM_PERIODER to false,
                PeriodeRule.IKKE_DEFINERT_PERIODE to false,
                PeriodeRule.BEHANDLINGSDATO_ETTER_MOTTATTDATO to false,
                PeriodeRule.AVVENTENDE_SYKMELDING_KOMBINERT to false,
                PeriodeRule.MANGLENDE_INNSPILL_TIL_ARBEIDSGIVER to false,
                PeriodeRule.AVVENTENDE_SYKMELDING_OVER_16_DAGER to false,
                PeriodeRule.FOR_MANGE_BEHANDLINGSDAGER_PER_UKE to false,
                PeriodeRule.GRADERT_SYKMELDING_OVER_99_PROSENT to false,
                PeriodeRule.SYKMELDING_MED_BEHANDLINGSDAGER to true,
            ),
        )

        assertEquals(
            result.ruleInputs,
            mapOf(
                "perioder" to perioder,
                "behandslingsDatoEtterMottatDato" to false,
                "antallAvventende" to 0,
                "antallPerioder" to 1,
                "manglendeInnspillArbeidsgiver" to false,
                "avventendeOver16Dager" to false,
                "forMangeBehandlingsDagerPrUke" to false,
                "gradertOver99Prosent" to false,
                "inneholderBehandlingsDager" to true,
            ),
        )

        assertEquals(
            result.treeResult.ruleOutcome,
            PeriodeRule.Outcomes.SYKMELDING_MED_BEHANDLINGSDAGER,
        )
    }
}

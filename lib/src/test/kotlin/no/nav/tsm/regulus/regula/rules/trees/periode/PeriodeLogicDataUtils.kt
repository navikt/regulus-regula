package no.nav.tsm.regulus.regula.rules.trees.periode

import java.time.LocalDate
import java.time.LocalDateTime
import no.nav.tsm.regulus.regula.payload.SykmeldingPeriode

internal fun testAktivitetIkkeMuligPeriode(
    fom: LocalDate = LocalDate.now(),
    tom: LocalDate = LocalDate.now().plusDays(15),
) = SykmeldingPeriode.AktivitetIkkeMulig(fom = fom, tom = tom)

internal fun testBehandlingsdagerPeriode(
    fom: LocalDate = LocalDate.now(),
    tom: LocalDate = LocalDate.now().plusDays(15),
    behandlingsdager: Int,
) = SykmeldingPeriode.Behandlingsdager(fom = fom, tom = tom, behandlingsdager = behandlingsdager)

internal fun testGradertPeriode(
    fom: LocalDate = LocalDate.now(),
    tom: LocalDate = LocalDate.now().plusDays(15),
    grad: Int,
) = SykmeldingPeriode.Gradert(fom = fom, tom = tom, grad = grad)

internal fun testAvventendePeriode(
    fom: LocalDate = LocalDate.now(),
    tom: LocalDate = LocalDate.now().plusDays(15),
    avventendeInnspill: String,
) =
    SykmeldingPeriode.Avventende(
        fom = fom,
        tom = tom,
        avventendeInnspillTilArbeidsgiver = avventendeInnspill,
    )

internal fun testPeriodeRulePayload(
    perioder: List<SykmeldingPeriode> = listOf(testAktivitetIkkeMuligPeriode()),
    behandletTidspunkt: LocalDateTime = LocalDateTime.now(),
    receivedDate: LocalDateTime = LocalDateTime.now(),
) =
    PeriodeRulePayload(
        sykmeldingId = "sykmeldingId",
        perioder = perioder,
        behandletTidspunkt = behandletTidspunkt,
        receivedDate = receivedDate,
    )

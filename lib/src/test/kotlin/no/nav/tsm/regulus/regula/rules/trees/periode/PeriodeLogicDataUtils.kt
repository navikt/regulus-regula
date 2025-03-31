package no.nav.tsm.regulus.regula.rules.trees.periode

import java.time.LocalDate
import java.time.LocalDateTime
import no.nav.tsm.regulus.regula.payload.Aktivitet

internal fun testAktivitetIkkeMuligPeriode(
    fom: LocalDate = LocalDate.now(),
    tom: LocalDate = LocalDate.now().plusDays(15),
) = Aktivitet.IkkeMulig(fom = fom, tom = tom)

internal fun testBehandlingsdagerPeriode(
    fom: LocalDate = LocalDate.now(),
    tom: LocalDate = LocalDate.now().plusDays(15),
    behandlingsdager: Int,
) = Aktivitet.Behandlingsdager(fom = fom, tom = tom, behandlingsdager = behandlingsdager)

internal fun testGradertPeriode(
    fom: LocalDate = LocalDate.now(),
    tom: LocalDate = LocalDate.now().plusDays(15),
    grad: Int,
) = Aktivitet.Gradert(fom = fom, tom = tom, grad = grad)

internal fun testAvventendePeriode(
    fom: LocalDate = LocalDate.now(),
    tom: LocalDate = LocalDate.now().plusDays(15),
    avventendeInnspill: String,
) =
    Aktivitet.Avventende(
        fom = fom,
        tom = tom,
        avventendeInnspillTilArbeidsgiver = avventendeInnspill,
    )

internal fun testPeriodeRulePayload(
    perioder: List<Aktivitet> = listOf(testAktivitetIkkeMuligPeriode()),
    behandletTidspunkt: LocalDateTime = LocalDateTime.now(),
    receivedDate: LocalDateTime = LocalDateTime.now(),
) =
    PeriodeRulePayload(
        sykmeldingId = "sykmeldingId",
        aktivitet = perioder,
        behandletTidspunkt = behandletTidspunkt,
        mottattDato = receivedDate,
    )

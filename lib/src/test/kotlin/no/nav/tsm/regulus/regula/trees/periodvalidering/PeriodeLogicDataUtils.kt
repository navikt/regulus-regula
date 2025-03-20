package no.nav.tsm.regulus.regula.trees.periodvalidering

import java.time.LocalDate
import java.time.LocalDateTime

fun testAktivitetIkkeMuligPeriode(
    fom: LocalDate = LocalDate.now(),
    tom: LocalDate = LocalDate.now().plusDays(15),
) =
    Periode(
        fom = fom,
        tom = tom,
        aktivitetIkkeMulig = AktivitetIkkeMulig(medisinskArsak = null, arbeidsrelatertArsak = null),
        avventendeInnspillTilArbeidsgiver = null,
        behandlingsdager = null,
        gradert = null,
        reisetilskudd = false,
    )

fun testBehandlingsdagerPeriode(
    fom: LocalDate = LocalDate.now(),
    tom: LocalDate = LocalDate.now().plusDays(15),
    behandlingsdager: Int,
) =
    Periode(
        fom = fom,
        tom = tom,
        aktivitetIkkeMulig = null,
        avventendeInnspillTilArbeidsgiver = null,
        behandlingsdager = behandlingsdager,
        gradert = null,
        reisetilskudd = false,
    )

fun testGradertPeriode(
    fom: LocalDate = LocalDate.now(),
    tom: LocalDate = LocalDate.now().plusDays(15),
    gradert: Int,
) =
    Periode(
        fom = fom,
        tom = tom,
        aktivitetIkkeMulig = null,
        avventendeInnspillTilArbeidsgiver = null,
        behandlingsdager = null,
        gradert = Gradert(grad = gradert, reisetilskudd = false),
        reisetilskudd = false,
    )

fun testAvventendePeriode(
    fom: LocalDate = LocalDate.now(),
    tom: LocalDate = LocalDate.now().plusDays(15),
    avventendeInnspill: String,
) =
    Periode(
        fom = fom,
        tom = tom,
        aktivitetIkkeMulig = null,
        avventendeInnspillTilArbeidsgiver = avventendeInnspill,
        behandlingsdager = null,
        gradert = null,
        reisetilskudd = false,
    )

fun testPeriodLogicRulePayload(
    perioder: List<Periode> = listOf(testAktivitetIkkeMuligPeriode()),
    behandletTidspunkt: LocalDateTime = LocalDateTime.now(),
    receivedDate: LocalDateTime = LocalDateTime.now(),
) =
    PeriodLogicRulePayload(
        sykmeldingId = "sykmeldingId",
        perioder = perioder,
        behandletTidspunkt = behandletTidspunkt,
        receivedDate = receivedDate,
    )

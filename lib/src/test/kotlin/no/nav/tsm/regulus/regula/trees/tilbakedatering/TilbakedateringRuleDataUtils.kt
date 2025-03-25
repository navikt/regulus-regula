package no.nav.tsm.regulus.regula.trees.tilbakedatering

import java.time.LocalDate
import java.time.LocalDateTime
import no.nav.tsm.regulus.regula.payload.SykmeldingPeriode

fun testTilbakedateringRulePayload(
    perioder: List<SykmeldingPeriode> =
        listOf(
            SykmeldingPeriode.AktivitetIkkeMulig(
                fom = LocalDate.now(),
                tom = LocalDate.now().plusDays(15),
            )
        ),
    tidligereSykmeldinger: List<TidligereSykmelding> = listOf(),
    hoveddiagnoseSystem: String? = null,
    signaturdato: LocalDateTime = LocalDateTime.now(),
    begrunnelseIkkeKontakt: String? = null,
    // Can't be bool, TODO fix it
    forlengelse: Boolean? = null,
    dagerForArbeidsgiverperiodeCheck: List<LocalDate> = listOf(),
    startdato: LocalDate? = null,
) =
    TilbakedateringRulePayload(
        sykmeldingId = "foo-bar-baz",
        signaturdato = signaturdato,
        perioder = perioder,
        startdato = startdato,
        hoveddiagnoseSystem = hoveddiagnoseSystem,
        tidligereSykmeldinger = tidligereSykmeldinger,
        begrunnelseIkkeKontakt = begrunnelseIkkeKontakt,
        dagerForArbeidsgiverperiodeCheck = dagerForArbeidsgiverperiodeCheck,
        forlengelse = forlengelse,
    )

fun testPeriode(fomOffset: Long, tomOffset: Long): List<SykmeldingPeriode> {
    return listOf(
        SykmeldingPeriode.AktivitetIkkeMulig(
            fom = LocalDate.now().plusDays(fomOffset),
            tom = LocalDate.now().plusDays(tomOffset),
        )
    )
}

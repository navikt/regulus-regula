package no.nav.tsm.regulus.regula.rules.trees.tilbakedatering

import java.time.LocalDate
import java.time.LocalDateTime
import no.nav.helse.diagnosekoder.Diagnosekoder
import no.nav.tsm.regulus.regula.payload.Aktivitet
import no.nav.tsm.regulus.regula.payload.Diagnose
import no.nav.tsm.regulus.regula.payload.TidligereSykmelding
import no.nav.tsm.regulus.regula.payload.TidligereSykmeldingAktivitet

internal fun testTilbakedateringRulePayload(
    perioder: List<Aktivitet> =
        listOf(Aktivitet.IkkeMulig(fom = LocalDate.now(), tom = LocalDate.now().plusDays(15))),
    tidligereSykmeldinger: List<TidligereSykmelding> = listOf(),
    hoveddiagnose: Diagnose? = Diagnose(system = Diagnosekoder.ICPC2_CODE, kode = "X01"),
    signaturdato: LocalDateTime = LocalDateTime.now(),
    begrunnelseIkkeKontakt: String? = null,
    startdato: LocalDate? = null,
) =
    TilbakedateringRulePayload(
        sykmeldingId = "foo-bar-baz",
        signaturdato = signaturdato,
        aktivitet = perioder,
        hoveddiagnose = hoveddiagnose,
        tidligereSykmeldinger = tidligereSykmeldinger,
        begrunnelseIkkeKontakt = begrunnelseIkkeKontakt,
    )

internal fun testAktivitet(fomOffset: Long, tomOffset: Long): List<Aktivitet> {
    return listOf(
        Aktivitet.IkkeMulig(
            fom = LocalDate.now().plusDays(fomOffset),
            tom = LocalDate.now().plusDays(tomOffset),
        )
    )
}

internal fun testTidligereAktivitet(
    fomOffset: Long,
    tomOffset: Long,
): List<TidligereSykmeldingAktivitet> {
    return listOf(
        TidligereSykmeldingAktivitet.IkkeMulig(
            fom = LocalDate.now().plusDays(fomOffset),
            tom = LocalDate.now().plusDays(tomOffset),
        )
    )
}

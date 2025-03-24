package no.nav.tsm.regulus.regula.trees.tilbakedatering

import java.time.LocalDate
import java.time.LocalDateTime
import no.nav.tsm.regulus.regula.payload.FomTom

fun testTilbakedateringRulePayload(
    perioder: List<FomTom> =
        listOf(FomTom(fom = LocalDate.now(), tom = LocalDate.now().plusDays(15))),
    hoveddiagnoseSystem: String? = null,
    signaturdato: LocalDateTime = LocalDateTime.now(),
    ettersendingAv: String? = null,
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
        begrunnelseIkkeKontakt = begrunnelseIkkeKontakt,
        ettersendingAv = ettersendingAv,
        dagerForArbeidsgiverperiodeCheck = dagerForArbeidsgiverperiodeCheck,
        forlengelse = forlengelse,
    )

fun testPeriode(fomOffset: Long, tomOffset: Long): List<FomTom> =
    listOf(
        FomTom(fom = LocalDate.now().plusDays(fomOffset), tom = LocalDate.now().plusDays(tomOffset))
    )

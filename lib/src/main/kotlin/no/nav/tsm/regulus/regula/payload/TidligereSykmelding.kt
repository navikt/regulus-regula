package no.nav.tsm.regulus.regula.payload

data class TidligereSykmelding(
    val sykmeldingId: String,
    val perioder: List<SykmeldingPeriode>,
    val hoveddiagnose: Diagnose?,
)

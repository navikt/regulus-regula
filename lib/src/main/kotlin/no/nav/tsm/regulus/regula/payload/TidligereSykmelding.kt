package no.nav.tsm.regulus.regula.payload

data class TidligereSykmelding(
    val sykmeldingId: String,
    val aktivitet: List<Aktivitet>,
    val hoveddiagnose: Diagnose?,
)

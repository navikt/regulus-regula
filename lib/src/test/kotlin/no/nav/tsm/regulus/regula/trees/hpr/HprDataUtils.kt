package no.nav.tsm.regulus.regula.trees.hpr

internal enum class BehandlerScenarios {
    INAKTIV_LEGE
}

internal fun testBehandler(variant: BehandlerScenarios): Behandler =
    when (variant) {
        BehandlerScenarios.INAKTIV_LEGE ->
            Behandler(
                godkjenninger =
                    listOf(
                        Godkjenning(
                            autorisasjon = Kode(aktiv = false, oid = 7704, verdi = "1"),
                            helsepersonellkategori = Kode(aktiv = true, oid = 0, verdi = "LE"),
                            tillegskompetanse = null,
                        )
                    )
            )
    }

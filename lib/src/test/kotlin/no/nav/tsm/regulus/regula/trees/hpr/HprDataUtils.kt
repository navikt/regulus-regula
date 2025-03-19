package no.nav.tsm.regulus.regula.trees.hpr

import java.time.LocalDate

internal enum class BehandlerScenarios {
    INAKTIV_LEGE,
    UGYLDIG_AUTORISASJON,
    AKTIV_LEGE,
    AKTIV_MANUELLTERAPEUT,
    AKTIV_TANNLEGE,
    AKTIV_FYSIOTERAPEUT,
    AKTIV_FYSIOTERAPEUT_MED_TILLEGGSKOMPETANSE,
    AKTIV_KIROPRAKTOR,
    AKTIV_KIROPRAKTOR_MED_TILLEGGSKOMPETANSE,
    AKTIV_FYSIOTERAPEUT_MED_FEIL_TILLEGGSKOMPETANSE_TYPE,
    AKTIV_FYSIOTERAPEUT_MED_INAKTIV_TILLEGGSKOMPETANSE,
    AKTIV_KIROPRAKTOR_MED_TILLEGGSKOMPETANSE_OG_ANNEN_HELSEPERSONELLKATEGORI,
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

        BehandlerScenarios.UGYLDIG_AUTORISASJON ->
            Behandler(
                godkjenninger =
                    listOf(
                        Godkjenning(
                            autorisasjon = Kode(aktiv = true, oid = 7704, verdi = "foo-bar"),
                            helsepersonellkategori = Kode(aktiv = true, oid = 0, verdi = "LE"),
                            tillegskompetanse = null,
                        )
                    )
            )

        BehandlerScenarios.AKTIV_LEGE ->
            Behandler(
                godkjenninger =
                    listOf(
                        Godkjenning(
                            autorisasjon = Kode(aktiv = true, oid = 7704, verdi = "1"),
                            helsepersonellkategori = Kode(aktiv = true, oid = 0, verdi = "LE"),
                            tillegskompetanse = null,
                        )
                    )
            )

        BehandlerScenarios.AKTIV_MANUELLTERAPEUT ->
            Behandler(
                godkjenninger =
                    listOf(
                        Godkjenning(
                            autorisasjon = Kode(aktiv = true, oid = 7704, verdi = "1"),
                            helsepersonellkategori = Kode(aktiv = true, oid = 0, verdi = "MT"),
                            tillegskompetanse = null,
                        )
                    )
            )

        BehandlerScenarios.AKTIV_TANNLEGE ->
            Behandler(
                godkjenninger =
                    listOf(
                        Godkjenning(
                            autorisasjon = Kode(aktiv = true, oid = 7704, verdi = "1"),
                            helsepersonellkategori = Kode(aktiv = true, oid = 0, verdi = "TL"),
                            tillegskompetanse = null,
                        )
                    )
            )

        BehandlerScenarios.AKTIV_FYSIOTERAPEUT ->
            Behandler(
                godkjenninger =
                    listOf(
                        Godkjenning(
                            autorisasjon = Kode(aktiv = true, oid = 7704, verdi = "1"),
                            helsepersonellkategori = Kode(aktiv = true, oid = 0, verdi = "FT"),
                            tillegskompetanse = null,
                        )
                    )
            )

        BehandlerScenarios.AKTIV_FYSIOTERAPEUT_MED_TILLEGGSKOMPETANSE ->
            Behandler(
                godkjenninger =
                    listOf(
                        Godkjenning(
                            autorisasjon = Kode(aktiv = true, oid = 7704, verdi = "1"),
                            helsepersonellkategori = Kode(aktiv = true, oid = 0, verdi = "FT"),
                            tillegskompetanse =
                                listOf(
                                    Tilleggskompetanse(
                                        avsluttetStatus = null,
                                        eTag = null,
                                        gyldig =
                                            Periode(
                                                fra = LocalDate.of(2000, 1, 1).atStartOfDay(),
                                                til = null,
                                            ),
                                        id = null,
                                        type = Kode(aktiv = true, oid = 7702, verdi = "1"),
                                    )
                                ),
                        )
                    )
            )

        BehandlerScenarios.AKTIV_KIROPRAKTOR ->
            Behandler(
                godkjenninger =
                    listOf(
                        Godkjenning(
                            autorisasjon = Kode(aktiv = true, oid = 7704, verdi = "1"),
                            helsepersonellkategori = Kode(aktiv = true, oid = 0, verdi = "KI"),
                            tillegskompetanse = null,
                        )
                    )
            )

        BehandlerScenarios.AKTIV_KIROPRAKTOR_MED_TILLEGGSKOMPETANSE ->
            Behandler(
                godkjenninger =
                    listOf(
                        Godkjenning(
                            autorisasjon = Kode(aktiv = true, oid = 7704, verdi = "1"),
                            helsepersonellkategori = Kode(aktiv = true, oid = 0, verdi = "KI"),
                            tillegskompetanse =
                                listOf(
                                    Tilleggskompetanse(
                                        avsluttetStatus = null,
                                        eTag = null,
                                        gyldig =
                                            Periode(
                                                fra = LocalDate.of(2000, 1, 1).atStartOfDay(),
                                                til = null,
                                            ),
                                        id = null,
                                        type = Kode(aktiv = true, oid = 7702, verdi = "1"),
                                    )
                                ),
                        )
                    )
            )

        BehandlerScenarios.AKTIV_FYSIOTERAPEUT_MED_FEIL_TILLEGGSKOMPETANSE_TYPE ->
            Behandler(
                godkjenninger =
                    listOf(
                        Godkjenning(
                            autorisasjon = Kode(aktiv = true, oid = 7704, verdi = "1"),
                            helsepersonellkategori = Kode(aktiv = true, oid = 0, verdi = "FT"),
                            tillegskompetanse =
                                listOf(
                                    Tilleggskompetanse(
                                        avsluttetStatus = null,
                                        eTag = null,
                                        gyldig =
                                            Periode(
                                                fra = LocalDate.of(2000, 1, 1).atStartOfDay(),
                                                til = null,
                                            ),
                                        id = null,
                                        type =
                                            Kode(
                                                aktiv = true,
                                                oid = 7702,
                                                verdi = "2", // Wrong value, should be "1"
                                            ),
                                    )
                                ),
                        )
                    )
            )

        BehandlerScenarios.AKTIV_FYSIOTERAPEUT_MED_INAKTIV_TILLEGGSKOMPETANSE ->
            Behandler(
                godkjenninger =
                    listOf(
                        Godkjenning(
                            autorisasjon = Kode(aktiv = true, oid = 7704, verdi = "1"),
                            helsepersonellkategori = Kode(aktiv = true, oid = 0, verdi = "FT"),
                            tillegskompetanse =
                                listOf(
                                    Tilleggskompetanse(
                                        avsluttetStatus = null,
                                        eTag = null,
                                        gyldig =
                                            Periode(
                                                fra = LocalDate.of(2000, 1, 1).atStartOfDay(),
                                                til = null,
                                            ),
                                        id = null,
                                        type =
                                            Kode(
                                                aktiv = false, // Inactive
                                                oid = 7702,
                                                verdi = "1",
                                            ),
                                    )
                                ),
                        )
                    )
            )

        BehandlerScenarios
            .AKTIV_KIROPRAKTOR_MED_TILLEGGSKOMPETANSE_OG_ANNEN_HELSEPERSONELLKATEGORI ->
            Behandler(
                godkjenninger =
                    listOf(
                        Godkjenning(
                            autorisasjon = Kode(aktiv = true, oid = 7704, verdi = "1"),
                            helsepersonellkategori = Kode(aktiv = true, oid = 9060, verdi = "ET"),
                            tillegskompetanse = null,
                        ),
                        Godkjenning(
                            autorisasjon = Kode(aktiv = true, oid = 7704, verdi = "1"),
                            helsepersonellkategori = Kode(aktiv = true, oid = 9060, verdi = "KI"),
                            tillegskompetanse =
                                listOf(
                                    Tilleggskompetanse(
                                        avsluttetStatus = null,
                                        eTag = null,
                                        gyldig =
                                            Periode(
                                                fra = LocalDate.of(2015, 8, 16).atStartOfDay(),
                                                til = LocalDate.of(2059, 1, 5).atStartOfDay(),
                                            ),
                                        id = 20358,
                                        type = Kode(aktiv = true, oid = 7702, verdi = "1"),
                                    )
                                ),
                        ),
                    )
            )
    }

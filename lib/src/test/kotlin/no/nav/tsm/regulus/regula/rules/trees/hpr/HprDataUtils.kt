package no.nav.tsm.regulus.regula.rules.trees.hpr

import java.time.LocalDate
import no.nav.tsm.regulus.regula.payload.BehandlerGodkjenning
import no.nav.tsm.regulus.regula.payload.BehandlerKode
import no.nav.tsm.regulus.regula.payload.BehandlerPeriode
import no.nav.tsm.regulus.regula.payload.BehandlerTilleggskompetanse

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

internal fun testBehandlerGodkjenninger(variant: BehandlerScenarios): List<BehandlerGodkjenning> =
    when (variant) {
        BehandlerScenarios.INAKTIV_LEGE ->
            listOf(
                BehandlerGodkjenning(
                    autorisasjon = BehandlerKode(aktiv = false, oid = 7704, verdi = "1"),
                    helsepersonellkategori = BehandlerKode(aktiv = true, oid = 0, verdi = "LE"),
                    tillegskompetanse = null,
                )
            )

        BehandlerScenarios.UGYLDIG_AUTORISASJON ->
            listOf(
                BehandlerGodkjenning(
                    autorisasjon = BehandlerKode(aktiv = true, oid = 7704, verdi = "foo-bar"),
                    helsepersonellkategori = BehandlerKode(aktiv = true, oid = 0, verdi = "LE"),
                    tillegskompetanse = null,
                )
            )

        BehandlerScenarios.AKTIV_LEGE ->
            listOf(
                BehandlerGodkjenning(
                    autorisasjon = BehandlerKode(aktiv = true, oid = 7704, verdi = "1"),
                    helsepersonellkategori = BehandlerKode(aktiv = true, oid = 0, verdi = "LE"),
                    tillegskompetanse = null,
                )
            )

        BehandlerScenarios.AKTIV_MANUELLTERAPEUT ->
            listOf(
                BehandlerGodkjenning(
                    autorisasjon = BehandlerKode(aktiv = true, oid = 7704, verdi = "1"),
                    helsepersonellkategori = BehandlerKode(aktiv = true, oid = 0, verdi = "MT"),
                    tillegskompetanse = null,
                )
            )

        BehandlerScenarios.AKTIV_TANNLEGE ->
            listOf(
                BehandlerGodkjenning(
                    autorisasjon = BehandlerKode(aktiv = true, oid = 7704, verdi = "1"),
                    helsepersonellkategori = BehandlerKode(aktiv = true, oid = 0, verdi = "TL"),
                    tillegskompetanse = null,
                )
            )

        BehandlerScenarios.AKTIV_FYSIOTERAPEUT ->
            listOf(
                BehandlerGodkjenning(
                    autorisasjon = BehandlerKode(aktiv = true, oid = 7704, verdi = "1"),
                    helsepersonellkategori = BehandlerKode(aktiv = true, oid = 0, verdi = "FT"),
                    tillegskompetanse = null,
                )
            )

        BehandlerScenarios.AKTIV_FYSIOTERAPEUT_MED_TILLEGGSKOMPETANSE ->
            listOf(
                BehandlerGodkjenning(
                    autorisasjon = BehandlerKode(aktiv = true, oid = 7704, verdi = "1"),
                    helsepersonellkategori = BehandlerKode(aktiv = true, oid = 0, verdi = "FT"),
                    tillegskompetanse =
                        listOf(
                            BehandlerTilleggskompetanse(
                                avsluttetStatus = null,
                                gyldig =
                                    BehandlerPeriode(
                                        fra = LocalDate.of(2000, 1, 1).atStartOfDay(),
                                        til = null,
                                    ),
                                type = BehandlerKode(aktiv = true, oid = 7702, verdi = "1"),
                            )
                        ),
                )
            )

        BehandlerScenarios.AKTIV_KIROPRAKTOR ->
            listOf(
                BehandlerGodkjenning(
                    autorisasjon = BehandlerKode(aktiv = true, oid = 7704, verdi = "1"),
                    helsepersonellkategori = BehandlerKode(aktiv = true, oid = 0, verdi = "KI"),
                    tillegskompetanse = null,
                )
            )

        BehandlerScenarios.AKTIV_KIROPRAKTOR_MED_TILLEGGSKOMPETANSE ->
            listOf(
                BehandlerGodkjenning(
                    autorisasjon = BehandlerKode(aktiv = true, oid = 7704, verdi = "1"),
                    helsepersonellkategori = BehandlerKode(aktiv = true, oid = 0, verdi = "KI"),
                    tillegskompetanse =
                        listOf(
                            BehandlerTilleggskompetanse(
                                avsluttetStatus = null,
                                gyldig =
                                    BehandlerPeriode(
                                        fra = LocalDate.of(2000, 1, 1).atStartOfDay(),
                                        til = null,
                                    ),
                                type = BehandlerKode(aktiv = true, oid = 7702, verdi = "1"),
                            )
                        ),
                )
            )

        BehandlerScenarios.AKTIV_FYSIOTERAPEUT_MED_FEIL_TILLEGGSKOMPETANSE_TYPE ->
            listOf(
                BehandlerGodkjenning(
                    autorisasjon = BehandlerKode(aktiv = true, oid = 7704, verdi = "1"),
                    helsepersonellkategori = BehandlerKode(aktiv = true, oid = 0, verdi = "FT"),
                    tillegskompetanse =
                        listOf(
                            BehandlerTilleggskompetanse(
                                avsluttetStatus = null,
                                gyldig =
                                    BehandlerPeriode(
                                        fra = LocalDate.of(2000, 1, 1).atStartOfDay(),
                                        til = null,
                                    ),
                                type =
                                    BehandlerKode(
                                        aktiv = true,
                                        oid = 7702,
                                        verdi = "2", // Wrong value, should be "1"
                                    ),
                            )
                        ),
                )
            )

        BehandlerScenarios.AKTIV_FYSIOTERAPEUT_MED_INAKTIV_TILLEGGSKOMPETANSE ->
            listOf(
                BehandlerGodkjenning(
                    autorisasjon = BehandlerKode(aktiv = true, oid = 7704, verdi = "1"),
                    helsepersonellkategori = BehandlerKode(aktiv = true, oid = 0, verdi = "FT"),
                    tillegskompetanse =
                        listOf(
                            BehandlerTilleggskompetanse(
                                avsluttetStatus = null,
                                gyldig =
                                    BehandlerPeriode(
                                        fra = LocalDate.of(2000, 1, 1).atStartOfDay(),
                                        til = null,
                                    ),
                                type =
                                    BehandlerKode(
                                        aktiv = false, // Inactive
                                        oid = 7702,
                                        verdi = "1",
                                    ),
                            )
                        ),
                )
            )

        BehandlerScenarios
            .AKTIV_KIROPRAKTOR_MED_TILLEGGSKOMPETANSE_OG_ANNEN_HELSEPERSONELLKATEGORI ->
            listOf(
                BehandlerGodkjenning(
                    autorisasjon = BehandlerKode(aktiv = true, oid = 7704, verdi = "1"),
                    helsepersonellkategori = BehandlerKode(aktiv = true, oid = 9060, verdi = "ET"),
                    tillegskompetanse = null,
                ),
                BehandlerGodkjenning(
                    autorisasjon = BehandlerKode(aktiv = true, oid = 7704, verdi = "1"),
                    helsepersonellkategori = BehandlerKode(aktiv = true, oid = 9060, verdi = "KI"),
                    tillegskompetanse =
                        listOf(
                            BehandlerTilleggskompetanse(
                                avsluttetStatus = null,
                                gyldig =
                                    BehandlerPeriode(
                                        fra = LocalDate.of(2015, 8, 16).atStartOfDay(),
                                        til = LocalDate.of(2059, 1, 5).atStartOfDay(),
                                    ),
                                type = BehandlerKode(aktiv = true, oid = 7702, verdi = "1"),
                            )
                        ),
                ),
            )
    }

package no.nav.tsm.regulus.regula.rules.trees.hpr

import java.time.LocalDate
import no.nav.tsm.regulus.regula.payload.SykmelderGodkjenning
import no.nav.tsm.regulus.regula.payload.SykmelderKode
import no.nav.tsm.regulus.regula.payload.SykmelderPeriode
import no.nav.tsm.regulus.regula.payload.SykmelderTilleggskompetanse

internal enum class SykmelderScenarios {
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

internal fun testSykmelderGodkjenninger(variant: SykmelderScenarios): List<SykmelderGodkjenning> =
    when (variant) {
        SykmelderScenarios.INAKTIV_LEGE ->
            listOf(
                SykmelderGodkjenning(
                    autorisasjon = SykmelderKode(aktiv = false, oid = 7704, verdi = "1"),
                    helsepersonellkategori = SykmelderKode(aktiv = true, oid = 0, verdi = "LE"),
                    tillegskompetanse = null,
                )
            )

        SykmelderScenarios.UGYLDIG_AUTORISASJON ->
            listOf(
                SykmelderGodkjenning(
                    autorisasjon = SykmelderKode(aktiv = true, oid = 7704, verdi = "foo-bar"),
                    helsepersonellkategori = SykmelderKode(aktiv = true, oid = 0, verdi = "LE"),
                    tillegskompetanse = null,
                )
            )

        SykmelderScenarios.AKTIV_LEGE ->
            listOf(
                SykmelderGodkjenning(
                    autorisasjon = SykmelderKode(aktiv = true, oid = 7704, verdi = "1"),
                    helsepersonellkategori = SykmelderKode(aktiv = true, oid = 0, verdi = "LE"),
                    tillegskompetanse = null,
                )
            )

        SykmelderScenarios.AKTIV_MANUELLTERAPEUT ->
            listOf(
                SykmelderGodkjenning(
                    autorisasjon = SykmelderKode(aktiv = true, oid = 7704, verdi = "1"),
                    helsepersonellkategori = SykmelderKode(aktiv = true, oid = 0, verdi = "MT"),
                    tillegskompetanse = null,
                )
            )

        SykmelderScenarios.AKTIV_TANNLEGE ->
            listOf(
                SykmelderGodkjenning(
                    autorisasjon = SykmelderKode(aktiv = true, oid = 7704, verdi = "1"),
                    helsepersonellkategori = SykmelderKode(aktiv = true, oid = 0, verdi = "TL"),
                    tillegskompetanse = null,
                )
            )

        SykmelderScenarios.AKTIV_FYSIOTERAPEUT ->
            listOf(
                SykmelderGodkjenning(
                    autorisasjon = SykmelderKode(aktiv = true, oid = 7704, verdi = "1"),
                    helsepersonellkategori = SykmelderKode(aktiv = true, oid = 0, verdi = "FT"),
                    tillegskompetanse = null,
                )
            )

        SykmelderScenarios.AKTIV_FYSIOTERAPEUT_MED_TILLEGGSKOMPETANSE ->
            listOf(
                SykmelderGodkjenning(
                    autorisasjon = SykmelderKode(aktiv = true, oid = 7704, verdi = "1"),
                    helsepersonellkategori = SykmelderKode(aktiv = true, oid = 0, verdi = "FT"),
                    tillegskompetanse =
                        listOf(
                            SykmelderTilleggskompetanse(
                                avsluttetStatus = null,
                                gyldig =
                                    SykmelderPeriode(
                                        fra = LocalDate.of(2000, 1, 1).atStartOfDay(),
                                        til = null,
                                    ),
                                type = SykmelderKode(aktiv = true, oid = 7702, verdi = "1"),
                            )
                        ),
                )
            )

        SykmelderScenarios.AKTIV_KIROPRAKTOR ->
            listOf(
                SykmelderGodkjenning(
                    autorisasjon = SykmelderKode(aktiv = true, oid = 7704, verdi = "1"),
                    helsepersonellkategori = SykmelderKode(aktiv = true, oid = 0, verdi = "KI"),
                    tillegskompetanse = null,
                )
            )

        SykmelderScenarios.AKTIV_KIROPRAKTOR_MED_TILLEGGSKOMPETANSE ->
            listOf(
                SykmelderGodkjenning(
                    autorisasjon = SykmelderKode(aktiv = true, oid = 7704, verdi = "1"),
                    helsepersonellkategori = SykmelderKode(aktiv = true, oid = 0, verdi = "KI"),
                    tillegskompetanse =
                        listOf(
                            SykmelderTilleggskompetanse(
                                avsluttetStatus = null,
                                gyldig =
                                    SykmelderPeriode(
                                        fra = LocalDate.of(2000, 1, 1).atStartOfDay(),
                                        til = null,
                                    ),
                                type = SykmelderKode(aktiv = true, oid = 7702, verdi = "1"),
                            )
                        ),
                )
            )

        SykmelderScenarios.AKTIV_FYSIOTERAPEUT_MED_FEIL_TILLEGGSKOMPETANSE_TYPE ->
            listOf(
                SykmelderGodkjenning(
                    autorisasjon = SykmelderKode(aktiv = true, oid = 7704, verdi = "1"),
                    helsepersonellkategori = SykmelderKode(aktiv = true, oid = 0, verdi = "FT"),
                    tillegskompetanse =
                        listOf(
                            SykmelderTilleggskompetanse(
                                avsluttetStatus = null,
                                gyldig =
                                    SykmelderPeriode(
                                        fra = LocalDate.of(2000, 1, 1).atStartOfDay(),
                                        til = null,
                                    ),
                                type =
                                    SykmelderKode(
                                        aktiv = true,
                                        oid = 7702,
                                        verdi = "2", // Wrong value, should be "1"
                                    ),
                            )
                        ),
                )
            )

        SykmelderScenarios.AKTIV_FYSIOTERAPEUT_MED_INAKTIV_TILLEGGSKOMPETANSE ->
            listOf(
                SykmelderGodkjenning(
                    autorisasjon = SykmelderKode(aktiv = true, oid = 7704, verdi = "1"),
                    helsepersonellkategori = SykmelderKode(aktiv = true, oid = 0, verdi = "FT"),
                    tillegskompetanse =
                        listOf(
                            SykmelderTilleggskompetanse(
                                avsluttetStatus = null,
                                gyldig =
                                    SykmelderPeriode(
                                        fra = LocalDate.of(2000, 1, 1).atStartOfDay(),
                                        til = null,
                                    ),
                                type =
                                    SykmelderKode(
                                        aktiv = false, // Inactive
                                        oid = 7702,
                                        verdi = "1",
                                    ),
                            )
                        ),
                )
            )

        SykmelderScenarios
            .AKTIV_KIROPRAKTOR_MED_TILLEGGSKOMPETANSE_OG_ANNEN_HELSEPERSONELLKATEGORI ->
            listOf(
                SykmelderGodkjenning(
                    autorisasjon = SykmelderKode(aktiv = true, oid = 7704, verdi = "1"),
                    helsepersonellkategori = SykmelderKode(aktiv = true, oid = 9060, verdi = "ET"),
                    tillegskompetanse = null,
                ),
                SykmelderGodkjenning(
                    autorisasjon = SykmelderKode(aktiv = true, oid = 7704, verdi = "1"),
                    helsepersonellkategori = SykmelderKode(aktiv = true, oid = 9060, verdi = "KI"),
                    tillegskompetanse =
                        listOf(
                            SykmelderTilleggskompetanse(
                                avsluttetStatus = null,
                                gyldig =
                                    SykmelderPeriode(
                                        fra = LocalDate.of(2015, 8, 16).atStartOfDay(),
                                        til = LocalDate.of(2059, 1, 5).atStartOfDay(),
                                    ),
                                type = SykmelderKode(aktiv = true, oid = 7702, verdi = "1"),
                            )
                        ),
                ),
            )
    }

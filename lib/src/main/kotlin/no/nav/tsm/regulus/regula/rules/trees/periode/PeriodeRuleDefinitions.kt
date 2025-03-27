package no.nav.tsm.regulus.regula.rules.trees.periode

import no.nav.tsm.regulus.regula.executor.RuleOutcome
import no.nav.tsm.regulus.regula.executor.RuleStatus

internal enum class PeriodeRule {
    PERIODER_MANGLER,
    FRADATO_ETTER_TILDATO,
    OVERLAPPENDE_PERIODER,
    OPPHOLD_MELLOM_PERIODER,
    IKKE_DEFINERT_PERIODE,
    BEHANDLINGSDATO_ETTER_MOTTATTDATO,
    AVVENTENDE_SYKMELDING_KOMBINERT,
    MANGLENDE_INNSPILL_TIL_ARBEIDSGIVER,
    AVVENTENDE_SYKMELDING_OVER_16_DAGER,
    FOR_MANGE_BEHANDLINGSDAGER_PER_UKE,
    GRADERT_SYKMELDING_OVER_99_PROSENT,
    SYKMELDING_MED_BEHANDLINGSDAGER;

    enum class Outcomes(
        override val rule: String,
        override val status: RuleStatus,
        override val messageForSender: String,
        override val messageForUser: String,
    ) : RuleOutcome {
        PERIODER_MANGLER(
            rule = "PERIODER_MANGLER",
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmeldingen kan ikke rettes, det må skrives en ny. Pasienten har fått beskjed om å " +
                    "vente på ny sykmelding fra deg. Grunnet følgende: " +
                    "Hvis ingen perioder er oppgitt skal sykmeldingen avvises.",
            messageForUser = "Det er ikke oppgitt hvilken periode sykmeldingen gjelder for.",
        ),
        FRADATO_ETTER_TILDATO(
            rule = "FRADATO_ETTER_TILDATO",
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmeldingen kan ikke rettes, det må skrives en ny. Pasienten har fått beskjed om å vente " +
                    "på ny sykmelding fra deg. Grunnet følgende: " +
                    "Hvis tildato for en periode ligger før fradato avvises meldingen og hvilken periode det gjelder oppgis.",
            messageForUser = "Det er lagt inn datoer som ikke stemmer innbyrdes.",
        ),
        OVERLAPPENDE_PERIODER(
            rule = "OVERLAPPENDE_PERIODER",
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmeldingen kan ikke rettes, det må skrives en ny. Pasienten har fått beskjed om å vente på" +
                    " ny sykmelding fra deg. Grunnet følgende: " +
                    "Hvis en eller flere perioder er overlappende avvises meldingen og hvilken periode det gjelder oppgis.",
            messageForUser = "Periodene må ikke overlappe hverandre.",
        ),
        OPPHOLD_MELLOM_PERIODER(
            rule = "OPPHOLD_MELLOM_PERIODER",
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmeldingen kan ikke rettes, det må skrives en ny. Pasienten har fått beskjed om å " +
                    "vente på ny sykmelding fra deg. Grunnet følgende: " +
                    "Hvis det finnes opphold mellom perioder i sykmeldingen avvises meldingen.",
            messageForUser = "Det er opphold mellom sykmeldingsperiodene.",
        ),
        IKKE_DEFINERT_PERIODE(
            rule = "IKKE_DEFINERT_PERIODE",
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmeldingen kan ikke rettes, det må skrives en ny. Pasienten har fått beskjed om å" +
                    " vente på ny sykmelding fra deg. Grunnet følgende: " +
                    "Det er ikke oppgitt type for sykmeldingen " +
                    "(den må være enten 100 prosent, gradert, avventende, reisetilskudd eller behandlingsdager).",
            messageForUser =
                "Det er ikke oppgitt type for sykmeldingen " +
                    "(den må være enten 100 prosent, gradert, avventende, reisetilskudd eller behandlingsdager).",
        ),
        BEHANDLINGSDATO_ETTER_MOTTATTDATO(
            rule = "BEHANDLINGSDATO_ETTER_MOTTATTDATO",
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmeldingen kan ikke rettes, det må skrives en ny. " +
                    "Pasienten har fått beskjed om å vente på ny sykmelding fra deg. Grunnet følgende:" +
                    "Behandlingsdatoen er etter dato for når NAV mottok meldingen",
            messageForUser = "Behandlingsdatoen må rettes.",
        ),
        AVVENTENDE_SYKMELDING_KOMBINERT(
            rule = "AVVENTENDE_SYKMELDING_KOMBINERT",
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmeldingen kan ikke rettes, det må skrives en ny. " +
                    "Pasienten har fått beskjed om å vente på ny sykmelding fra deg. Grunnet følgende:" +
                    "Avventende sykmelding kan ikke inneholde flere perioder.",
            messageForUser = "En avventende sykmelding kan bare inneholde én periode.",
        ),
        MANGLENDE_INNSPILL_TIL_ARBEIDSGIVER(
            rule = "MANGLENDE_INNSPILL_TIL_ARBEIDSGIVER",
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmeldingen kan ikke rettes, det må skrives en ny. Pasienten har fått beskjed om å vente på ny sykmelding fra deg. Grunnet følgende:" +
                    "Hvis innspill til arbeidsgiver om tilrettelegging i pkt 4.1.3 ikke er utfylt ved avventende sykmelding avvises meldingen",
            messageForUser =
                "En avventende sykmelding forutsetter at du kan jobbe hvis arbeidsgiveren din legger til " +
                    "rette for det. Den som har sykmeldt deg har ikke foreslått hva arbeidsgiveren kan gjøre, " +
                    "noe som kreves for denne typen sykmelding.",
        ),
        AVVENTENDE_SYKMELDING_OVER_16_DAGER(
            rule = "AVVENTENDE_SYKMELDING_OVER_16_DAGER",
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmeldingen kan ikke rettes, det må skrives en ny. " +
                    "Pasienten har fått beskjed om å vente på ny sykmelding fra deg. Grunnet følgende:" +
                    "Hvis avventende sykmelding benyttes utover arbeidsgiverperioden på 16 kalenderdager," +
                    " avvises meldingen.",
            messageForUser = "En avventende sykmelding kan bare gis for 16 dager.",
        ),
        FOR_MANGE_BEHANDLINGSDAGER_PER_UKE(
            rule = "FOR_MANGE_BEHANDLINGSDAGER_PER_UKE",
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmeldingen kan ikke rettes, det må skrives en ny. " +
                    "Pasienten har fått beskjed om å vente på ny sykmelding fra deg. Grunnet følgende:" +
                    "Hvis antall dager oppgitt for behandlingsdager periode er for høyt" +
                    " i forhold til periodens lengde avvises meldingen. Mer enn en dag per uke er for høyt." +
                    " 1 dag per påbegynt uke.",
            messageForUser =
                "Det er angitt for mange behandlingsdager. Det kan bare angis én behandlingsdag per uke.",
        ),
        GRADERT_SYKMELDING_OVER_99_PROSENT(
            rule = "GRADERT_SYKMELDING_OVER_99_PROSENT",
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmeldingen kan ikke rettes, det må skrives en ny. " +
                    "Pasienten har fått beskjed om å vente på ny sykmelding fra deg. Grunnet følgende:" +
                    "Hvis sykmeldingsgrad er høyere enn 99% for delvis sykmelding avvises meldingen",
            messageForUser =
                "Sykmeldingsgraden kan ikke være mer enn 99% fordi det er en gradert sykmelding.",
        ),
        SYKMELDING_MED_BEHANDLINGSDAGER(
            rule = "SYKMELDING_MED_BEHANDLINGSDAGER",
            status = RuleStatus.MANUAL_PROCESSING,
            messageForSender = "Sykmelding inneholder behandlingsdager (felt 4.4).",
            messageForUser = "Sykmelding inneholder behandlingsdager.",
        ),
    }
}

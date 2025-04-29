package no.nav.tsm.regulus.regula.rules.trees.tilbakedatering

import no.nav.tsm.regulus.regula.dsl.RuleOutcome
import no.nav.tsm.regulus.regula.dsl.RuleStatus

internal enum class TilbakedateringRule {
    ARBEIDSGIVERPERIODE,
    BEGRUNNELSE_MIN_1_ORD,
    BEGRUNNELSE_MIN_3_ORD,
    ETTERSENDING,
    FORLENGELSE,
    SPESIALISTHELSETJENESTEN,
    TILBAKEDATERING,
    TILBAKEDATERT_INNTIL_4_DAGER,
    TILBAKEDATERT_INNTIL_8_DAGER,
    TILBAKEDATERT_MINDRE_ENN_1_MAANED;

    enum class Outcomes(
        override val status: RuleStatus,
        override val messageForSender: String,
        override val messageForUser: String,
    ) : RuleOutcome {
        INNTIL_8_DAGER(
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmeldingen kan ikke rettes, det må skrives en ny. Pasienten har fått beskjed om å vente på ny sykmelding fra deg. Grunnet følgende:" +
                    "Første sykmelding er tilbakedatert uten at begrunnelse (felt 11.2) er tilstrekkelig utfylt",
            messageForUser =
                "Sykmeldingen er tilbakedatert uten tilstrekkelig begrunnelse fra den som sykmeldte deg.",
        ),
        MINDRE_ENN_1_MAANED(
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmeldingen kan ikke rettes, det må skrives en ny. Pasienten har fått beskjed om å vente på ny sykmelding fra deg. Grunnet følgende:" +
                    "Sykmelding er tilbakedatert uten begrunnelse (felt 11.2) er tilstrekkelig utfylt",
            messageForUser =
                "Sykmeldingen er tilbakedatert uten tilstrekkelig begrunnelse fra den som sykmeldte deg.",
        ),
        MINDRE_ENN_1_MAANED_MED_BEGRUNNELSE(
            status = RuleStatus.MANUAL_PROCESSING,
            messageForSender =
                "Første sykmelding er tilbakedatert og felt 11.2 (begrunnelse) er utfylt",
            messageForUser = "Sykmeldingen blir manuelt behandlet fordi den er tilbakedatert",
        ),
        OVER_1_MND(
            status = RuleStatus.INVALID,
            messageForSender =
                "Sykmeldingen kan ikke rettes, det må skrives en ny. Pasienten har fått beskjed om å vente på ny sykmelding fra deg. " +
                    "Grunnet følgende: Sykmelding er tilbakedatert mer enn det som er tillatt og felt 11.2 (begrunnelse) er utfylt uten tilstrekkelig begrunnelse",
            messageForUser =
                "Sykmeldingen er tilbakedatert uten tilstrekkelig begrunnelse fra den som sykmeldte deg.",
        ),
        OVER_1_MND_MED_BEGRUNNELSE(
            status = RuleStatus.MANUAL_PROCESSING,
            messageForSender = "Sykmeldingen er tilbakedatert og felt 11.2 (begrunnelse) er utfylt",
            messageForUser = "Sykmeldingen blir manuelt behandlet fordi den er tilbakedatert",
        ),
        OVER_1_MND_SPESIALISTHELSETJENESTEN(
            status = RuleStatus.MANUAL_PROCESSING,
            messageForSender =
                "Sykmeldingen er tilbakedatert over 1 månede og er fra spesialisthelsetjenesten",
            messageForUser = "Sykmeldingen blir manuelt behandlet fordi den er tilbakedatert",
        ),
    }
}

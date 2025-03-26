package no.nav.tsm.regulus.regula.trees.tilbakedatering.extras

import java.time.LocalDate
import no.nav.tsm.regulus.regula.payload.SykmeldingPeriode
import no.nav.tsm.regulus.regula.trees.tilbakedatering.TidligereSykmelding
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Ettersending")

internal data class Ettersendelse(
    val sykmeldingId: String,
    val fom: LocalDate,
    val tom: LocalDate,
    val gradert: Int?,
)

internal fun isEttersending(
    sykmeldingId: String,
    perioder: List<SykmeldingPeriode>,
    harMedisinskVurdering: Boolean,
    tidligereSykmeldinger: List<TidligereSykmelding>,
): Ettersendelse? {
    if (perioder.size > 1) {
        logger.info("Flere perioder i periodelisten returnerer false ${sykmeldingId}")
        return null
    }
    if (!harMedisinskVurdering) {
        logger.info("Diagnosekode mangler for ${sykmeldingId}")
        return null
    }

    val periode = perioder.first()
    val tidligerePerioder: List<Pair<SykmeldingPeriode, TidligereSykmelding>> =
        tidligereSykmeldinger
            .map { tidligereSykmelding ->
                tidligereSykmelding.perioder.map { it to tidligereSykmelding }
            }
            .flatten()

    val tidligereSykmeldingEttersendelse: Pair<SykmeldingPeriode, TidligereSykmelding>? =
        tidligerePerioder.firstOrNull { (tidligerePeriode) ->
            periode.ettersendingmessigEqual(tidligerePeriode)
        }

    if (tidligereSykmeldingEttersendelse != null) {
        logger.info(
            "Sykmelding ${sykmeldingId} er ettersending av ${tidligereSykmeldingEttersendelse.second.sykmeldingId}"
        )
    } else {
        logger.info(
            "Could not find ettersending for ${sykmeldingId} from ${tidligereSykmeldinger.map { it.sykmeldingId }}"
        )
    }

    return tidligereSykmeldingEttersendelse?.let { (periode, sykmelding) ->
        Ettersendelse(
            sykmeldingId = sykmelding.sykmeldingId,
            // TODO: Dobbeltsjekk at dette blir riktig mtp. forrige impl. som brukte earliestFom →
            // latestTom
            fom = periode.fom,
            tom = periode.tom,
            gradert = if (periode is SykmeldingPeriode.Gradert) periode.grad else null,
        )
    }
}

private fun SykmeldingPeriode.ettersendingmessigEqual(other: SykmeldingPeriode): Boolean {
    val overallEqual = this.type == other.type && this.fom == other.fom && this.tom == other.tom
    if (!overallEqual) return false

    return when {
        this is SykmeldingPeriode.Gradert && other is SykmeldingPeriode.Gradert ->
            this.grad == other.grad
        else -> true
    }
}

package no.nav.tsm.regulus.regula.rules.trees.tilbakedatering.extras

import java.time.LocalDate
import no.nav.tsm.regulus.regula.payload.Aktivitet
import no.nav.tsm.regulus.regula.payload.Diagnose
import no.nav.tsm.regulus.regula.payload.SykmeldingPeriodeType
import no.nav.tsm.regulus.regula.payload.TidligereSykmelding
import no.nav.tsm.regulus.regula.rules.shared.onlyRelevantWithSameDiagnosis
import no.nav.tsm.regulus.regula.utils.earliestFom
import no.nav.tsm.regulus.regula.utils.isWorkingDaysBetween
import no.nav.tsm.regulus.regula.utils.latestTom

internal data class Forlengelse(
    val sykmeldingId: String,
    val fom: LocalDate,
    val tom: LocalDate,
    val gradert: Int?,
)

internal fun isForlengelse(
    perioder: List<Aktivitet>,
    hoveddiagnose: Diagnose?,
    tidligereSykmeldinger: List<TidligereSykmelding>,
): List<Forlengelse> {
    val firstFom = perioder.earliestFom()
    val lastTom = perioder.latestTom()

    val tidligerePerioderFomTom =
        tidligereSykmeldinger
            .onlyRelevantWithSameDiagnosis(hoveddiagnose)
            .filter { it.hoveddiagnose?.kode == hoveddiagnose?.kode }
            .filter { it.aktivitet.size == 1 }
            .map { it.sykmeldingId to it.aktivitet.first() }
            .filter { (_, periode) ->
                periode.type == SykmeldingPeriodeType.AKTIVITET_IKKE_MULIG ||
                    periode.type == SykmeldingPeriodeType.GRADERT
            }
            .map { (id, periode) ->
                Forlengelse(
                    id,
                    fom = periode.fom,
                    tom = periode.tom,
                    gradert = if (periode is Aktivitet.Gradert) periode.grad else null,
                )
            }

    val forlengelserAv =
        tidligerePerioderFomTom.filter { periode ->
            !isWorkingDaysBetween(firstFom, periode.tom) ||
                isOverlappendeAndForlengelse(periode.tom, periode.fom, firstFom, lastTom)
        }

    return forlengelserAv
}

private fun isOverlappendeAndForlengelse(
    periodeTom: LocalDate,
    periodeFom: LocalDate,
    firstFom: LocalDate,
    lastTom: LocalDate,
) =
    (firstFom.isAfter(periodeFom.minusDays(1)) &&
        firstFom.isBefore(periodeTom.plusDays(1)) &&
        lastTom.isAfter(periodeTom.minusDays(1)))

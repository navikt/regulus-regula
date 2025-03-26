package no.nav.tsm.regulus.regula.trees.tilbakedatering.extras

import java.time.LocalDate
import no.nav.tsm.regulus.regula.payload.Diagnose
import no.nav.tsm.regulus.regula.payload.SykmeldingPeriode
import no.nav.tsm.regulus.regula.payload.SykmeldingPeriodeType
import no.nav.tsm.regulus.regula.trees.tilbakedatering.TidligereSykmelding
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
    perioder: List<SykmeldingPeriode>,
    hoveddiagnose: Diagnose?,
    tidligereSykmeldinger: List<TidligereSykmelding>,
): List<Forlengelse> {
    val firstFom = perioder.earliestFom()
    val lastTom = perioder.latestTom()

    val tidligerePerioderFomTom =
        tidligereSykmeldinger
            .filter { it.hoveddiagnose?.kode == hoveddiagnose?.kode }
            .filter { it.perioder.size == 1 }
            .map { it.sykmeldingId to it.perioder.first() }
            .filter { (_, periode) ->
                periode.type == SykmeldingPeriodeType.AKTIVITET_IKKE_MULIG ||
                    periode.type == SykmeldingPeriodeType.GRADERT
            }
            .map { (id, periode) ->
                Forlengelse(
                    id,
                    fom = periode.fom,
                    tom = periode.tom,
                    gradert = if (periode is SykmeldingPeriode.Gradert) periode.grad else null,
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

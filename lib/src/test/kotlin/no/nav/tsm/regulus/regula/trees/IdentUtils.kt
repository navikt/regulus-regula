package no.nav.tsm.regulus.regula.trees

import java.time.LocalDate
import java.time.format.DateTimeFormatter

val personNumberDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("ddMMyy")

fun generatePersonNumber(bornDate: LocalDate, useDNumber: Boolean = false): String {
    val personDate =
        bornDate.format(personNumberDateFormat).let {
            if (useDNumber) "${it[0] + 4}${it.substring(1)}" else it
        }
    return (if (bornDate.year >= 2000) (75011..99999) else (11111..50099))
        .map { "$personDate$it" }
        .first { validatePersonAndDNumber(it) }
}

fun validatePersonAndDNumber(personNumber: String): Boolean =
    validatePersonDNumberMod11(personNumber) && validatePersonAndPersonDNumberRange(personNumber)

private fun validatePersonAndPersonDNumberRange(personNumber: String): Boolean {
    val personNumberBornDay = personNumber.substring(0, 2)
    return validatePersonNumberRange(personNumberBornDay) ||
        validatePersonDNumberRange(personNumberBornDay)
}

val lookup1: IntArray = intArrayOf(3, 7, 6, 1, 8, 9, 4, 5, 2, 0)
val lookup2: IntArray = intArrayOf(5, 4, 3, 2, 7, 6, 5, 4, 3, 2)

fun validatePersonDNumberMod11(personNumber: String): Boolean {
    if (personNumber.length != 11) {
        return false
    }

    var checksum1 = 0
    var checksum2 = 0

    for (i in 0..9) {
        val currNum = (personNumber[i] - '0')
        checksum1 += currNum * lookup1[i]
        checksum2 += currNum * lookup2[i]
    }

    checksum1 %= 11
    checksum2 %= 11

    val checksum1Final = if (checksum1 == 0) 0 else 11 - checksum1
    val checksum2Final = if (checksum2 == 0) 0 else 11 - checksum2

    return checksum1Final != 10 &&
        personNumber[9] - '0' == checksum1Final &&
        personNumber[10] - '0' == checksum2Final
}

fun validatePersonNumberRange(personNumberFirstAndSecoundChar: String): Boolean =
    personNumberFirstAndSecoundChar.toInt() in 1..31

fun validatePersonDNumberRange(personNumberFirstAndSecoundChar: String): Boolean =
    personNumberFirstAndSecoundChar.toInt() in 41..71

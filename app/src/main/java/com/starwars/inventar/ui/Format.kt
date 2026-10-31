package com.starwars.inventar.ui

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

/** Formatiert UTC-Epoch-Millisekunden als dd.MM.yyyy, null ergibt einen leeren Text. */
fun formatDate(millis: Long?): String =
    if (millis == null) "" else Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate().format(dateFormatter)

/** Betrag in der Währung des Universums: Credits. */
fun formatCredits(value: Double): String = String.format(Locale.GERMANY, "%,.2f cr", value)

/** Wandelt eine Eingabe wie "12,50" oder "1.234,50" in eine Zahl um. Ungültige Eingaben ergeben 0. */
fun parseDecimal(text: String): Double {
    val trimmed = text.trim()
    val normalized = if (trimmed.contains(',')) trimmed.replace(".", "").replace(',', '.') else trimmed
    return normalized.toDoubleOrNull() ?: 0.0
}

/** Für Eingabefelder: 0 wird als leeres Feld dargestellt. */
fun decimalToText(value: Double): String =
    if (value == 0.0) "" else String.format(Locale.GERMANY, "%.2f", value)

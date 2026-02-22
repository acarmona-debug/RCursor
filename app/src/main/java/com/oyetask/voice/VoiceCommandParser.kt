package com.oyetask.voice

import java.text.Normalizer
import java.time.LocalDate
import java.util.Locale

data class ParsedTask(
    val title: String,
    val dueDateEpochDay: Long?,
)

object VoiceCommandParser {
    fun parse(input: String, today: LocalDate = LocalDate.now()): ParsedTask? {
        val raw = input.trim()
        if (raw.isBlank()) return null

        val base = stripPrefix(raw).trimStart(' ', ',', ':', ';', '.').trim()
        val baseNonEmpty = if (base.isNotBlank()) base else raw

        val split = splitOnWordIndex(baseNonEmpty, "para")
        val beforePara = split?.let { baseNonEmpty.substring(0, it).trim() } ?: baseNonEmpty
        val afterPara = split?.let { baseNonEmpty.substring(it).trim() } // includes "para ..."

        val due = afterPara?.let { parseDueDate(it, today) }
        if (due != null && beforePara.isBlank()) return null
        val title = cleanTitle(
            if (due != null && beforePara.isNotBlank()) beforePara else baseNonEmpty,
        )

        if (title.isBlank()) return null
        return ParsedTask(title = title, dueDateEpochDay = due?.toEpochDay())
    }

    private fun parseDueDate(text: String, today: LocalDate): LocalDate? {
        val t = normalize(text).trim().trim(',', '.', ';', ':')
        if (t.isBlank()) return null

        if (t.contains("hoy")) return today
        if (t.contains("manana")) return today.plusDays(1)

        // dd/mm(/yyyy) o dd-mm(-yyyy)
        Regex("""\b(\d{1,2})[/-](\d{1,2})(?:[/-](\d{2,4}))?\b""")
            .find(t)
            ?.let { m ->
                val day = m.groupValues[1].toIntOrNull() ?: return null
                val month = m.groupValues[2].toIntOrNull() ?: return null
                val yearRaw = m.groupValues.getOrNull(3).orEmpty()
                val year = when (yearRaw.length) {
                    0 -> today.year
                    2 -> 2000 + (yearRaw.toIntOrNull() ?: return null)
                    4 -> yearRaw.toIntOrNull() ?: return null
                    else -> return null
                }
                return safeLocalDate(day, month, year)?.let { adjustYearIfNeeded(it, today, yearRaw.isEmpty()) }
            }

        // "5 de marzo (de 2026)"
        Regex("""\b(\d{1,2})\s+de\s+([a-zñ]+)(?:\s+de\s+(\d{4}))?\b""")
            .find(t)
            ?.let { m ->
                val day = m.groupValues[1].toIntOrNull() ?: return null
                val monthName = m.groupValues[2]
                val month = monthFromSpanish(monthName) ?: return null
                val yearStr = m.groupValues.getOrNull(3).orEmpty()
                val explicitYear = yearStr.isNotEmpty()
                val year = if (explicitYear) yearStr.toIntOrNull() ?: return null else today.year
                return safeLocalDate(day, month, year)?.let { adjustYearIfNeeded(it, today, !explicitYear) }
            }

        return null
    }

    private fun adjustYearIfNeeded(date: LocalDate, today: LocalDate, yearWasAssumed: Boolean): LocalDate {
        if (!yearWasAssumed) return date
        return if (date.isBefore(today)) date.plusYears(1) else date
    }

    private fun safeLocalDate(day: Int, month: Int, year: Int): LocalDate? {
        return try {
            LocalDate.of(year, month, day)
        } catch (_: Throwable) {
            null
        }
    }

    private fun monthFromSpanish(name: String): Int? {
        return when (name) {
            "enero" -> 1
            "febrero" -> 2
            "marzo" -> 3
            "abril" -> 4
            "mayo" -> 5
            "junio" -> 6
            "julio" -> 7
            "agosto" -> 8
            "septiembre", "setiembre" -> 9
            "octubre" -> 10
            "noviembre" -> 11
            "diciembre" -> 12
            else -> null
        }
    }

    private fun cleanTitle(text: String): String {
        var t = text.trim().trim(',', '.', ';', ':')

        val fillers = listOf(
            Regex("""^\s*tengo\s+que\s+hacer\s+""", RegexOption.IGNORE_CASE),
            Regex("""^\s*tengo\s+que\s+""", RegexOption.IGNORE_CASE),
            Regex("""^\s*debo\s+hacer\s+""", RegexOption.IGNORE_CASE),
            Regex("""^\s*debo\s+""", RegexOption.IGNORE_CASE),
            Regex("""^\s*necesito\s+""", RegexOption.IGNORE_CASE),
            Regex("""^\s*hay\s+que\s+""", RegexOption.IGNORE_CASE),
        )
        for (r in fillers) t = r.replace(t, "")
        return t.trim().trim(',', '.', ';', ':')
    }

    private fun splitOnWordIndex(text: String, word: String): Int? {
        val lower = text.lowercase(Locale("es", "ES"))
        val m = Regex("""\b$word\b""").find(lower) ?: return null
        return m.range.first
    }

    private fun stripPrefix(text: String): String {
        val lower = text.lowercase(Locale("es", "ES")).trimStart()
        val prefixes = listOf("oye task", "oye tasks")
        val prefix = prefixes.firstOrNull { lower.startsWith(it) } ?: return text
        val start = lower.indexOf(prefix)
        val rawTrimStart = text.dropWhile { it.isWhitespace() }
        return rawTrimStart.drop(prefix.length)
    }

    private fun normalize(input: String): String {
        val noAccents = Normalizer.normalize(input, Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
        return noAccents.lowercase(Locale("es", "ES"))
    }
}


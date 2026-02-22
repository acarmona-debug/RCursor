package com.example.taskvoicemanager.voice

import java.time.LocalDate
import java.time.ZoneId

object VoiceTaskParser {
    data class ParsedTask(
        val description: String,
        val dueAtEpochMillis: Long?
    )

    private val absoluteDateRegex = Regex("""\b(\d{1,2})[/-](\d{1,2})(?:[/-](\d{2,4}))?\b""")
    private val leadingAssistantRegex = Regex("""^\s*oye task[\s,:-]*""", RegexOption.IGNORE_CASE)
    private val leadingIntentRegex = Regex(
        """^\s*(tengo que|debo|necesito|recordarme|recuerdame)\s+""",
        RegexOption.IGNORE_CASE
    )
    private val separatorRegex = Regex("""\b(para|antes de|el dia)\b""", RegexOption.IGNORE_CASE)
    private val temporalWordsRegex = Regex("""\b(hoy|manana|pasado manana)\b""", RegexOption.IGNORE_CASE)

    fun parse(rawText: String, now: LocalDate = LocalDate.now()): ParsedTask {
        val normalized = rawText.trim()
        if (normalized.isBlank()) return ParsedTask(description = "", dueAtEpochMillis = null)

        val withoutAssistant = normalized.replace(leadingAssistantRegex, "")
        val cleaned = withoutAssistant.replace(leadingIntentRegex, "").trim()

        val dueDate = parseDueDate(cleaned, now)
        val dueEpochMillis = dueDate?.atTime(23, 59)
            ?.atZone(ZoneId.systemDefault())
            ?.toInstant()
            ?.toEpochMilli()

        val beforeDueSegment = separatorRegex.split(cleaned, limit = 2).firstOrNull().orEmpty()
        val description = beforeDueSegment
            .replace(absoluteDateRegex, "")
            .replace(temporalWordsRegex, "")
            .replace(Regex("""\s+"""), " ")
            .trim(' ', ',', '.', ':', ';')

        val finalDescription = if (description.isNotBlank()) {
            description
        } else {
            cleaned
                .replace(absoluteDateRegex, "")
                .replace(temporalWordsRegex, "")
                .replace(Regex("""\s+"""), " ")
                .trim(' ', ',', '.', ':', ';')
        }

        return ParsedTask(
            description = finalDescription,
            dueAtEpochMillis = dueEpochMillis
        )
    }

    private fun parseDueDate(text: String, now: LocalDate): LocalDate? {
        absoluteDateRegex.find(text)?.let { match ->
            val day = match.groupValues[1].toIntOrNull() ?: return@let
            val month = match.groupValues[2].toIntOrNull() ?: return@let
            val inputYear = match.groupValues.getOrNull(3).orEmpty()
            val year = when {
                inputYear.isBlank() -> now.year
                inputYear.length == 2 -> 2000 + (inputYear.toIntOrNull() ?: return@let)
                else -> inputYear.toIntOrNull() ?: return@let
            }

            runCatching { LocalDate.of(year, month, day) }.getOrNull()?.let { return it }
        }

        val lowered = text.lowercase()
        return when {
            lowered.contains("pasado manana") -> now.plusDays(2)
            lowered.contains("manana") -> now.plusDays(1)
            lowered.contains("hoy") -> now
            else -> null
        }
    }
}

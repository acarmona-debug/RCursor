package com.example.taskvoicemanager.voice

import java.time.LocalDate
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class VoiceTaskParserTest {
    @Test
    fun `parse absolute date and description`() {
        val now = LocalDate.of(2026, 2, 22)
        val result = VoiceTaskParser.parse(
            rawText = "Oye task, tengo que entregar informe para 25/02/2026",
            now = now
        )

        val expectedEpoch = LocalDate.of(2026, 2, 25)
            .atTime(23, 59)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        assertEquals("entregar informe", result.description)
        assertEquals(expectedEpoch, result.dueAtEpochMillis)
    }

    @Test
    fun `parse manana keyword`() {
        val now = LocalDate.of(2026, 2, 22)
        val result = VoiceTaskParser.parse(
            rawText = "Debo llamar al cliente manana",
            now = now
        )

        val expectedEpoch = LocalDate.of(2026, 2, 23)
            .atTime(23, 59)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        assertEquals("llamar al cliente", result.description)
        assertEquals(expectedEpoch, result.dueAtEpochMillis)
    }

    @Test
    fun `parse without date`() {
        val result = VoiceTaskParser.parse(rawText = "Oye task organizar backlog")
        assertEquals("organizar backlog", result.description)
        assertNull(result.dueAtEpochMillis)
    }
}

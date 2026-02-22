package com.actividadapp.util

import java.util.*

/**
 * Parsea frases en español del tipo:
 * - "tengo que hacer X para mañana"
 * - "oye task, tengo que hacer X para el lunes"
 * - "tengo que hacer X para el 15 de marzo"
 * - "tengo que hacer X para pasado mañana"
 * - "tengo que hacer X para la próxima semana"
 */
object VoiceParser {
    private val patronesFecha = listOf(
        // "para mañana", "para pasado mañana"
        Regex("""para\s+mañana""", RegexOption.IGNORE_CASE) to { c: Calendar -> c.add(Calendar.DAY_OF_YEAR, 1) },
        Regex("""para\s+pasado\s+mañana""", RegexOption.IGNORE_CASE) to { c: Calendar -> c.add(Calendar.DAY_OF_YEAR, 2) },
        // Días de la semana
        Regex("""para\s+(?:el\s+)?lunes""", RegexOption.IGNORE_CASE) to { c: Calendar -> nextDayOfWeek(c, Calendar.MONDAY) },
        Regex("""para\s+(?:el\s+)?martes""", RegexOption.IGNORE_CASE) to { c: Calendar -> nextDayOfWeek(c, Calendar.TUESDAY) },
        Regex("""para\s+(?:el\s+)?miércoles""", RegexOption.IGNORE_CASE) to { c: Calendar -> nextDayOfWeek(c, Calendar.WEDNESDAY) },
        Regex("""para\s+(?:el\s+)?jueves""", RegexOption.IGNORE_CASE) to { c: Calendar -> nextDayOfWeek(c, Calendar.THURSDAY) },
        Regex("""para\s+(?:el\s+)?viernes""", RegexOption.IGNORE_CASE) to { c: Calendar -> nextDayOfWeek(c, Calendar.FRIDAY) },
        Regex("""para\s+(?:el\s+)?sábado""", RegexOption.IGNORE_CASE) to { c: Calendar -> nextDayOfWeek(c, Calendar.SATURDAY) },
        Regex("""para\s+(?:el\s+)?sabado""", RegexOption.IGNORE_CASE) to { c: Calendar -> nextDayOfWeek(c, Calendar.SATURDAY) },
        Regex("""para\s+(?:el\s+)?domingo""", RegexOption.IGNORE_CASE) to { c: Calendar -> nextDayOfWeek(c, Calendar.SUNDAY) },
        // "próximo lunes", etc.
        Regex("""para\s+(?:el\s+)?(?:próximo|proximo)\s+lunes""", RegexOption.IGNORE_CASE) to { c: Calendar -> nextWeekDay(c, Calendar.MONDAY) },
        Regex("""para\s+(?:el\s+)?(?:próximo|proximo)\s+martes""", RegexOption.IGNORE_CASE) to { c: Calendar -> nextWeekDay(c, Calendar.TUESDAY) },
        Regex("""para\s+(?:el\s+)?(?:próximo|proximo)\s+mi[eé]rcoles""", RegexOption.IGNORE_CASE) to { c: Calendar -> nextWeekDay(c, Calendar.WEDNESDAY) },
        Regex("""para\s+(?:el\s+)?(?:próximo|proximo)\s+jueves""", RegexOption.IGNORE_CASE) to { c: Calendar -> nextWeekDay(c, Calendar.THURSDAY) },
        Regex("""para\s+(?:el\s+)?(?:próximo|proximo)\s+viernes""", RegexOption.IGNORE_CASE) to { c: Calendar -> nextWeekDay(c, Calendar.FRIDAY) },
        Regex("""para\s+(?:el\s+)?(?:próximo|proximo)\s+s[aá]bado""", RegexOption.IGNORE_CASE) to { c: Calendar -> nextWeekDay(c, Calendar.SATURDAY) },
        Regex("""para\s+(?:el\s+)?(?:próximo|proximo)\s+domingo""", RegexOption.IGNORE_CASE) to { c: Calendar -> nextWeekDay(c, Calendar.SUNDAY) },
        // "para hoy"
        Regex("""para\s+hoy""", RegexOption.IGNORE_CASE) to { c: Calendar -> Unit },
        // "para la próxima semana" - lunes de la siguiente semana
        Regex("""para\s+(?:la\s+)?próxima\s+semana""", RegexOption.IGNORE_CASE) to { c: Calendar -> c.add(Calendar.WEEK_OF_YEAR, 1); c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY) },
        Regex("""para\s+(?:la\s+)?proxima\s+semana""", RegexOption.IGNORE_CASE) to { c: Calendar -> c.add(Calendar.WEEK_OF_YEAR, 1); c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY) }
    )

    data class Resultado(
        val titulo: String,
        val fechaLimite: Long
    )

    /**
     * Parsea texto de voz y extrae tarea + fecha.
     * Frases reconocidas: "tengo que hacer X para Y", "oye task tengo que hacer X para Y"
     */
    fun parsear(texto: String): Resultado? {
        val textoLimpio = texto
            .replace(Regex("""(?:oye\s+task[,\.]?\s*|ok\s+)?""", RegexOption.IGNORE_CASE), "")
            .trim()

        val patronTarea = Regex(
            """(?:tengo\s+que\s+hacer|tengo\s+que|hacer|realizar|completar)\s+(.+?)\s+para\s+(.+)""",
            RegexOption.IGNORE_CASE and RegexOption.DOT_MATCHES_ALL
        )
        val match = patronTarea.find(textoLimpio) ?: return null

        val tarea = match.groupValues[1].trim()
        val fechaTexto = match.groupValues[2].trim()

        val calendario = Calendar.getInstance()
        calendario.set(Calendar.HOUR_OF_DAY, 23)
        calendario.set(Calendar.MINUTE, 59)
        calendario.set(Calendar.SECOND, 0)
        calendario.set(Calendar.MILLISECOND, 0)

        for ((patron, accion) in patronesFecha) {
            if (patron.containsMatchIn("para $fechaTexto")) {
                accion(calendario)
                return Resultado(titulo = tarea, fechaLimite = calendario.timeInMillis)
            }
        }

        // Intentar parsear "15 de marzo" o "15/03"
        val fechaNum = Regex("""(?:para\s+)?(\d{1,2})[/\s]+(?:de\s+)?(\d{1,2}|enero|febrero|marzo|abril|mayo|junio|julio|agosto|septiembre|octubre|noviembre|diciembre)""", RegexOption.IGNORE_CASE)
            .find("para $fechaTexto")
        if (fechaNum != null) {
            val dia = fechaNum.groupValues[1].toIntOrNull() ?: return null
            val mesTexto = fechaNum.groupValues[2].lowercase()
            val meses = mapOf(
                "enero" to 0, "febrero" to 1, "marzo" to 2, "abril" to 3, "mayo" to 4, "junio" to 5,
                "julio" to 6, "agosto" to 7, "septiembre" to 8, "octubre" to 9, "noviembre" to 10, "diciembre" to 11
            )
            val mes = meses[mesTexto] ?: mesTexto.toIntOrNull()?.let { it - 1 } ?: return null
            calendario.set(Calendar.DAY_OF_MONTH, dia)
            calendario.set(Calendar.MONTH, mes)
            if (calendario.before(Calendar.getInstance())) {
                calendario.add(Calendar.YEAR, 1)
            }
            return Resultado(titulo = tarea, fechaLimite = calendario.timeInMillis)
        }

        return null
    }

    private fun nextDayOfWeek(cal: Calendar, dayOfWeek: Int) {
        val actual = cal.get(Calendar.DAY_OF_WEEK)
        var diff = dayOfWeek - actual
        if (diff <= 0) diff += 7
        cal.add(Calendar.DAY_OF_YEAR, diff)
    }

    private fun nextWeekDay(cal: Calendar, dayOfWeek: Int) {
        cal.add(Calendar.WEEK_OF_YEAR, 1)
        val actual = cal.get(Calendar.DAY_OF_WEEK)
        var diff = dayOfWeek - actual
        if (diff <= 0) diff += 7
        cal.add(Calendar.DAY_OF_YEAR, diff)
    }
}

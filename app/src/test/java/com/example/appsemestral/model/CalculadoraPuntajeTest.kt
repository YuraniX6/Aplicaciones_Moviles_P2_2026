package com.example.appsemestral.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class CalculadoraPuntajeTest {

    private val criterios = listOf(
        Criterio(id = 1, nombre = "A", descripcion = "", peso = 60, orden = 1),
        Criterio(id = 2, nombre = "B", descripcion = "", peso = 40, orden = 2)
    )

    @Test
    fun todo_en_cinco_da_cien() {
        assertEquals(100.0, CalculadoraPuntaje.calcular(criterios, mapOf(1L to 5, 2L to 5)), 0.001)
    }

    @Test
    fun promedio_ponderado() {
        // (5*60 + 3*40) / (100*5) * 100 = 84
        assertEquals(84.0, CalculadoraPuntaje.calcular(criterios, mapOf(1L to 5, 2L to 3)), 0.001)
    }

    @Test
    fun sin_criterios_da_cero() {
        assertEquals(0.0, CalculadoraPuntaje.calcular(emptyList(), emptyMap()), 0.001)
    }

    @Test
    fun aviso_solo_aparece_sobre_85() {
        assertEquals(false, CalculadoraPuntaje.superaUmbralAviso(85.0))
        assertEquals(true, CalculadoraPuntaje.superaUmbralAviso(85.1))
        assertEquals(false, CalculadoraPuntaje.superaUmbralAviso(70.0))
    }

    @Test
    fun clave_de_evaluadora_se_normaliza() {
        assertEquals("ana perez", Evaluacion.normalizarClave("  Ana   Pérez "))
    }

    @Test
    fun clave_ignora_tildes_y_mayusculas() {
        assertEquals(
            Evaluacion.normalizarClave("Ana Pérez"),
            Evaluacion.normalizarClave("ana PEREZ")
        )
        assertEquals("maria munoz", Evaluacion.normalizarClave("María  Muñoz"))
    }

    @Test
    fun claves_de_personas_distintas_siguen_siendo_distintas() {
        assertNotEquals(
            Evaluacion.normalizarClave("Ana Pérez"),
            Evaluacion.normalizarClave("Ana Pereira")
        )
    }
}

package com.example.appsemestral.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.text.Normalizer

/**
 * Evaluación de una candidata realizada por una evaluadora.
 * El índice único (candidataId, evaluadoraClave) impide evaluaciones duplicadas
 * incluso a nivel de base de datos.
 */
@Entity(
    tableName = "evaluaciones",
    foreignKeys = [
        ForeignKey(
            entity = Candidata::class,
            parentColumns = ["id"],
            childColumns = ["candidataId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["candidataId", "evaluadoraClave"], unique = true)]
)
data class Evaluacion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val candidataId: Long,
    val evaluadora: String,
    val evaluadoraClave: String,
    val puntajeTotal: Double,
    val observacionGeneral: String,
    val fecha: Long = System.currentTimeMillis()
) {
    companion object {
        /** Evaluaciones mínimas para considerar una candidatura "Evaluada" (consolidada). */
        const val MINIMO_PARA_CONSOLIDAR = 2

        /**
         * "  Ana   Pérez ", "ana pérez" y "Ana Perez" se consideran la misma evaluadora:
         * se ignoran mayúsculas, espacios repetidos y tildes (así no se burla el bloqueo de duplicados).
         */
        fun normalizarClave(nombre: String): String =
            Normalizer.normalize(nombre.trim().lowercase(), Normalizer.Form.NFD)
                .replace(Regex("\\p{Mn}+"), "")
                .replace(Regex("\\s+"), " ")
    }
}

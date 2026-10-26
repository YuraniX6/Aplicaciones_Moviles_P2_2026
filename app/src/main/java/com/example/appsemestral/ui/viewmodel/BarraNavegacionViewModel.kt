package com.example.appsemestral.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appsemestral.data.repository.CandidataRepository
import com.example.appsemestral.model.EstadoCandidatura
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/** Datos de la barra inferior: la cantidad de candidatas "En evaluación" que muestra el Badge. */
class BarraNavegacionViewModel(candidataRepository: CandidataRepository) : ViewModel() {

    val enEvaluacion: StateFlow<Int> = candidataRepository
        .observarCantidadPorEstado(EstadoCandidatura.EN_EVALUACION)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
}

package com.example.appsemestral.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appsemestral.data.repository.EvaluacionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

/** Estadística del perfil: cuántas evaluaciones ha registrado la evaluadora de la sesión. */
@OptIn(ExperimentalCoroutinesApi::class)
class PerfilViewModel(evaluacionRepository: EvaluacionRepository) : ViewModel() {

    private val evaluadora = MutableStateFlow("")

    val totalEvaluaciones: StateFlow<Int> = evaluadora
        .flatMapLatest { nombre ->
            if (nombre.isBlank()) flowOf(0) else evaluacionRepository.observarTotalPorEvaluadora(nombre)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    fun onSesion(nombre: String) {
        evaluadora.value = nombre
    }
}

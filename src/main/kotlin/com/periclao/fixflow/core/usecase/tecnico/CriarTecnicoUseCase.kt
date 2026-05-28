package com.periclao.fixflow.core.usecase.tecnico

import com.periclao.fixflow.core.model.Tecnico
import com.periclao.fixflow.core.model.enums.Categoria

interface CriarTecnicoUseCase {
    fun executar(command: Command): Tecnico

    data class Command(
        val nome: String,
        val email: String,
        val telefone: String,
        val especialidades: Set<Categoria>
    )
}

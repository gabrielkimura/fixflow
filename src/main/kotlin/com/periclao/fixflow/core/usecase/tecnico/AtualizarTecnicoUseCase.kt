package com.periclao.fixflow.core.usecase.tecnico

import com.periclao.fixflow.core.model.Tecnico
import com.periclao.fixflow.core.model.enums.Categoria
import java.util.UUID

interface AtualizarTecnicoUseCase {
    fun executar(command: Command): Tecnico

    data class Command(
        val id: UUID,
        val nome: String,
        val email: String,
        val telefone: String,
        val especialidades: Set<Categoria>,
        val disponivel: Boolean
    )
}

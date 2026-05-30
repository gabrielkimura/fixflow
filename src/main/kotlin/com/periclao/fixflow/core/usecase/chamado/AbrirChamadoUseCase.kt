package com.periclao.fixflow.core.usecase.chamado

import com.periclao.fixflow.core.model.Chamado
import java.util.UUID

interface AbrirChamadoUseCase {
    fun executar(command: Command): Chamado

    data class Command(
        val clienteId: UUID,
        val enderecoId: UUID,
        val descricao: String
    )
}

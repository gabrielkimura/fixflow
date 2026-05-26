package com.periclao.fixflow.core.usecase.cliente

import com.periclao.fixflow.core.model.Cliente
import java.util.UUID

interface AtualizarClienteUseCase {
    fun executar(command: Command): Cliente

    data class Command(
        val id: UUID,
        val nome: String,
        val email: String,
        val telefone: String
    )
}

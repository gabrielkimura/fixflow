package com.periclao.fixflow.core.usecase.cliente

import com.periclao.fixflow.core.model.Cliente

interface CriarClienteUseCase {
    fun executar(command: Command): Cliente

    data class Command(
        val nome: String,
        val email: String,
        val telefone: String
    )
}

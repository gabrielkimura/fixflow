package com.periclao.fixflow.core.usecase.endereco

import com.periclao.fixflow.core.model.Endereco
import com.periclao.fixflow.core.model.UF
import java.util.UUID

interface AtualizarEnderecoUseCase {
    fun executar(command: Command): Endereco

    data class Command(
        val id: UUID,
        val logradouro: String,
        val numero: String,
        val complemento: String?,
        val bairro: String,
        val cidade: String,
        val uf: UF,
        val cep: String,
        val principal: Boolean
    )
}

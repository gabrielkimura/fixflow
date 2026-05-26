package com.periclao.fixflow.api.cliente.response

import com.periclao.fixflow.api.endereco.response.EnderecoResponse
import com.periclao.fixflow.core.model.Cliente
import java.time.LocalDateTime
import java.util.UUID

data class ClienteResponse(
    val id: UUID,
    val nome: String,
    val email: String,
    val telefone: String,
    val ativo: Boolean,
    val criadoEm: LocalDateTime,
    val atualizadoEm: LocalDateTime,
    val enderecos: List<EnderecoResponse>
) {
    companion object {
        fun from(cliente: Cliente) = ClienteResponse(
            id = cliente.id,
            nome = cliente.nome,
            email = cliente.email,
            telefone = cliente.telefone,
            ativo = cliente.ativo,
            criadoEm = cliente.criadoEm,
            atualizadoEm = cliente.atualizadoEm,
            enderecos = cliente.enderecos.map { EnderecoResponse.from(it) }
        )
    }
}

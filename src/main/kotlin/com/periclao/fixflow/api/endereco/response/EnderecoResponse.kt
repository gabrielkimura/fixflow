package com.periclao.fixflow.api.endereco.response

import com.periclao.fixflow.core.model.Endereco
import com.periclao.fixflow.core.model.UF
import java.util.UUID

data class EnderecoResponse(
    val id: UUID,
    val clienteId: UUID,
    val logradouro: String,
    val numero: String,
    val complemento: String?,
    val bairro: String,
    val cidade: String,
    val uf: UF,
    val cep: String,
    val principal: Boolean
) {
    companion object {
        fun from(endereco: Endereco) = EnderecoResponse(
            id = endereco.id,
            clienteId = endereco.clienteId,
            logradouro = endereco.logradouro,
            numero = endereco.numero,
            complemento = endereco.complemento,
            bairro = endereco.bairro,
            cidade = endereco.cidade,
            uf = endereco.uf,
            cep = endereco.cep,
            principal = endereco.principal
        )
    }
}

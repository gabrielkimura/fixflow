package com.periclao.fixflow.infrastructure.mapper

import com.periclao.fixflow.core.model.Endereco
import com.periclao.fixflow.infrastructure.entity.EnderecoEntity

object EnderecoMapper {

    fun toDomain(entity: EnderecoEntity): Endereco = Endereco(
        id = entity.id,
        clienteId = entity.clienteId,
        logradouro = entity.logradouro,
        numero = entity.numero,
        complemento = entity.complemento,
        bairro = entity.bairro,
        cidade = entity.cidade,
        uf = entity.uf,
        cep = entity.cep,
        principal = entity.principal
    )

    fun toEntity(domain: Endereco): EnderecoEntity = EnderecoEntity(
        id = domain.id,
        clienteId = domain.clienteId,
        logradouro = domain.logradouro,
        numero = domain.numero,
        complemento = domain.complemento,
        bairro = domain.bairro,
        cidade = domain.cidade,
        uf = domain.uf,
        cep = domain.cep,
        principal = domain.principal
    )
}

package com.periclao.fixflow.infrastructure.mapper

import com.periclao.fixflow.core.model.Cliente
import com.periclao.fixflow.core.model.Endereco
import com.periclao.fixflow.infrastructure.entity.ClienteEntity

object ClienteMapper {

    fun toDomain(entity: ClienteEntity, enderecos: List<Endereco> = emptyList()): Cliente = Cliente(
        id = entity.id,
        nome = entity.nome,
        email = entity.email,
        telefone = entity.telefone,
        ativo = entity.ativo,
        criadoEm = entity.criadoEm,
        atualizadoEm = entity.atualizadoEm,
        enderecos = enderecos
    )

    fun toEntity(domain: Cliente): ClienteEntity = ClienteEntity(
        id = domain.id,
        nome = domain.nome,
        email = domain.email,
        telefone = domain.telefone,
        ativo = domain.ativo,
        criadoEm = domain.criadoEm,
        atualizadoEm = domain.atualizadoEm
    )
}

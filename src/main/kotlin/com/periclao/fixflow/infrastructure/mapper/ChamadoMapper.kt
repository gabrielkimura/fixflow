package com.periclao.fixflow.infrastructure.mapper

import com.periclao.fixflow.core.model.Chamado
import com.periclao.fixflow.infrastructure.entity.ChamadoEntity

object ChamadoMapper {

    fun toDomain(entity: ChamadoEntity): Chamado = Chamado(
        id = entity.id,
        clienteId = entity.clienteId,
        enderecoId = entity.enderecoId,
        descricao = entity.descricao,
        categoria = entity.categoria,
        status = entity.status,
        tecnicoId = entity.tecnicoId,
        descricaoEncerramento = entity.descricaoEncerramento,
        criadoEm = entity.criadoEm,
        atualizadoEm = entity.atualizadoEm
    )

    fun toEntity(domain: Chamado): ChamadoEntity = ChamadoEntity(
        id = domain.id,
        clienteId = domain.clienteId,
        enderecoId = domain.enderecoId,
        descricao = domain.descricao,
        categoria = domain.categoria,
        status = domain.status,
        tecnicoId = domain.tecnicoId,
        descricaoEncerramento = domain.descricaoEncerramento,
        criadoEm = domain.criadoEm,
        atualizadoEm = domain.atualizadoEm
    )
}

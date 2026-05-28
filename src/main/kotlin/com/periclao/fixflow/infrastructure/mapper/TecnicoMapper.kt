package com.periclao.fixflow.infrastructure.mapper

import com.periclao.fixflow.core.model.Tecnico
import com.periclao.fixflow.infrastructure.entity.TecnicoEntity

object TecnicoMapper {

    fun toDomain(entity: TecnicoEntity): Tecnico = Tecnico(
        id = entity.id,
        nome = entity.nome,
        email = entity.email,
        telefone = entity.telefone,
        especialidades = entity.especialidades.toSet(),
        disponivel = entity.disponivel,
        ativo = entity.ativo,
        criadoEm = entity.criadoEm,
        atualizadoEm = entity.atualizadoEm
    )

    fun toEntity(domain: Tecnico): TecnicoEntity = TecnicoEntity(
        id = domain.id,
        nome = domain.nome,
        email = domain.email,
        telefone = domain.telefone,
        especialidades = domain.especialidades.toMutableSet(),
        disponivel = domain.disponivel,
        ativo = domain.ativo,
        criadoEm = domain.criadoEm,
        atualizadoEm = domain.atualizadoEm
    )
}

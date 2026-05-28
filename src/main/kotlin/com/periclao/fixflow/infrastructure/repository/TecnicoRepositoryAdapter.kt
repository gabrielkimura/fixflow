package com.periclao.fixflow.infrastructure.repository

import com.periclao.fixflow.core.model.Tecnico
import com.periclao.fixflow.core.repository.TecnicoRepositoryPort
import com.periclao.fixflow.infrastructure.mapper.TecnicoMapper
import java.util.UUID

class TecnicoRepositoryAdapter(
    private val jpaRepository: TecnicoJpaRepository
) : TecnicoRepositoryPort {

    override fun salvar(tecnico: Tecnico): Tecnico =
        TecnicoMapper.toEntity(tecnico)
            .let { jpaRepository.save(it) }
            .let { TecnicoMapper.toDomain(it) }

    override fun buscarPorId(id: UUID): Tecnico? =
        jpaRepository.findById(id).orElse(null)?.let { TecnicoMapper.toDomain(it) }

    override fun listar(): List<Tecnico> =
        jpaRepository.findAll().map { TecnicoMapper.toDomain(it) }

    override fun existePorEmail(email: String): Boolean =
        jpaRepository.existsByEmail(email)
}

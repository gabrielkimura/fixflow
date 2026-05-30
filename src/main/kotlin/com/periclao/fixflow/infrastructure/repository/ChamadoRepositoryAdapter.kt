package com.periclao.fixflow.infrastructure.repository

import com.periclao.fixflow.core.model.Chamado
import com.periclao.fixflow.core.repository.ChamadoRepositoryPort
import com.periclao.fixflow.infrastructure.mapper.ChamadoMapper
import java.util.UUID

class ChamadoRepositoryAdapter(
    private val jpaRepository: ChamadoJpaRepository
) : ChamadoRepositoryPort {

    override fun salvar(chamado: Chamado): Chamado =
        ChamadoMapper.toEntity(chamado)
            .let { jpaRepository.save(it) }
            .let { ChamadoMapper.toDomain(it) }

    override fun buscarPorId(id: UUID): Chamado? =
        jpaRepository.findById(id).orElse(null)?.let { ChamadoMapper.toDomain(it) }

    override fun listarPorTecnico(tecnicoId: UUID): List<Chamado> =
        jpaRepository.findByTecnicoId(tecnicoId).map { ChamadoMapper.toDomain(it) }

    override fun listarPorCliente(clienteId: UUID): List<Chamado> =
        jpaRepository.findByClienteId(clienteId).map { ChamadoMapper.toDomain(it) }
}

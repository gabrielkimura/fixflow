package com.periclao.fixflow.infrastructure.repository

import com.periclao.fixflow.core.model.Endereco
import com.periclao.fixflow.core.repository.EnderecoRepositoryPort
import com.periclao.fixflow.infrastructure.mapper.EnderecoMapper
import java.util.UUID

class EnderecoRepositoryAdapter(
    private val jpaRepository: EnderecoJpaRepository
) : EnderecoRepositoryPort {

    override fun salvar(endereco: Endereco): Endereco =
        EnderecoMapper.toEntity(endereco)
            .let { jpaRepository.save(it) }
            .let { EnderecoMapper.toDomain(it) }

    override fun buscarPorId(id: UUID): Endereco? =
        jpaRepository.findById(id).orElse(null)?.let { EnderecoMapper.toDomain(it) }

    override fun listarPorCliente(clienteId: UUID): List<Endereco> =
        jpaRepository.findByClienteId(clienteId).map { EnderecoMapper.toDomain(it) }

    override fun deletar(id: UUID) =
        jpaRepository.deleteById(id)
}

package com.periclao.fixflow.infrastructure.repository

import com.periclao.fixflow.core.model.Cliente
import com.periclao.fixflow.core.repository.ClienteRepositoryPort
import com.periclao.fixflow.infrastructure.mapper.ClienteMapper
import com.periclao.fixflow.infrastructure.mapper.EnderecoMapper
import java.util.UUID

class ClienteRepositoryAdapter(
    private val clienteJpaRepository: ClienteJpaRepository,
    private val enderecoJpaRepository: EnderecoJpaRepository
) : ClienteRepositoryPort {

    override fun salvar(cliente: Cliente): Cliente {
        val entity = clienteJpaRepository.save(ClienteMapper.toEntity(cliente))
        val enderecos = enderecoJpaRepository.findByClienteId(entity.id).map { EnderecoMapper.toDomain(it) }
        return ClienteMapper.toDomain(entity, enderecos)
    }

    override fun buscarPorId(id: UUID): Cliente? {
        val entity = clienteJpaRepository.findById(id).orElse(null) ?: return null
        val enderecos = enderecoJpaRepository.findByClienteId(id).map { EnderecoMapper.toDomain(it) }
        return ClienteMapper.toDomain(entity, enderecos)
    }

    override fun buscarPorEmail(email: String): Cliente? {
        val entity = clienteJpaRepository.findByEmail(email) ?: return null
        val enderecos = enderecoJpaRepository.findByClienteId(entity.id).map { EnderecoMapper.toDomain(it) }
        return ClienteMapper.toDomain(entity, enderecos)
    }

    override fun listar(): List<Cliente> {
        val clientes = clienteJpaRepository.findAll()
        val clienteIds = clientes.map { it.id }.toSet()
        val enderecosPorCliente = enderecoJpaRepository.findByClienteIdIn(clienteIds)
            .groupBy({ it.clienteId }, { EnderecoMapper.toDomain(it) })
        return clientes.map { ClienteMapper.toDomain(it, enderecosPorCliente[it.id] ?: emptyList()) }
    }

    override fun existePorEmail(email: String): Boolean =
        clienteJpaRepository.existsByEmail(email)
}

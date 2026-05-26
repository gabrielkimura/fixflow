package com.periclao.fixflow.infrastructure.repository

import com.periclao.fixflow.infrastructure.entity.EnderecoEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface EnderecoJpaRepository : JpaRepository<EnderecoEntity, UUID> {
    fun findByClienteId(clienteId: UUID): List<EnderecoEntity>
    fun findByClienteIdIn(clienteIds: Set<UUID>): List<EnderecoEntity>
    fun existsByClienteIdAndPrincipalTrue(clienteId: UUID): Boolean
}

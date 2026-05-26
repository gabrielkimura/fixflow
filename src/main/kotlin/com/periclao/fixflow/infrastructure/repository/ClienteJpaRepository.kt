package com.periclao.fixflow.infrastructure.repository

import com.periclao.fixflow.infrastructure.entity.ClienteEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ClienteJpaRepository : JpaRepository<ClienteEntity, UUID> {
    fun findByEmail(email: String): ClienteEntity?
    fun existsByEmail(email: String): Boolean
}

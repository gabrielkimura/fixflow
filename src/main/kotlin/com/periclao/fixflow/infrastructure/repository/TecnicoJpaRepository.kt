package com.periclao.fixflow.infrastructure.repository

import com.periclao.fixflow.infrastructure.entity.TecnicoEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TecnicoJpaRepository : JpaRepository<TecnicoEntity, UUID> {
    fun existsByEmail(email: String): Boolean
}

package com.periclao.fixflow.infrastructure.repository

import com.periclao.fixflow.infrastructure.entity.ChamadoEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ChamadoJpaRepository : JpaRepository<ChamadoEntity, UUID> {
    fun findByTecnicoId(tecnicoId: UUID): List<ChamadoEntity>
    fun findByClienteId(clienteId: UUID): List<ChamadoEntity>
}

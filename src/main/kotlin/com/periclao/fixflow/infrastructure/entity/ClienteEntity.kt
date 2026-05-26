package com.periclao.fixflow.infrastructure.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "clientes")
class ClienteEntity(

    @Id
    val id: UUID,

    @Column(nullable = false)
    var nome: String,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var telefone: String,

    @Column(nullable = false)
    var ativo: Boolean,

    @Column(name = "criado_em", nullable = false, updatable = false)
    val criadoEm: LocalDateTime,

    @Column(name = "atualizado_em", nullable = false)
    var atualizadoEm: LocalDateTime
)

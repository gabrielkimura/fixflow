package com.periclao.fixflow.infrastructure.entity

import com.periclao.fixflow.core.model.enums.Categoria
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "tecnicos")
class TecnicoEntity(

    @Id
    val id: UUID,

    @Column(nullable = false)
    var nome: String,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var telefone: String,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tecnico_especialidades", joinColumns = [JoinColumn(name = "tecnico_id")])
    @Column(name = "categoria")
    @Enumerated(EnumType.STRING)
    var especialidades: MutableSet<Categoria>,

    @Column(nullable = false)
    var disponivel: Boolean,

    @Column(nullable = false)
    var ativo: Boolean,

    @Column(name = "criado_em", nullable = false, updatable = false)
    val criadoEm: LocalDateTime,

    @Column(name = "atualizado_em", nullable = false)
    var atualizadoEm: LocalDateTime
)

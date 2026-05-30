package com.periclao.fixflow.infrastructure.entity

import com.periclao.fixflow.core.model.enums.Categoria
import com.periclao.fixflow.core.model.enums.StatusChamado
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "chamados")
class ChamadoEntity(

    @Id
    val id: UUID,

    @Column(name = "cliente_id", nullable = false)
    val clienteId: UUID,

    @Column(name = "endereco_id", nullable = false)
    val enderecoId: UUID,

    @Column(nullable = false, length = 1000)
    var descricao: String,

    @Column
    @Enumerated(EnumType.STRING)
    var categoria: Categoria?,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: StatusChamado,

    @Column(name = "tecnico_id")
    var tecnicoId: UUID?,

    @Column(name = "descricao_encerramento", length = 1000)
    var descricaoEncerramento: String?,

    @Column(name = "criado_em", nullable = false, updatable = false)
    val criadoEm: LocalDateTime,

    @Column(name = "atualizado_em", nullable = false)
    var atualizadoEm: LocalDateTime
)

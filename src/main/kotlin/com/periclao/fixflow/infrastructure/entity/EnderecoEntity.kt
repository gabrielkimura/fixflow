package com.periclao.fixflow.infrastructure.entity

import com.periclao.fixflow.core.model.UF
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "enderecos")
class EnderecoEntity(

    @Id
    val id: UUID,

    @Column(name = "cliente_id", nullable = false)
    val clienteId: UUID,

    @Column(nullable = false)
    var logradouro: String,

    @Column(nullable = false)
    var numero: String,

    @Column
    var complemento: String?,

    @Column(nullable = false)
    var bairro: String,

    @Column(nullable = false)
    var cidade: String,

    @Column(nullable = false, length = 2)
    @Enumerated(EnumType.STRING)
    var uf: UF,

    @Column(nullable = false, length = 8)
    var cep: String,

    @Column(nullable = false)
    var principal: Boolean
)

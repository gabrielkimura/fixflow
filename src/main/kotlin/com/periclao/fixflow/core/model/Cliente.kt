package com.periclao.fixflow.core.model

import java.time.LocalDateTime
import java.util.UUID

data class Cliente(
    val id: UUID,
    val nome: String,
    val email: String,
    val telefone: String,
    val ativo: Boolean,
    val criadoEm: LocalDateTime,
    val atualizadoEm: LocalDateTime,
    val enderecos: List<Endereco> = emptyList()
)

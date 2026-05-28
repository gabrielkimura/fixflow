package com.periclao.fixflow.core.model

import com.periclao.fixflow.core.model.enums.Categoria
import java.time.LocalDateTime
import java.util.UUID

data class Tecnico(
    val id: UUID,
    val nome: String,
    val email: String,
    val telefone: String,
    val especialidades: Set<Categoria>,
    val disponivel: Boolean,
    val ativo: Boolean,
    val criadoEm: LocalDateTime,
    val atualizadoEm: LocalDateTime
)

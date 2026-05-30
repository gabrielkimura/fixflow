package com.periclao.fixflow.core.model

import com.periclao.fixflow.core.model.enums.Categoria
import com.periclao.fixflow.core.model.enums.StatusChamado
import java.time.LocalDateTime
import java.util.UUID

data class Chamado(
    val id: UUID,
    val clienteId: UUID,
    val enderecoId: UUID,
    val descricao: String,
    val categoria: Categoria?,
    val status: StatusChamado,
    val tecnicoId: UUID?,
    val descricaoEncerramento: String?,
    val criadoEm: LocalDateTime,
    val atualizadoEm: LocalDateTime
)

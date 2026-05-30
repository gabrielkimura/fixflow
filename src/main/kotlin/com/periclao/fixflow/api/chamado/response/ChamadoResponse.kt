package com.periclao.fixflow.api.chamado.response

import com.periclao.fixflow.core.model.Chamado
import com.periclao.fixflow.core.model.enums.Categoria
import com.periclao.fixflow.core.model.enums.StatusChamado
import java.time.LocalDateTime
import java.util.UUID

data class ChamadoResponse(
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
) {
    companion object {
        fun from(chamado: Chamado) = ChamadoResponse(
            id = chamado.id,
            clienteId = chamado.clienteId,
            enderecoId = chamado.enderecoId,
            descricao = chamado.descricao,
            categoria = chamado.categoria,
            status = chamado.status,
            tecnicoId = chamado.tecnicoId,
            descricaoEncerramento = chamado.descricaoEncerramento,
            criadoEm = chamado.criadoEm,
            atualizadoEm = chamado.atualizadoEm
        )
    }
}

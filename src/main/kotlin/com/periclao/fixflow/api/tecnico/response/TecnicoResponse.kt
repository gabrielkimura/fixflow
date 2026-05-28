package com.periclao.fixflow.api.tecnico.response

import com.periclao.fixflow.core.model.Tecnico
import com.periclao.fixflow.core.model.enums.Categoria
import java.time.LocalDateTime
import java.util.UUID

data class TecnicoResponse(
    val id: UUID,
    val nome: String,
    val email: String,
    val telefone: String,
    val especialidades: Set<Categoria>,
    val disponivel: Boolean,
    val ativo: Boolean,
    val criadoEm: LocalDateTime,
    val atualizadoEm: LocalDateTime
) {
    companion object {
        fun from(tecnico: Tecnico) = TecnicoResponse(
            id = tecnico.id,
            nome = tecnico.nome,
            email = tecnico.email,
            telefone = tecnico.telefone,
            especialidades = tecnico.especialidades,
            disponivel = tecnico.disponivel,
            ativo = tecnico.ativo,
            criadoEm = tecnico.criadoEm,
            atualizadoEm = tecnico.atualizadoEm
        )
    }
}

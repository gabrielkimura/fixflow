package com.periclao.fixflow.api.chamado.request

import com.periclao.fixflow.core.usecase.chamado.AtribuirTecnicoChamadoUseCase
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class AtribuirTecnicoChamadoRequest(

    @field:NotNull(message = "Técnico é obrigatório")
    val tecnicoId: UUID?
) {
    fun toCommand(chamadoId: UUID) = AtribuirTecnicoChamadoUseCase.Command(
        chamadoId = chamadoId,
        tecnicoId = tecnicoId!!
    )
}

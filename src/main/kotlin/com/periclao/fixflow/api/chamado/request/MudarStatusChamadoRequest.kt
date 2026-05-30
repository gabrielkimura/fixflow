package com.periclao.fixflow.api.chamado.request

import com.periclao.fixflow.core.model.enums.StatusChamado
import com.periclao.fixflow.core.usecase.chamado.MudarStatusChamadoUseCase
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class MudarStatusChamadoRequest(

    @field:NotNull(message = "Novo status é obrigatório")
    val novoStatus: StatusChamado?
) {
    fun toCommand(chamadoId: UUID) = MudarStatusChamadoUseCase.Command(
        chamadoId = chamadoId,
        novoStatus = novoStatus!!
    )
}

package com.periclao.fixflow.api.chamado.request

import com.periclao.fixflow.core.usecase.chamado.ConcluirChamadoUseCase
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.UUID

data class ConcluirChamadoRequest(

    @field:NotBlank(message = "Descrição de encerramento é obrigatória")
    @field:Size(min = 10, max = 1000, message = "Descrição de encerramento deve ter entre 10 e 1000 caracteres")
    val descricaoEncerramento: String
) {
    fun toCommand(chamadoId: UUID) = ConcluirChamadoUseCase.Command(
        chamadoId = chamadoId,
        descricaoEncerramento = descricaoEncerramento
    )
}

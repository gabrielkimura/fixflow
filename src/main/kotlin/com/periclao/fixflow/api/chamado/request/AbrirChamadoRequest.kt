package com.periclao.fixflow.api.chamado.request

import com.periclao.fixflow.core.usecase.chamado.AbrirChamadoUseCase
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class AbrirChamadoRequest(

    @field:NotNull(message = "Cliente é obrigatório")
    val clienteId: UUID?,

    @field:NotNull(message = "Endereço é obrigatório")
    val enderecoId: UUID?,

    @field:NotBlank(message = "Descrição é obrigatória")
    @field:Size(min = 10, max = 1000, message = "Descrição deve ter entre 10 e 1000 caracteres")
    val descricao: String
) {
    fun toCommand() = AbrirChamadoUseCase.Command(
        clienteId = clienteId!!,
        enderecoId = enderecoId!!,
        descricao = descricao
    )
}

package com.periclao.fixflow.api.cliente.request

import com.periclao.fixflow.core.usecase.cliente.AtualizarClienteUseCase
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.util.UUID

data class AtualizarClienteRequest(

    @field:NotBlank(message = "Nome é obrigatório")
    @field:Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    val nome: String,

    @field:NotBlank(message = "E-mail é obrigatório")
    @field:Email(message = "E-mail inválido")
    val email: String,

    @field:NotBlank(message = "Telefone é obrigatório")
    @field:Pattern(regexp = "\\d{10,11}", message = "Telefone deve conter 10 ou 11 dígitos")
    val telefone: String
) {
    fun toCommand(id: UUID) = AtualizarClienteUseCase.Command(
        id = id,
        nome = nome,
        email = email,
        telefone = telefone
    )
}

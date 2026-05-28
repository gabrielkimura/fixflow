package com.periclao.fixflow.api.tecnico.request

import com.periclao.fixflow.core.model.enums.Categoria
import com.periclao.fixflow.core.usecase.tecnico.CriarTecnicoUseCase
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CriarTecnicoRequest(

    @field:NotBlank(message = "Nome é obrigatório")
    @field:Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    val nome: String,

    @field:NotBlank(message = "E-mail é obrigatório")
    @field:Email(message = "E-mail inválido")
    val email: String,

    @field:NotBlank(message = "Telefone é obrigatório")
    @field:Pattern(regexp = "\\d{10,11}", message = "Telefone deve conter 10 ou 11 dígitos")
    val telefone: String,

    @field:NotEmpty(message = "Pelo menos uma especialidade é obrigatória")
    val especialidades: Set<Categoria>
) {
    fun toCommand() = CriarTecnicoUseCase.Command(
        nome = nome,
        email = email,
        telefone = telefone,
        especialidades = especialidades
    )
}

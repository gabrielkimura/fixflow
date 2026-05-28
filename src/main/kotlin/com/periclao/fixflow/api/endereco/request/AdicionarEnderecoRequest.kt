package com.periclao.fixflow.api.endereco.request

import com.periclao.fixflow.core.model.enums.UF
import com.periclao.fixflow.core.usecase.endereco.AdicionarEnderecoUseCase
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import java.util.UUID

data class AdicionarEnderecoRequest(

    @field:NotBlank(message = "Logradouro é obrigatório")
    val logradouro: String,

    @field:NotBlank(message = "Número é obrigatório")
    val numero: String,

    val complemento: String?,

    @field:NotBlank(message = "Bairro é obrigatório")
    val bairro: String,

    @field:NotBlank(message = "Cidade é obrigatória")
    val cidade: String,

    @field:NotNull(message = "UF é obrigatória")
    val uf: UF,

    @field:Pattern(regexp = "\\d{8}", message = "CEP deve conter 8 dígitos numéricos")
    val cep: String,

    val principal: Boolean = false
) {
    fun toCommand(clienteId: UUID) = AdicionarEnderecoUseCase.Command(
        clienteId = clienteId,
        logradouro = logradouro,
        numero = numero,
        complemento = complemento,
        bairro = bairro,
        cidade = cidade,
        uf = uf,
        cep = cep,
        principal = principal
    )
}

package com.periclao.fixflow.core.model

import java.util.UUID

data class Endereco(
    val id: UUID,
    val clienteId: UUID,
    val logradouro: String,
    val numero: String,
    val complemento: String?,
    val bairro: String,
    val cidade: String,
    val uf: UF,
    val cep: String,
    val principal: Boolean
)

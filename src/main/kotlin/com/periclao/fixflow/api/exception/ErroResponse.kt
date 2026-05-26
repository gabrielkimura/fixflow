package com.periclao.fixflow.api.exception

import java.time.LocalDateTime

data class ErroResponse(
    val status: Int,
    val mensagem: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val erros: List<String> = emptyList()
)

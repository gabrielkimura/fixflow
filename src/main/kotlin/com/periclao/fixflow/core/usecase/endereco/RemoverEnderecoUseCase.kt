package com.periclao.fixflow.core.usecase.endereco

import java.util.UUID

interface RemoverEnderecoUseCase {
    fun executar(id: UUID)
}

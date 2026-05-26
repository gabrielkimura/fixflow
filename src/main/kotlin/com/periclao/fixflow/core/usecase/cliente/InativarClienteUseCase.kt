package com.periclao.fixflow.core.usecase.cliente

import com.periclao.fixflow.core.model.Cliente
import java.util.UUID

interface InativarClienteUseCase {
    fun executar(id: UUID): Cliente
}

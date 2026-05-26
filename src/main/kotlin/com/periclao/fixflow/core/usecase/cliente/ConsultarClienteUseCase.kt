package com.periclao.fixflow.core.usecase.cliente

import com.periclao.fixflow.core.model.Cliente
import java.util.UUID

interface ConsultarClienteUseCase {
    fun buscarPorId(id: UUID): Cliente
    fun listarTodos(): List<Cliente>
}

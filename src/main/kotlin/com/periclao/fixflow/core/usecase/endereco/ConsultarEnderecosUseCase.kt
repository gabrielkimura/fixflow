package com.periclao.fixflow.core.usecase.endereco

import com.periclao.fixflow.core.model.Endereco
import java.util.UUID

interface ConsultarEnderecosUseCase {
    fun buscarPorId(id: UUID): Endereco
    fun listarPorCliente(clienteId: UUID): List<Endereco>
}

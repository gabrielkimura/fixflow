package com.periclao.fixflow.core.usecase.chamado

import com.periclao.fixflow.core.model.Chamado
import java.util.UUID

interface ConsultarChamadoUseCase {
    fun buscarPorId(id: UUID): Chamado
    fun listarPorTecnico(tecnicoId: UUID): List<Chamado>
    fun listarPorCliente(clienteId: UUID): List<Chamado>
}

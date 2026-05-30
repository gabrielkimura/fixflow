package com.periclao.fixflow.core.repository

import com.periclao.fixflow.core.model.Chamado
import java.util.UUID

interface ChamadoRepositoryPort {
    fun salvar(chamado: Chamado): Chamado
    fun buscarPorId(id: UUID): Chamado?
    fun listarPorTecnico(tecnicoId: UUID): List<Chamado>
    fun listarPorCliente(clienteId: UUID): List<Chamado>
}

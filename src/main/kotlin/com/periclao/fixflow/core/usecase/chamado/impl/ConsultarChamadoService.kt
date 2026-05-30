package com.periclao.fixflow.core.usecase.chamado.impl

import com.periclao.fixflow.core.exception.ChamadoNaoEncontradoException
import com.periclao.fixflow.core.model.Chamado
import com.periclao.fixflow.core.repository.ChamadoRepositoryPort
import com.periclao.fixflow.core.usecase.chamado.ConsultarChamadoUseCase
import java.util.UUID

class ConsultarChamadoService(
    private val chamadoRepository: ChamadoRepositoryPort
) : ConsultarChamadoUseCase {

    override fun buscarPorId(id: UUID): Chamado =
        chamadoRepository.buscarPorId(id) ?: throw ChamadoNaoEncontradoException(id)

    override fun listarPorTecnico(tecnicoId: UUID): List<Chamado> =
        chamadoRepository.listarPorTecnico(tecnicoId)

    override fun listarPorCliente(clienteId: UUID): List<Chamado> =
        chamadoRepository.listarPorCliente(clienteId)
}

package com.periclao.fixflow.core.usecase.chamado.impl

import com.periclao.fixflow.core.exception.ChamadoNaoEncontradoException
import com.periclao.fixflow.core.exception.TransicaoRequerOperacaoDedicadaException
import com.periclao.fixflow.core.exception.TransicaoStatusInvalidaException
import com.periclao.fixflow.core.model.Chamado
import com.periclao.fixflow.core.model.enums.StatusChamado
import com.periclao.fixflow.core.repository.ChamadoRepositoryPort
import com.periclao.fixflow.core.usecase.chamado.MudarStatusChamadoUseCase
import java.time.LocalDateTime

class MudarStatusChamadoService(
    private val chamadoRepository: ChamadoRepositoryPort
) : MudarStatusChamadoUseCase {

    override fun executar(command: MudarStatusChamadoUseCase.Command): Chamado {
        if (command.novoStatus !in STATUS_SEM_PAYLOAD) {
            throw TransicaoRequerOperacaoDedicadaException(command.novoStatus)
        }

        val chamado = chamadoRepository.buscarPorId(command.chamadoId)
            ?: throw ChamadoNaoEncontradoException(command.chamadoId)

        if (!chamado.status.podeTransicionarPara(command.novoStatus)) {
            throw TransicaoStatusInvalidaException(chamado.status, command.novoStatus)
        }

        val chamadoAtualizado = chamado.copy(
            status = command.novoStatus,
            atualizadoEm = LocalDateTime.now()
        )

        return chamadoRepository.salvar(chamadoAtualizado)
    }

    private companion object {
        // States reached by a dedicated use case are excluded: assigning a technician,
        // completing (requires closing description) and cancelling.
        val STATUS_SEM_PAYLOAD = setOf(StatusChamado.EM_ANALISE, StatusChamado.EM_ANDAMENTO)
    }
}

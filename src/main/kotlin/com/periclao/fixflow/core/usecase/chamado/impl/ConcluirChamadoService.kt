package com.periclao.fixflow.core.usecase.chamado.impl

import com.periclao.fixflow.core.exception.ChamadoNaoEncontradoException
import com.periclao.fixflow.core.exception.DescricaoEncerramentoObrigatoriaException
import com.periclao.fixflow.core.exception.TransicaoStatusInvalidaException
import com.periclao.fixflow.core.model.Chamado
import com.periclao.fixflow.core.model.enums.StatusChamado
import com.periclao.fixflow.core.repository.ChamadoRepositoryPort
import com.periclao.fixflow.core.usecase.chamado.ConcluirChamadoUseCase
import java.time.LocalDateTime

class ConcluirChamadoService(
    private val chamadoRepository: ChamadoRepositoryPort
) : ConcluirChamadoUseCase {

    override fun executar(command: ConcluirChamadoUseCase.Command): Chamado {
        if (command.descricaoEncerramento.isBlank()) {
            throw DescricaoEncerramentoObrigatoriaException()
        }

        val chamado = chamadoRepository.buscarPorId(command.chamadoId)
            ?: throw ChamadoNaoEncontradoException(command.chamadoId)

        if (!chamado.status.podeTransicionarPara(StatusChamado.CONCLUIDO)) {
            throw TransicaoStatusInvalidaException(chamado.status, StatusChamado.CONCLUIDO)
        }

        val chamadoConcluido = chamado.copy(
            status = StatusChamado.CONCLUIDO,
            descricaoEncerramento = command.descricaoEncerramento,
            atualizadoEm = LocalDateTime.now()
        )

        return chamadoRepository.salvar(chamadoConcluido)
    }
}

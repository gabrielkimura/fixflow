package com.periclao.fixflow.core.usecase.chamado.impl

import com.periclao.fixflow.core.exception.ChamadoNaoEncontradoException
import com.periclao.fixflow.core.exception.TransicaoStatusInvalidaException
import com.periclao.fixflow.core.model.Chamado
import com.periclao.fixflow.core.model.enums.StatusChamado
import com.periclao.fixflow.core.repository.ChamadoRepositoryPort
import com.periclao.fixflow.core.usecase.chamado.CancelarChamadoUseCase
import java.time.LocalDateTime
import java.util.UUID

class CancelarChamadoService(
    private val chamadoRepository: ChamadoRepositoryPort
) : CancelarChamadoUseCase {

    override fun executar(chamadoId: UUID): Chamado {
        val chamado = chamadoRepository.buscarPorId(chamadoId)
            ?: throw ChamadoNaoEncontradoException(chamadoId)

        if (!chamado.status.podeTransicionarPara(StatusChamado.CANCELADO)) {
            throw TransicaoStatusInvalidaException(chamado.status, StatusChamado.CANCELADO)
        }

        val chamadoCancelado = chamado.copy(
            status = StatusChamado.CANCELADO,
            atualizadoEm = LocalDateTime.now()
        )

        return chamadoRepository.salvar(chamadoCancelado)
    }
}

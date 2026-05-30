package com.periclao.fixflow.core.usecase.chamado.impl

import com.periclao.fixflow.core.exception.ChamadoNaoEncontradoException
import com.periclao.fixflow.core.exception.TecnicoInativoException
import com.periclao.fixflow.core.exception.TecnicoNaoEncontradoException
import com.periclao.fixflow.core.exception.TransicaoStatusInvalidaException
import com.periclao.fixflow.core.model.Chamado
import com.periclao.fixflow.core.model.enums.StatusChamado
import com.periclao.fixflow.core.repository.ChamadoRepositoryPort
import com.periclao.fixflow.core.repository.TecnicoRepositoryPort
import com.periclao.fixflow.core.usecase.chamado.AtribuirTecnicoChamadoUseCase
import java.time.LocalDateTime

class AtribuirTecnicoChamadoService(
    private val chamadoRepository: ChamadoRepositoryPort,
    private val tecnicoRepository: TecnicoRepositoryPort
) : AtribuirTecnicoChamadoUseCase {

    override fun executar(command: AtribuirTecnicoChamadoUseCase.Command): Chamado {
        val chamado = chamadoRepository.buscarPorId(command.chamadoId)
            ?: throw ChamadoNaoEncontradoException(command.chamadoId)

        if (!chamado.status.podeTransicionarPara(StatusChamado.TECNICO_ATRIBUIDO)) {
            throw TransicaoStatusInvalidaException(chamado.status, StatusChamado.TECNICO_ATRIBUIDO)
        }

        val tecnico = tecnicoRepository.buscarPorId(command.tecnicoId)
            ?: throw TecnicoNaoEncontradoException(command.tecnicoId)

        if (!tecnico.ativo) throw TecnicoInativoException(command.tecnicoId)

        val chamadoAtribuido = chamado.copy(
            tecnicoId = tecnico.id,
            status = StatusChamado.TECNICO_ATRIBUIDO,
            atualizadoEm = LocalDateTime.now()
        )

        return chamadoRepository.salvar(chamadoAtribuido)
    }
}

package com.periclao.fixflow.core.usecase.chamado.impl

import com.periclao.fixflow.core.exception.ClienteInativoException
import com.periclao.fixflow.core.exception.ClienteNaoEncontradoException
import com.periclao.fixflow.core.exception.EnderecoNaoEncontradoException
import com.periclao.fixflow.core.model.Chamado
import com.periclao.fixflow.core.model.enums.StatusChamado
import com.periclao.fixflow.core.repository.ChamadoRepositoryPort
import com.periclao.fixflow.core.repository.ClienteRepositoryPort
import com.periclao.fixflow.core.repository.EnderecoRepositoryPort
import com.periclao.fixflow.core.usecase.chamado.AbrirChamadoUseCase
import com.periclao.fixflow.core.usecase.chamado.CategorizadorChamado
import java.time.LocalDateTime
import java.util.UUID

class AbrirChamadoService(
    private val chamadoRepository: ChamadoRepositoryPort,
    private val clienteRepository: ClienteRepositoryPort,
    private val enderecoRepository: EnderecoRepositoryPort
) : AbrirChamadoUseCase {

    override fun executar(command: AbrirChamadoUseCase.Command): Chamado {
        val cliente = clienteRepository.buscarPorId(command.clienteId)
            ?: throw ClienteNaoEncontradoException(command.clienteId)

        if (!cliente.ativo) throw ClienteInativoException(command.clienteId)

        val endereco = enderecoRepository.buscarPorId(command.enderecoId)
            ?: throw EnderecoNaoEncontradoException(command.enderecoId)

        if (endereco.clienteId != command.clienteId) {
            throw EnderecoNaoEncontradoException(command.enderecoId)
        }

        val agora = LocalDateTime.now()
        val novoChamado = Chamado(
            id = UUID.randomUUID(),
            clienteId = command.clienteId,
            enderecoId = command.enderecoId,
            descricao = command.descricao,
            categoria = CategorizadorChamado.categorizar(command.descricao),
            status = StatusChamado.ABERTO,
            tecnicoId = null,
            descricaoEncerramento = null,
            criadoEm = agora,
            atualizadoEm = agora
        )

        return chamadoRepository.salvar(novoChamado)
    }
}

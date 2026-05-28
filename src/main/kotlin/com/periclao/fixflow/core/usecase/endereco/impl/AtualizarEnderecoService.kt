package com.periclao.fixflow.core.usecase.endereco.impl

import com.periclao.fixflow.core.exception.ClienteInativoException
import com.periclao.fixflow.core.exception.ClienteNaoEncontradoException
import com.periclao.fixflow.core.exception.EnderecoNaoEncontradoException
import com.periclao.fixflow.core.model.Endereco
import com.periclao.fixflow.core.repository.ClienteRepositoryPort
import com.periclao.fixflow.core.repository.EnderecoRepositoryPort
import com.periclao.fixflow.core.usecase.endereco.AtualizarEnderecoUseCase

class AtualizarEnderecoService(
    private val clienteRepository: ClienteRepositoryPort,
    private val enderecoRepository: EnderecoRepositoryPort
) : AtualizarEnderecoUseCase {

    override fun executar(command: AtualizarEnderecoUseCase.Command): Endereco {
        val endereco = enderecoRepository.buscarPorId(command.id)
            ?: throw EnderecoNaoEncontradoException(command.id)

        val cliente = clienteRepository.buscarPorId(endereco.clienteId)
            ?: throw ClienteNaoEncontradoException(endereco.clienteId)

        if (!cliente.ativo) throw ClienteInativoException(endereco.clienteId)

        if (command.principal && !endereco.principal) {
            DesmarcarEnderecoPrincipal.executar(enderecoRepository, endereco.clienteId, command.id)
        }

        val enderecoAtualizado = endereco.copy(
            logradouro = command.logradouro,
            numero = command.numero,
            complemento = command.complemento,
            bairro = command.bairro,
            cidade = command.cidade,
            uf = command.uf,
            cep = command.cep,
            principal = command.principal
        )

        return enderecoRepository.salvar(enderecoAtualizado)
    }
}

package com.periclao.fixflow.core.usecase.endereco.impl

import com.periclao.fixflow.core.exception.ClienteInativoException
import com.periclao.fixflow.core.exception.ClienteNaoEncontradoException
import com.periclao.fixflow.core.model.Endereco
import com.periclao.fixflow.core.repository.ClienteRepositoryPort
import com.periclao.fixflow.core.repository.EnderecoRepositoryPort
import com.periclao.fixflow.core.usecase.endereco.AdicionarEnderecoUseCase
import java.util.UUID

class AdicionarEnderecoService(
    private val clienteRepository: ClienteRepositoryPort,
    private val enderecoRepository: EnderecoRepositoryPort
) : AdicionarEnderecoUseCase {

    override fun executar(command: AdicionarEnderecoUseCase.Command): Endereco {
        val cliente = clienteRepository.buscarPorId(command.clienteId)
            ?: throw ClienteNaoEncontradoException(command.clienteId)

        if (!cliente.ativo) throw ClienteInativoException(command.clienteId)

        if (command.principal) desmarcarPrincipalAtual(command.clienteId)

        val novoEndereco = Endereco(
            id = UUID.randomUUID(),
            clienteId = command.clienteId,
            logradouro = command.logradouro,
            numero = command.numero,
            complemento = command.complemento,
            bairro = command.bairro,
            cidade = command.cidade,
            uf = command.uf,
            cep = command.cep,
            principal = command.principal
        )

        return enderecoRepository.salvar(novoEndereco)
    }

    private fun desmarcarPrincipalAtual(clienteId: UUID) {
        enderecoRepository.listarPorCliente(clienteId)
            .filter { it.principal }
            .forEach { enderecoRepository.salvar(it.copy(principal = false)) }
    }
}

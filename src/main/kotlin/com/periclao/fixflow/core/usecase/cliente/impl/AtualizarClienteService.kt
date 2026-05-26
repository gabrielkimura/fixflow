package com.periclao.fixflow.core.usecase.cliente.impl

import com.periclao.fixflow.core.exception.ClienteInativoException
import com.periclao.fixflow.core.exception.ClienteNaoEncontradoException
import com.periclao.fixflow.core.exception.EmailJaCadastradoException
import com.periclao.fixflow.core.model.Cliente
import com.periclao.fixflow.core.repository.ClienteRepositoryPort
import com.periclao.fixflow.core.usecase.cliente.AtualizarClienteUseCase
import java.time.LocalDateTime

class AtualizarClienteService(
    private val clienteRepository: ClienteRepositoryPort
) : AtualizarClienteUseCase {

    override fun executar(command: AtualizarClienteUseCase.Command): Cliente {
        val cliente = clienteRepository.buscarPorId(command.id)
            ?: throw ClienteNaoEncontradoException(command.id)

        if (!cliente.ativo) throw ClienteInativoException(command.id)

        val emailAlterado = cliente.email != command.email
        if (emailAlterado && clienteRepository.existePorEmail(command.email)) {
            throw EmailJaCadastradoException(command.email)
        }

        val clienteAtualizado = cliente.copy(
            nome = command.nome,
            email = command.email,
            telefone = command.telefone,
            atualizadoEm = LocalDateTime.now()
        )

        return clienteRepository.salvar(clienteAtualizado)
    }
}

package com.periclao.fixflow.core.usecase.cliente.impl

import com.periclao.fixflow.core.exception.EmailJaCadastradoException
import com.periclao.fixflow.core.model.Cliente
import com.periclao.fixflow.core.repository.ClienteRepositoryPort
import com.periclao.fixflow.core.usecase.cliente.CriarClienteUseCase
import java.time.LocalDateTime
import java.util.UUID

class CriarClienteService(
    private val clienteRepository: ClienteRepositoryPort
) : CriarClienteUseCase {

    override fun executar(command: CriarClienteUseCase.Command): Cliente {
        if (clienteRepository.existePorEmail(command.email)) {
            throw EmailJaCadastradoException(command.email)
        }

        val agora = LocalDateTime.now()
        val novoCliente = Cliente(
            id = UUID.randomUUID(),
            nome = command.nome,
            email = command.email,
            telefone = command.telefone,
            ativo = true,
            criadoEm = agora,
            atualizadoEm = agora
        )

        return clienteRepository.salvar(novoCliente)
    }
}

package com.periclao.fixflow.core.usecase.cliente.impl

import com.periclao.fixflow.core.exception.ClienteNaoEncontradoException
import com.periclao.fixflow.core.model.Cliente
import com.periclao.fixflow.core.repository.ClienteRepositoryPort
import com.periclao.fixflow.core.usecase.cliente.InativarClienteUseCase
import java.time.LocalDateTime
import java.util.UUID

class InativarClienteService(
    private val clienteRepository: ClienteRepositoryPort
) : InativarClienteUseCase {

    override fun executar(id: UUID): Cliente {
        val cliente = clienteRepository.buscarPorId(id)
            ?: throw ClienteNaoEncontradoException(id)

        if (!cliente.ativo) return cliente

        val clienteInativado = cliente.copy(
            ativo = false,
            atualizadoEm = LocalDateTime.now()
        )

        return clienteRepository.salvar(clienteInativado)
    }
}

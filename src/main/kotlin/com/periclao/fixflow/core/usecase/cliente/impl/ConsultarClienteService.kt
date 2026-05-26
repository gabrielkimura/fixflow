package com.periclao.fixflow.core.usecase.cliente.impl

import com.periclao.fixflow.core.exception.ClienteNaoEncontradoException
import com.periclao.fixflow.core.model.Cliente
import com.periclao.fixflow.core.repository.ClienteRepositoryPort
import com.periclao.fixflow.core.usecase.cliente.ConsultarClienteUseCase
import java.util.UUID

class ConsultarClienteService(
    private val clienteRepository: ClienteRepositoryPort
) : ConsultarClienteUseCase {

    override fun buscarPorId(id: UUID): Cliente =
        clienteRepository.buscarPorId(id) ?: throw ClienteNaoEncontradoException(id)

    override fun listarTodos(): List<Cliente> =
        clienteRepository.listar()
}

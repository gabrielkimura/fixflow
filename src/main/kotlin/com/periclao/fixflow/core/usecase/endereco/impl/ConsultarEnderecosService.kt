package com.periclao.fixflow.core.usecase.endereco.impl

import com.periclao.fixflow.core.exception.EnderecoNaoEncontradoException
import com.periclao.fixflow.core.model.Endereco
import com.periclao.fixflow.core.repository.EnderecoRepositoryPort
import com.periclao.fixflow.core.usecase.endereco.ConsultarEnderecosUseCase
import java.util.UUID

class ConsultarEnderecosService(
    private val enderecoRepository: EnderecoRepositoryPort
) : ConsultarEnderecosUseCase {

    override fun buscarPorId(id: UUID): Endereco =
        enderecoRepository.buscarPorId(id) ?: throw EnderecoNaoEncontradoException(id)

    override fun listarPorCliente(clienteId: UUID): List<Endereco> =
        enderecoRepository.listarPorCliente(clienteId)
}

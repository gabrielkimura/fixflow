package com.periclao.fixflow.core.usecase.endereco.impl

import com.periclao.fixflow.core.exception.EnderecoNaoEncontradoException
import com.periclao.fixflow.core.repository.EnderecoRepositoryPort
import com.periclao.fixflow.core.usecase.endereco.RemoverEnderecoUseCase
import java.util.UUID

class RemoverEnderecoService(
    private val enderecoRepository: EnderecoRepositoryPort
) : RemoverEnderecoUseCase {

    override fun executar(id: UUID) {
        enderecoRepository.buscarPorId(id) ?: throw EnderecoNaoEncontradoException(id)
        enderecoRepository.deletar(id)
    }
}

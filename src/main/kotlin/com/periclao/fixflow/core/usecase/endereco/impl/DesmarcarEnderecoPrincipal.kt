package com.periclao.fixflow.core.usecase.endereco.impl

import com.periclao.fixflow.core.repository.EnderecoRepositoryPort
import java.util.UUID

object DesmarcarEnderecoPrincipal {

    fun executar(enderecoRepository: EnderecoRepositoryPort, clienteId: UUID, excluirId: UUID? = null) {
        enderecoRepository.listarPorCliente(clienteId)
            .filter { it.principal && it.id != excluirId }
            .forEach { enderecoRepository.salvar(it.copy(principal = false)) }
    }
}

package com.periclao.fixflow.core.repository

import com.periclao.fixflow.core.model.Endereco
import java.util.UUID

interface EnderecoRepositoryPort {
    fun salvar(endereco: Endereco): Endereco
    fun buscarPorId(id: UUID): Endereco?
    fun listarPorCliente(clienteId: UUID): List<Endereco>
    fun deletar(id: UUID)
    fun existeEnderecoPrincipal(clienteId: UUID): Boolean
}

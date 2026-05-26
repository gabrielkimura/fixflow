package com.periclao.fixflow.core.repository

import com.periclao.fixflow.core.model.Cliente
import java.util.UUID

interface ClienteRepositoryPort {
    fun salvar(cliente: Cliente): Cliente
    fun buscarPorId(id: UUID): Cliente?
    fun buscarPorEmail(email: String): Cliente?
    fun listar(): List<Cliente>
    fun existePorEmail(email: String): Boolean
}

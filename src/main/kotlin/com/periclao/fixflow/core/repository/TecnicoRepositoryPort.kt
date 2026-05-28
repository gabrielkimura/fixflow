package com.periclao.fixflow.core.repository

import com.periclao.fixflow.core.model.Tecnico
import java.util.UUID

interface TecnicoRepositoryPort {
    fun salvar(tecnico: Tecnico): Tecnico
    fun buscarPorId(id: UUID): Tecnico?
    fun listar(): List<Tecnico>
    fun existePorEmail(email: String): Boolean
}

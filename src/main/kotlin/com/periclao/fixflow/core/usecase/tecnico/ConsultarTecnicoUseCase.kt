package com.periclao.fixflow.core.usecase.tecnico

import com.periclao.fixflow.core.model.Tecnico
import java.util.UUID

interface ConsultarTecnicoUseCase {
    fun buscarPorId(id: UUID): Tecnico
    fun listarTodos(): List<Tecnico>
}

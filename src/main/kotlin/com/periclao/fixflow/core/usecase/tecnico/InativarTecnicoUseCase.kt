package com.periclao.fixflow.core.usecase.tecnico

import com.periclao.fixflow.core.model.Tecnico
import java.util.UUID

interface InativarTecnicoUseCase {
    fun executar(id: UUID): Tecnico
}

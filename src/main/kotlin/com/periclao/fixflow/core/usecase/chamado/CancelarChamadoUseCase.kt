package com.periclao.fixflow.core.usecase.chamado

import com.periclao.fixflow.core.model.Chamado
import java.util.UUID

interface CancelarChamadoUseCase {
    fun executar(chamadoId: UUID): Chamado
}

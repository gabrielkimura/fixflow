package com.periclao.fixflow.core.usecase.chamado

import com.periclao.fixflow.core.model.Chamado
import com.periclao.fixflow.core.model.enums.StatusChamado
import java.util.UUID

interface MudarStatusChamadoUseCase {
    fun executar(command: Command): Chamado

    data class Command(
        val chamadoId: UUID,
        val novoStatus: StatusChamado
    )
}

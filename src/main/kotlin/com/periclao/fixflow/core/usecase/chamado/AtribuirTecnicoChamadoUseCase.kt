package com.periclao.fixflow.core.usecase.chamado

import com.periclao.fixflow.core.model.Chamado
import java.util.UUID

interface AtribuirTecnicoChamadoUseCase {
    fun executar(command: Command): Chamado

    data class Command(
        val chamadoId: UUID,
        val tecnicoId: UUID
    )
}

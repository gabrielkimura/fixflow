package com.periclao.fixflow.core.usecase.tecnico.impl

import com.periclao.fixflow.core.exception.TecnicoNaoEncontradoException
import com.periclao.fixflow.core.model.Tecnico
import com.periclao.fixflow.core.repository.TecnicoRepositoryPort
import com.periclao.fixflow.core.usecase.tecnico.ConsultarTecnicoUseCase
import java.util.UUID

class ConsultarTecnicoService(
    private val tecnicoRepository: TecnicoRepositoryPort
) : ConsultarTecnicoUseCase {

    override fun buscarPorId(id: UUID): Tecnico =
        tecnicoRepository.buscarPorId(id) ?: throw TecnicoNaoEncontradoException(id)

    override fun listarTodos(): List<Tecnico> =
        tecnicoRepository.listar()
}

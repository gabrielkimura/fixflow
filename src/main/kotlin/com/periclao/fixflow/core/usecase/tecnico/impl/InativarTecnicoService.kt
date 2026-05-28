package com.periclao.fixflow.core.usecase.tecnico.impl

import com.periclao.fixflow.core.exception.TecnicoNaoEncontradoException
import com.periclao.fixflow.core.model.Tecnico
import com.periclao.fixflow.core.repository.TecnicoRepositoryPort
import com.periclao.fixflow.core.usecase.tecnico.InativarTecnicoUseCase
import java.time.LocalDateTime
import java.util.UUID

class InativarTecnicoService(
    private val tecnicoRepository: TecnicoRepositoryPort
) : InativarTecnicoUseCase {

    override fun executar(id: UUID): Tecnico {
        val tecnico = tecnicoRepository.buscarPorId(id)
            ?: throw TecnicoNaoEncontradoException(id)

        if (!tecnico.ativo) return tecnico

        val tecnicoInativado = tecnico.copy(
            ativo = false,
            disponivel = false,
            atualizadoEm = LocalDateTime.now()
        )

        return tecnicoRepository.salvar(tecnicoInativado)
    }
}

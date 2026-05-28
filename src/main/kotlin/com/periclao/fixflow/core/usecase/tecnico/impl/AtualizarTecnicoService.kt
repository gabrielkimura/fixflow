package com.periclao.fixflow.core.usecase.tecnico.impl

import com.periclao.fixflow.core.exception.EmailJaCadastradoException
import com.periclao.fixflow.core.exception.TecnicoInativoException
import com.periclao.fixflow.core.exception.TecnicoNaoEncontradoException
import com.periclao.fixflow.core.model.Tecnico
import com.periclao.fixflow.core.repository.TecnicoRepositoryPort
import com.periclao.fixflow.core.usecase.tecnico.AtualizarTecnicoUseCase
import java.time.LocalDateTime

class AtualizarTecnicoService(
    private val tecnicoRepository: TecnicoRepositoryPort
) : AtualizarTecnicoUseCase {

    override fun executar(command: AtualizarTecnicoUseCase.Command): Tecnico {
        val tecnico = tecnicoRepository.buscarPorId(command.id)
            ?: throw TecnicoNaoEncontradoException(command.id)

        if (!tecnico.ativo) throw TecnicoInativoException(command.id)

        val emailAlterado = tecnico.email != command.email
        if (emailAlterado && tecnicoRepository.existePorEmail(command.email)) {
            throw EmailJaCadastradoException(command.email)
        }

        val tecnicoAtualizado = tecnico.copy(
            nome = command.nome,
            email = command.email,
            telefone = command.telefone,
            especialidades = command.especialidades,
            disponivel = command.disponivel,
            atualizadoEm = LocalDateTime.now()
        )

        return tecnicoRepository.salvar(tecnicoAtualizado)
    }
}

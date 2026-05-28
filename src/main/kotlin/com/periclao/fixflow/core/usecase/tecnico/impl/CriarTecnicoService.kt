package com.periclao.fixflow.core.usecase.tecnico.impl

import com.periclao.fixflow.core.exception.EmailJaCadastradoException
import com.periclao.fixflow.core.model.Tecnico
import com.periclao.fixflow.core.repository.TecnicoRepositoryPort
import com.periclao.fixflow.core.usecase.tecnico.CriarTecnicoUseCase
import java.time.LocalDateTime
import java.util.UUID

class CriarTecnicoService(
    private val tecnicoRepository: TecnicoRepositoryPort
) : CriarTecnicoUseCase {

    override fun executar(command: CriarTecnicoUseCase.Command): Tecnico {
        if (tecnicoRepository.existePorEmail(command.email)) {
            throw EmailJaCadastradoException(command.email)
        }

        val agora = LocalDateTime.now()
        val novoTecnico = Tecnico(
            id = UUID.randomUUID(),
            nome = command.nome,
            email = command.email,
            telefone = command.telefone,
            especialidades = command.especialidades,
            disponivel = true,
            ativo = true,
            criadoEm = agora,
            atualizadoEm = agora
        )

        return tecnicoRepository.salvar(novoTecnico)
    }
}

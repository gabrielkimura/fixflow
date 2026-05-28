package com.periclao.fixflow.core.usecase.tecnico

import com.periclao.fixflow.core.exception.EmailJaCadastradoException
import com.periclao.fixflow.core.exception.TecnicoInativoException
import com.periclao.fixflow.core.exception.TecnicoNaoEncontradoException
import com.periclao.fixflow.core.model.Tecnico
import com.periclao.fixflow.core.model.enums.Categoria
import com.periclao.fixflow.core.repository.TecnicoRepositoryPort
import com.periclao.fixflow.core.usecase.tecnico.impl.AtualizarTecnicoService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.UUID

class AtualizarTecnicoServiceTest {

    private val tecnicoRepository: TecnicoRepositoryPort = mockk()
    private val service = AtualizarTecnicoService(tecnicoRepository)

    private val tecnicoExistente = Tecnico(
        id = UUID.randomUUID(),
        nome = "Pedro",
        email = "pedro@email.com",
        telefone = "11977777777",
        especialidades = setOf(Categoria.HIDRAULICA),
        disponivel = true,
        ativo = true,
        criadoEm = LocalDateTime.now(),
        atualizadoEm = LocalDateTime.now()
    )

    @Test
    fun `deve atualizar tecnico com sucesso`() {
        val command = AtualizarTecnicoUseCase.Command(
            id = tecnicoExistente.id,
            nome = "Pedro Atualizado",
            email = "pedro@email.com",
            telefone = "11966666666",
            especialidades = setOf(Categoria.HIDRAULICA, Categoria.GERAL),
            disponivel = true
        )
        every { tecnicoRepository.buscarPorId(command.id) } returns tecnicoExistente
        every { tecnicoRepository.salvar(any()) } answers { firstArg() }

        val resultado = service.executar(command)

        assertEquals("Pedro Atualizado", resultado.nome)
        assertEquals(setOf(Categoria.HIDRAULICA, Categoria.GERAL), resultado.especialidades)
    }

    @Test
    fun `deve lancar excecao ao atualizar tecnico inativo`() {
        val tecnicoInativo = tecnicoExistente.copy(ativo = false)
        val command = AtualizarTecnicoUseCase.Command(
            id = tecnicoInativo.id, nome = "X", email = "x@email.com",
            telefone = "11900000000", especialidades = setOf(Categoria.GERAL), disponivel = true
        )
        every { tecnicoRepository.buscarPorId(command.id) } returns tecnicoInativo

        assertThrows<TecnicoInativoException> { service.executar(command) }
        verify(exactly = 0) { tecnicoRepository.salvar(any()) }
    }

    @Test
    fun `deve lancar excecao ao trocar email para um ja cadastrado`() {
        val command = AtualizarTecnicoUseCase.Command(
            id = tecnicoExistente.id, nome = "Pedro", email = "outro@email.com",
            telefone = "11977777777", especialidades = setOf(Categoria.HIDRAULICA), disponivel = true
        )
        every { tecnicoRepository.buscarPorId(command.id) } returns tecnicoExistente
        every { tecnicoRepository.existePorEmail("outro@email.com") } returns true

        assertThrows<EmailJaCadastradoException> { service.executar(command) }
    }

    @Test
    fun `deve lancar excecao quando tecnico nao encontrado`() {
        val id = UUID.randomUUID()
        val command = AtualizarTecnicoUseCase.Command(
            id = id, nome = "X", email = "x@x.com", telefone = "11900000000",
            especialidades = setOf(Categoria.GERAL), disponivel = true
        )
        every { tecnicoRepository.buscarPorId(id) } returns null

        assertThrows<TecnicoNaoEncontradoException> { service.executar(command) }
    }
}

package com.periclao.fixflow.core.usecase.tecnico

import com.periclao.fixflow.core.exception.TecnicoNaoEncontradoException
import com.periclao.fixflow.core.model.Tecnico
import com.periclao.fixflow.core.model.enums.Categoria
import com.periclao.fixflow.core.repository.TecnicoRepositoryPort
import com.periclao.fixflow.core.usecase.tecnico.impl.InativarTecnicoService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.UUID

class InativarTecnicoServiceTest {

    private val tecnicoRepository: TecnicoRepositoryPort = mockk()
    private val service = InativarTecnicoService(tecnicoRepository)

    private val tecnicoAtivo = Tecnico(
        id = UUID.randomUUID(),
        nome = "Pedro",
        email = "pedro@email.com",
        telefone = "11988888888",
        especialidades = setOf(Categoria.HIDRAULICA),
        disponivel = true,
        ativo = true,
        criadoEm = LocalDateTime.now(),
        atualizadoEm = LocalDateTime.now()
    )

    @Test
    fun `deve inativar tecnico ativo`() {
        every { tecnicoRepository.buscarPorId(tecnicoAtivo.id) } returns tecnicoAtivo
        every { tecnicoRepository.salvar(any()) } answers { firstArg() }

        val resultado = service.executar(tecnicoAtivo.id)

        assertFalse(resultado.ativo)
        assertFalse(resultado.disponivel)
        verify(exactly = 1) { tecnicoRepository.salvar(any()) }
    }

    @Test
    fun `deve retornar tecnico sem salvar quando ja inativo`() {
        val tecnicoInativo = tecnicoAtivo.copy(ativo = false, disponivel = false)
        every { tecnicoRepository.buscarPorId(tecnicoInativo.id) } returns tecnicoInativo

        val resultado = service.executar(tecnicoInativo.id)

        assertFalse(resultado.ativo)
        verify(exactly = 0) { tecnicoRepository.salvar(any()) }
    }

    @Test
    fun `deve lancar excecao quando tecnico nao encontrado`() {
        val id = UUID.randomUUID()
        every { tecnicoRepository.buscarPorId(id) } returns null

        assertThrows<TecnicoNaoEncontradoException> { service.executar(id) }
    }
}

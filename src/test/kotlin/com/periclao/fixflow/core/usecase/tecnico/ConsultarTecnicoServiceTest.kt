package com.periclao.fixflow.core.usecase.tecnico

import com.periclao.fixflow.core.exception.TecnicoNaoEncontradoException
import com.periclao.fixflow.core.model.Tecnico
import com.periclao.fixflow.core.model.enums.Categoria
import com.periclao.fixflow.core.repository.TecnicoRepositoryPort
import com.periclao.fixflow.core.usecase.tecnico.impl.ConsultarTecnicoService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.UUID

class ConsultarTecnicoServiceTest {

    private val tecnicoRepository: TecnicoRepositoryPort = mockk()
    private val service = ConsultarTecnicoService(tecnicoRepository)

    private val tecnico = Tecnico(
        id = UUID.randomUUID(),
        nome = "Pedro",
        email = "pedro@email.com",
        telefone = "11999999999",
        especialidades = setOf(Categoria.ELETRICA),
        disponivel = true,
        ativo = true,
        criadoEm = LocalDateTime.now(),
        atualizadoEm = LocalDateTime.now()
    )

    @Test
    fun `deve retornar tecnico por id`() {
        every { tecnicoRepository.buscarPorId(tecnico.id) } returns tecnico

        val resultado = service.buscarPorId(tecnico.id)

        assertEquals(tecnico.id, resultado.id)
    }

    @Test
    fun `deve lancar excecao quando tecnico nao encontrado`() {
        val id = UUID.randomUUID()
        every { tecnicoRepository.buscarPorId(id) } returns null

        assertThrows<TecnicoNaoEncontradoException> { service.buscarPorId(id) }
    }

    @Test
    fun `deve listar todos os tecnicos`() {
        val tecnicos = listOf(tecnico, tecnico.copy(id = UUID.randomUUID(), nome = "Maria"))
        every { tecnicoRepository.listar() } returns tecnicos

        val resultado = service.listarTodos()

        assertEquals(2, resultado.size)
    }

    @Test
    fun `deve retornar lista vazia quando nao ha tecnicos`() {
        every { tecnicoRepository.listar() } returns emptyList()

        val resultado = service.listarTodos()

        assertEquals(0, resultado.size)
    }
}

package com.periclao.fixflow.core.usecase.tecnico

import com.periclao.fixflow.core.exception.EmailJaCadastradoException
import com.periclao.fixflow.core.model.Tecnico
import com.periclao.fixflow.core.model.enums.Categoria
import com.periclao.fixflow.core.repository.TecnicoRepositoryPort
import com.periclao.fixflow.core.usecase.tecnico.impl.CriarTecnicoService
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CriarTecnicoServiceTest {

    private val tecnicoRepository: TecnicoRepositoryPort = mockk()
    private val service = CriarTecnicoService(tecnicoRepository)

    private val command = CriarTecnicoUseCase.Command(
        nome = "Pedro Encanador",
        email = "pedro@email.com",
        telefone = "11999999999",
        especialidades = setOf(Categoria.HIDRAULICA)
    )

    @Test
    fun `deve criar tecnico com sucesso`() {
        val tecnicoSlot = slot<Tecnico>()
        every { tecnicoRepository.existePorEmail(command.email) } returns false
        every { tecnicoRepository.salvar(capture(tecnicoSlot)) } answers { tecnicoSlot.captured }

        val resultado = service.executar(command)

        assertEquals(command.nome, resultado.nome)
        assertEquals(command.email, resultado.email)
        assertEquals(command.especialidades, resultado.especialidades)
        assertTrue(resultado.ativo)
        assertTrue(resultado.disponivel)
    }

    @Test
    fun `deve lancar excecao quando email ja cadastrado`() {
        every { tecnicoRepository.existePorEmail(command.email) } returns true

        assertThrows<EmailJaCadastradoException> { service.executar(command) }
        verify(exactly = 0) { tecnicoRepository.salvar(any()) }
    }
}

package com.periclao.fixflow.core.usecase.cliente

import com.periclao.fixflow.core.exception.EmailJaCadastradoException
import com.periclao.fixflow.core.model.Cliente
import com.periclao.fixflow.core.repository.ClienteRepositoryPort
import com.periclao.fixflow.core.usecase.cliente.impl.CriarClienteService
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CriarClienteServiceTest {

    private val clienteRepository: ClienteRepositoryPort = mockk()
    private val service = CriarClienteService(clienteRepository)

    private val command = CriarClienteUseCase.Command(
        nome = "João Silva",
        email = "joao@email.com",
        telefone = "11999999999"
    )

    @Test
    fun `deve criar cliente com sucesso`() {
        val clienteSlot = slot<Cliente>()
        every { clienteRepository.existePorEmail(command.email) } returns false
        every { clienteRepository.salvar(capture(clienteSlot)) } answers { clienteSlot.captured }

        val resultado = service.executar(command)

        assertEquals(command.nome, resultado.nome)
        assertEquals(command.email, resultado.email)
        assertEquals(command.telefone, resultado.telefone)
        assertTrue(resultado.ativo)
        verify(exactly = 1) { clienteRepository.salvar(any()) }
    }

    @Test
    fun `deve lancar excecao quando email ja cadastrado`() {
        every { clienteRepository.existePorEmail(command.email) } returns true

        assertThrows<EmailJaCadastradoException> { service.executar(command) }

        verify(exactly = 0) { clienteRepository.salvar(any()) }
    }
}

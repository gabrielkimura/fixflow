package com.periclao.fixflow.core.usecase.cliente

import com.periclao.fixflow.core.exception.ClienteInativoException
import com.periclao.fixflow.core.exception.ClienteNaoEncontradoException
import com.periclao.fixflow.core.exception.EmailJaCadastradoException
import com.periclao.fixflow.core.model.Cliente
import com.periclao.fixflow.core.repository.ClienteRepositoryPort
import com.periclao.fixflow.core.usecase.cliente.impl.AtualizarClienteService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.UUID

class AtualizarClienteServiceTest {

    private val clienteRepository: ClienteRepositoryPort = mockk()
    private val service = AtualizarClienteService(clienteRepository)

    private val clienteExistente = Cliente(
        id = UUID.randomUUID(),
        nome = "Carlos",
        email = "carlos@email.com",
        telefone = "11977777777",
        ativo = true,
        criadoEm = LocalDateTime.now(),
        atualizadoEm = LocalDateTime.now()
    )

    @Test
    fun `deve atualizar cliente com sucesso`() {
        val command = AtualizarClienteUseCase.Command(
            id = clienteExistente.id,
            nome = "Carlos Novo",
            email = "carlos@email.com",
            telefone = "11966666666"
        )
        every { clienteRepository.buscarPorId(command.id) } returns clienteExistente
        every { clienteRepository.salvar(any()) } answers { firstArg() }

        val resultado = service.executar(command)

        assertEquals("Carlos Novo", resultado.nome)
        assertEquals("11966666666", resultado.telefone)
    }

    @Test
    fun `deve lancar excecao ao atualizar cliente inativo`() {
        val clienteInativo = clienteExistente.copy(ativo = false)
        val command = AtualizarClienteUseCase.Command(
            id = clienteInativo.id, nome = "X", email = "x@email.com", telefone = "11900000000"
        )
        every { clienteRepository.buscarPorId(command.id) } returns clienteInativo

        assertThrows<ClienteInativoException> { service.executar(command) }
        verify(exactly = 0) { clienteRepository.salvar(any()) }
    }

    @Test
    fun `deve lancar excecao ao trocar email para um ja cadastrado`() {
        val command = AtualizarClienteUseCase.Command(
            id = clienteExistente.id, nome = "Carlos", email = "outro@email.com", telefone = "11977777777"
        )
        every { clienteRepository.buscarPorId(command.id) } returns clienteExistente
        every { clienteRepository.existePorEmail("outro@email.com") } returns true

        assertThrows<EmailJaCadastradoException> { service.executar(command) }
    }

    @Test
    fun `deve lancar excecao quando cliente nao encontrado`() {
        val id = UUID.randomUUID()
        val command = AtualizarClienteUseCase.Command(id, "X", "x@x.com", "11900000000")
        every { clienteRepository.buscarPorId(id) } returns null

        assertThrows<ClienteNaoEncontradoException> { service.executar(command) }
    }
}

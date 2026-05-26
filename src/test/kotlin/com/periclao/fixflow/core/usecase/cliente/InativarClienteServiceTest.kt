package com.periclao.fixflow.core.usecase.cliente

import com.periclao.fixflow.core.exception.ClienteNaoEncontradoException
import com.periclao.fixflow.core.model.Cliente
import com.periclao.fixflow.core.repository.ClienteRepositoryPort
import com.periclao.fixflow.core.usecase.cliente.impl.InativarClienteService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.UUID

class InativarClienteServiceTest {

    private val clienteRepository: ClienteRepositoryPort = mockk()
    private val service = InativarClienteService(clienteRepository)

    private val clienteAtivo = Cliente(
        id = UUID.randomUUID(),
        nome = "Maria",
        email = "maria@email.com",
        telefone = "11988888888",
        ativo = true,
        criadoEm = LocalDateTime.now(),
        atualizadoEm = LocalDateTime.now()
    )

    @Test
    fun `deve inativar cliente ativo`() {
        every { clienteRepository.buscarPorId(clienteAtivo.id) } returns clienteAtivo
        every { clienteRepository.salvar(any()) } answers { firstArg() }

        val resultado = service.executar(clienteAtivo.id)

        assertFalse(resultado.ativo)
        verify(exactly = 1) { clienteRepository.salvar(any()) }
    }

    @Test
    fun `deve retornar cliente sem salvar quando ja inativo`() {
        val clienteInativo = clienteAtivo.copy(ativo = false)
        every { clienteRepository.buscarPorId(clienteInativo.id) } returns clienteInativo

        val resultado = service.executar(clienteInativo.id)

        assertFalse(resultado.ativo)
        verify(exactly = 0) { clienteRepository.salvar(any()) }
    }

    @Test
    fun `deve lancar excecao quando cliente nao encontrado`() {
        val id = UUID.randomUUID()
        every { clienteRepository.buscarPorId(id) } returns null

        assertThrows<ClienteNaoEncontradoException> { service.executar(id) }
    }
}

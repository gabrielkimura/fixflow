package com.periclao.fixflow.core.usecase.cliente

import com.periclao.fixflow.core.exception.ClienteNaoEncontradoException
import com.periclao.fixflow.core.model.Cliente
import com.periclao.fixflow.core.repository.ClienteRepositoryPort
import com.periclao.fixflow.core.usecase.cliente.impl.ConsultarClienteService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.UUID

class ConsultarClienteServiceTest {

    private val clienteRepository: ClienteRepositoryPort = mockk()
    private val service = ConsultarClienteService(clienteRepository)

    private val cliente = Cliente(
        id = UUID.randomUUID(),
        nome = "João",
        email = "joao@email.com",
        telefone = "11999999999",
        ativo = true,
        criadoEm = LocalDateTime.now(),
        atualizadoEm = LocalDateTime.now()
    )

    @Test
    fun `deve retornar cliente por id`() {
        every { clienteRepository.buscarPorId(cliente.id) } returns cliente

        val resultado = service.buscarPorId(cliente.id)

        assertEquals(cliente.id, resultado.id)
        assertEquals(cliente.nome, resultado.nome)
    }

    @Test
    fun `deve lancar excecao quando cliente nao encontrado`() {
        val id = UUID.randomUUID()
        every { clienteRepository.buscarPorId(id) } returns null

        assertThrows<ClienteNaoEncontradoException> { service.buscarPorId(id) }
    }

    @Test
    fun `deve listar todos os clientes`() {
        val clientes = listOf(cliente, cliente.copy(id = UUID.randomUUID(), nome = "Maria"))
        every { clienteRepository.listar() } returns clientes

        val resultado = service.listarTodos()

        assertEquals(2, resultado.size)
    }

    @Test
    fun `deve retornar lista vazia quando nao ha clientes`() {
        every { clienteRepository.listar() } returns emptyList()

        val resultado = service.listarTodos()

        assertEquals(0, resultado.size)
    }
}

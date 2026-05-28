package com.periclao.fixflow.core.usecase.endereco

import com.periclao.fixflow.core.exception.EnderecoNaoEncontradoException
import com.periclao.fixflow.core.model.Endereco
import com.periclao.fixflow.core.model.enums.UF
import com.periclao.fixflow.core.repository.EnderecoRepositoryPort
import com.periclao.fixflow.core.usecase.endereco.impl.ConsultarEnderecosService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class ConsultarEnderecosServiceTest {

    private val enderecoRepository: EnderecoRepositoryPort = mockk()
    private val service = ConsultarEnderecosService(enderecoRepository)

    private val clienteId = UUID.randomUUID()

    private val endereco = Endereco(
        id = UUID.randomUUID(),
        clienteId = clienteId,
        logradouro = "Rua das Flores",
        numero = "100",
        complemento = null,
        bairro = "Centro",
        cidade = "São Paulo",
        uf = UF.SP,
        cep = "01001000",
        principal = true
    )

    @Test
    fun `deve retornar endereco por id`() {
        every { enderecoRepository.buscarPorId(endereco.id) } returns endereco

        val resultado = service.buscarPorId(endereco.id)

        assertEquals(endereco.id, resultado.id)
        assertEquals(endereco.logradouro, resultado.logradouro)
    }

    @Test
    fun `deve lancar excecao quando endereco nao encontrado`() {
        val id = UUID.randomUUID()
        every { enderecoRepository.buscarPorId(id) } returns null

        assertThrows<EnderecoNaoEncontradoException> { service.buscarPorId(id) }
    }

    @Test
    fun `deve listar enderecos do cliente`() {
        val enderecos = listOf(endereco, endereco.copy(id = UUID.randomUUID(), principal = false))
        every { enderecoRepository.listarPorCliente(clienteId) } returns enderecos

        val resultado = service.listarPorCliente(clienteId)

        assertEquals(2, resultado.size)
    }

    @Test
    fun `deve retornar lista vazia quando cliente sem enderecos`() {
        every { enderecoRepository.listarPorCliente(clienteId) } returns emptyList()

        val resultado = service.listarPorCliente(clienteId)

        assertEquals(0, resultado.size)
    }
}

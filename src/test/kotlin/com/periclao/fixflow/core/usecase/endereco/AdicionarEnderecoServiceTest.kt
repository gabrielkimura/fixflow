package com.periclao.fixflow.core.usecase.endereco

import com.periclao.fixflow.core.exception.ClienteInativoException
import com.periclao.fixflow.core.exception.ClienteNaoEncontradoException
import com.periclao.fixflow.core.model.Cliente
import com.periclao.fixflow.core.model.Endereco
import com.periclao.fixflow.core.model.UF
import com.periclao.fixflow.core.repository.ClienteRepositoryPort
import com.periclao.fixflow.core.repository.EnderecoRepositoryPort
import com.periclao.fixflow.core.usecase.endereco.impl.AdicionarEnderecoService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.UUID

class AdicionarEnderecoServiceTest {

    private val clienteRepository: ClienteRepositoryPort = mockk()
    private val enderecoRepository: EnderecoRepositoryPort = mockk()
    private val service = AdicionarEnderecoService(clienteRepository, enderecoRepository)

    private val clienteAtivo = Cliente(
        id = UUID.randomUUID(),
        nome = "Ana",
        email = "ana@email.com",
        telefone = "11955555555",
        ativo = true,
        criadoEm = LocalDateTime.now(),
        atualizadoEm = LocalDateTime.now()
    )

    private val command = AdicionarEnderecoUseCase.Command(
        clienteId = clienteAtivo.id,
        logradouro = "Rua das Flores",
        numero = "100",
        complemento = null,
        bairro = "Centro",
        cidade = "São Paulo",
        uf = UF.SP,
        cep = "01001000",
        principal = false
    )

    @Test
    fun `deve adicionar endereco com sucesso`() {
        every { clienteRepository.buscarPorId(clienteAtivo.id) } returns clienteAtivo
        every { enderecoRepository.salvar(any()) } answers { firstArg() }

        val resultado = service.executar(command)

        assertEquals(command.logradouro, resultado.logradouro)
        assertEquals(command.clienteId, resultado.clienteId)
    }

    @Test
    fun `deve desmarcar principal anterior ao adicionar novo principal`() {
        val enderecoAnterior = Endereco(
            id = UUID.randomUUID(), clienteId = clienteAtivo.id,
            logradouro = "Rua Antiga", numero = "1", complemento = null,
            bairro = "Vila", cidade = "SP", uf = UF.SP, cep = "00000000", principal = true
        )
        every { clienteRepository.buscarPorId(clienteAtivo.id) } returns clienteAtivo
        every { enderecoRepository.listarPorCliente(clienteAtivo.id) } returns listOf(enderecoAnterior)
        every { enderecoRepository.salvar(any()) } answers { firstArg() }

        val resultado = service.executar(command.copy(principal = true))

        assertTrue(resultado.principal)
        verify(exactly = 1) { enderecoRepository.salvar(enderecoAnterior.copy(principal = false)) }
    }

    @Test
    fun `deve lancar excecao quando cliente nao encontrado`() {
        every { clienteRepository.buscarPorId(command.clienteId) } returns null

        assertThrows<ClienteNaoEncontradoException> { service.executar(command) }
    }

    @Test
    fun `deve lancar excecao quando cliente inativo`() {
        val clienteInativo = clienteAtivo.copy(ativo = false)
        every { clienteRepository.buscarPorId(clienteInativo.id) } returns clienteInativo

        assertThrows<ClienteInativoException> { service.executar(command.copy(clienteId = clienteInativo.id)) }
    }
}

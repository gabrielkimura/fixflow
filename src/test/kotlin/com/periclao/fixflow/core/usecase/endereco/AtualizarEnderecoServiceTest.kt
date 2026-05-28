package com.periclao.fixflow.core.usecase.endereco

import com.periclao.fixflow.core.exception.ClienteInativoException
import com.periclao.fixflow.core.exception.ClienteNaoEncontradoException
import com.periclao.fixflow.core.exception.EnderecoNaoEncontradoException
import com.periclao.fixflow.core.model.Cliente
import com.periclao.fixflow.core.model.Endereco
import com.periclao.fixflow.core.model.enums.UF
import com.periclao.fixflow.core.repository.ClienteRepositoryPort
import com.periclao.fixflow.core.repository.EnderecoRepositoryPort
import com.periclao.fixflow.core.usecase.endereco.impl.AtualizarEnderecoService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.UUID

class AtualizarEnderecoServiceTest {

    private val clienteRepository: ClienteRepositoryPort = mockk()
    private val enderecoRepository: EnderecoRepositoryPort = mockk()
    private val service = AtualizarEnderecoService(clienteRepository, enderecoRepository)

    private val clienteAtivo = Cliente(
        id = UUID.randomUUID(),
        nome = "Ana",
        email = "ana@email.com",
        telefone = "11955555555",
        ativo = true,
        criadoEm = LocalDateTime.now(),
        atualizadoEm = LocalDateTime.now()
    )

    private val enderecoExistente = Endereco(
        id = UUID.randomUUID(),
        clienteId = clienteAtivo.id,
        logradouro = "Rua Antiga",
        numero = "50",
        complemento = null,
        bairro = "Centro",
        cidade = "São Paulo",
        uf = UF.SP,
        cep = "01001000",
        principal = false
    )

    private val command = AtualizarEnderecoUseCase.Command(
        id = enderecoExistente.id,
        logradouro = "Rua Nova",
        numero = "200",
        complemento = "Apto 10",
        bairro = "Jardins",
        cidade = "São Paulo",
        uf = UF.SP,
        cep = "01402000",
        principal = false
    )

    @Test
    fun `deve atualizar endereco com sucesso`() {
        every { enderecoRepository.buscarPorId(command.id) } returns enderecoExistente
        every { clienteRepository.buscarPorId(clienteAtivo.id) } returns clienteAtivo
        every { enderecoRepository.salvar(any()) } answers { firstArg() }

        val resultado = service.executar(command)

        assertEquals("Rua Nova", resultado.logradouro)
        assertEquals("200", resultado.numero)
        assertEquals("Apto 10", resultado.complemento)
    }

    @Test
    fun `deve desmarcar principal anterior ao promover endereco`() {
        val enderecoPrincipal = enderecoExistente.copy(
            id = UUID.randomUUID(),
            principal = true
        )
        every { enderecoRepository.buscarPorId(command.id) } returns enderecoExistente
        every { clienteRepository.buscarPorId(clienteAtivo.id) } returns clienteAtivo
        every { enderecoRepository.listarPorCliente(clienteAtivo.id) } returns listOf(enderecoPrincipal, enderecoExistente)
        every { enderecoRepository.salvar(any()) } answers { firstArg() }

        val resultado = service.executar(command.copy(principal = true))

        assertTrue(resultado.principal)
        verify { enderecoRepository.salvar(enderecoPrincipal.copy(principal = false)) }
    }

    @Test
    fun `deve lancar excecao quando endereco nao encontrado`() {
        every { enderecoRepository.buscarPorId(command.id) } returns null

        assertThrows<EnderecoNaoEncontradoException> { service.executar(command) }
    }

    @Test
    fun `deve lancar excecao quando cliente nao encontrado`() {
        every { enderecoRepository.buscarPorId(command.id) } returns enderecoExistente
        every { clienteRepository.buscarPorId(clienteAtivo.id) } returns null

        assertThrows<ClienteNaoEncontradoException> { service.executar(command) }
    }

    @Test
    fun `deve lancar excecao quando cliente inativo`() {
        val clienteInativo = clienteAtivo.copy(ativo = false)
        val endereco = enderecoExistente.copy(clienteId = clienteInativo.id)
        val cmd = command.copy(id = endereco.id)

        every { enderecoRepository.buscarPorId(cmd.id) } returns endereco
        every { clienteRepository.buscarPorId(clienteInativo.id) } returns clienteInativo

        assertThrows<ClienteInativoException> { service.executar(cmd) }
    }
}

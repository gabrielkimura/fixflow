package com.periclao.fixflow.core.usecase.chamado

import com.periclao.fixflow.core.exception.ClienteInativoException
import com.periclao.fixflow.core.exception.ClienteNaoEncontradoException
import com.periclao.fixflow.core.exception.EnderecoNaoEncontradoException
import com.periclao.fixflow.core.model.Chamado
import com.periclao.fixflow.core.model.Cliente
import com.periclao.fixflow.core.model.Endereco
import com.periclao.fixflow.core.model.enums.Categoria
import com.periclao.fixflow.core.model.enums.StatusChamado
import com.periclao.fixflow.core.model.enums.UF
import com.periclao.fixflow.core.repository.ChamadoRepositoryPort
import com.periclao.fixflow.core.repository.ClienteRepositoryPort
import com.periclao.fixflow.core.repository.EnderecoRepositoryPort
import com.periclao.fixflow.core.usecase.chamado.impl.AbrirChamadoService
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.UUID

class AbrirChamadoServiceTest {

    private val chamadoRepository: ChamadoRepositoryPort = mockk()
    private val clienteRepository: ClienteRepositoryPort = mockk()
    private val enderecoRepository: EnderecoRepositoryPort = mockk()
    private val service = AbrirChamadoService(chamadoRepository, clienteRepository, enderecoRepository)

    private val clienteId = UUID.randomUUID()
    private val enderecoId = UUID.randomUUID()

    private val command = AbrirChamadoUseCase.Command(
        clienteId = clienteId,
        enderecoId = enderecoId,
        descricao = "Tem um vazamento no cano"
    )

    private fun cliente(ativo: Boolean = true) = Cliente(
        id = clienteId,
        nome = "Maria",
        email = "maria@email.com",
        telefone = "11999999999",
        ativo = ativo,
        criadoEm = LocalDateTime.now(),
        atualizadoEm = LocalDateTime.now()
    )

    private fun endereco(clienteId: UUID = this.clienteId) = Endereco(
        id = enderecoId,
        clienteId = clienteId,
        logradouro = "Rua A",
        numero = "10",
        complemento = null,
        bairro = "Centro",
        cidade = "São Paulo",
        uf = UF.SP,
        cep = "01000000",
        principal = true
    )

    @Test
    fun `deve abrir chamado categorizado com status aberto`() {
        val chamadoSlot = slot<Chamado>()
        every { clienteRepository.buscarPorId(clienteId) } returns cliente()
        every { enderecoRepository.buscarPorId(enderecoId) } returns endereco()
        every { chamadoRepository.salvar(capture(chamadoSlot)) } answers { chamadoSlot.captured }

        val resultado = service.executar(command)

        assertEquals(StatusChamado.ABERTO, resultado.status)
        assertEquals(Categoria.HIDRAULICA, resultado.categoria)
        assertNull(resultado.tecnicoId)
        assertNull(resultado.descricaoEncerramento)
    }

    @Test
    fun `deve abrir chamado pendente de categorizacao quando sem palavra-chave`() {
        val chamadoSlot = slot<Chamado>()
        every { clienteRepository.buscarPorId(clienteId) } returns cliente()
        every { enderecoRepository.buscarPorId(enderecoId) } returns endereco()
        every { chamadoRepository.salvar(capture(chamadoSlot)) } answers { chamadoSlot.captured }

        val resultado = service.executar(command.copy(descricao = "Servico generico"))

        assertNull(resultado.categoria)
    }

    @Test
    fun `deve lancar excecao quando cliente nao encontrado`() {
        every { clienteRepository.buscarPorId(clienteId) } returns null

        assertThrows<ClienteNaoEncontradoException> { service.executar(command) }
        verify(exactly = 0) { chamadoRepository.salvar(any()) }
    }

    @Test
    fun `deve lancar excecao quando cliente inativo`() {
        every { clienteRepository.buscarPorId(clienteId) } returns cliente(ativo = false)

        assertThrows<ClienteInativoException> { service.executar(command) }
        verify(exactly = 0) { chamadoRepository.salvar(any()) }
    }

    @Test
    fun `deve lancar excecao quando endereco nao encontrado`() {
        every { clienteRepository.buscarPorId(clienteId) } returns cliente()
        every { enderecoRepository.buscarPorId(enderecoId) } returns null

        assertThrows<EnderecoNaoEncontradoException> { service.executar(command) }
        verify(exactly = 0) { chamadoRepository.salvar(any()) }
    }

    @Test
    fun `deve lancar excecao quando endereco pertence a outro cliente`() {
        every { clienteRepository.buscarPorId(clienteId) } returns cliente()
        every { enderecoRepository.buscarPorId(enderecoId) } returns endereco(clienteId = UUID.randomUUID())

        assertThrows<EnderecoNaoEncontradoException> { service.executar(command) }
        verify(exactly = 0) { chamadoRepository.salvar(any()) }
    }
}

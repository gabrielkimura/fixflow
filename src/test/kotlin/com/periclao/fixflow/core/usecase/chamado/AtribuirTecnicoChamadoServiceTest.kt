package com.periclao.fixflow.core.usecase.chamado

import com.periclao.fixflow.core.exception.ChamadoNaoEncontradoException
import com.periclao.fixflow.core.exception.TecnicoInativoException
import com.periclao.fixflow.core.exception.TecnicoNaoEncontradoException
import com.periclao.fixflow.core.exception.TransicaoStatusInvalidaException
import com.periclao.fixflow.core.model.Chamado
import com.periclao.fixflow.core.model.Tecnico
import com.periclao.fixflow.core.model.enums.Categoria
import com.periclao.fixflow.core.model.enums.StatusChamado
import com.periclao.fixflow.core.repository.ChamadoRepositoryPort
import com.periclao.fixflow.core.repository.TecnicoRepositoryPort
import com.periclao.fixflow.core.usecase.chamado.impl.AtribuirTecnicoChamadoService
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.UUID

class AtribuirTecnicoChamadoServiceTest {

    private val chamadoRepository: ChamadoRepositoryPort = mockk()
    private val tecnicoRepository: TecnicoRepositoryPort = mockk()
    private val service = AtribuirTecnicoChamadoService(chamadoRepository, tecnicoRepository)

    private val chamadoId = UUID.randomUUID()
    private val tecnicoId = UUID.randomUUID()
    private val command = AtribuirTecnicoChamadoUseCase.Command(chamadoId, tecnicoId)

    private fun chamado(status: StatusChamado) = Chamado(
        id = chamadoId,
        clienteId = UUID.randomUUID(),
        enderecoId = UUID.randomUUID(),
        descricao = "Tem um vazamento no cano",
        categoria = Categoria.HIDRAULICA,
        status = status,
        tecnicoId = null,
        descricaoEncerramento = null,
        criadoEm = LocalDateTime.now(),
        atualizadoEm = LocalDateTime.now()
    )

    private fun tecnico(ativo: Boolean = true) = Tecnico(
        id = tecnicoId,
        nome = "Pedro",
        email = "pedro@email.com",
        telefone = "11999999999",
        especialidades = setOf(Categoria.HIDRAULICA),
        disponivel = true,
        ativo = ativo,
        criadoEm = LocalDateTime.now(),
        atualizadoEm = LocalDateTime.now()
    )

    @Test
    fun `deve atribuir tecnico e mover para tecnico atribuido`() {
        val chamadoSlot = slot<Chamado>()
        every { chamadoRepository.buscarPorId(chamadoId) } returns chamado(StatusChamado.EM_ANALISE)
        every { tecnicoRepository.buscarPorId(tecnicoId) } returns tecnico()
        every { chamadoRepository.salvar(capture(chamadoSlot)) } answers { chamadoSlot.captured }

        val resultado = service.executar(command)

        assertEquals(StatusChamado.TECNICO_ATRIBUIDO, resultado.status)
        assertEquals(tecnicoId, resultado.tecnicoId)
    }

    @Test
    fun `deve lancar excecao quando chamado nao encontrado`() {
        every { chamadoRepository.buscarPorId(chamadoId) } returns null

        assertThrows<ChamadoNaoEncontradoException> { service.executar(command) }
        verify(exactly = 0) { chamadoRepository.salvar(any()) }
    }

    @Test
    fun `deve lancar excecao quando status nao permite atribuicao`() {
        every { chamadoRepository.buscarPorId(chamadoId) } returns chamado(StatusChamado.ABERTO)

        assertThrows<TransicaoStatusInvalidaException> { service.executar(command) }
        verify(exactly = 0) { chamadoRepository.salvar(any()) }
    }

    @Test
    fun `deve lancar excecao quando tecnico nao encontrado`() {
        every { chamadoRepository.buscarPorId(chamadoId) } returns chamado(StatusChamado.EM_ANALISE)
        every { tecnicoRepository.buscarPorId(tecnicoId) } returns null

        assertThrows<TecnicoNaoEncontradoException> { service.executar(command) }
        verify(exactly = 0) { chamadoRepository.salvar(any()) }
    }

    @Test
    fun `deve lancar excecao quando tecnico inativo`() {
        every { chamadoRepository.buscarPorId(chamadoId) } returns chamado(StatusChamado.EM_ANALISE)
        every { tecnicoRepository.buscarPorId(tecnicoId) } returns tecnico(ativo = false)

        assertThrows<TecnicoInativoException> { service.executar(command) }
        verify(exactly = 0) { chamadoRepository.salvar(any()) }
    }
}

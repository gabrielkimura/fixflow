package com.periclao.fixflow.core.usecase.chamado

import com.periclao.fixflow.core.exception.ChamadoNaoEncontradoException
import com.periclao.fixflow.core.exception.TransicaoRequerOperacaoDedicadaException
import com.periclao.fixflow.core.exception.TransicaoStatusInvalidaException
import com.periclao.fixflow.core.model.Chamado
import com.periclao.fixflow.core.model.enums.Categoria
import com.periclao.fixflow.core.model.enums.StatusChamado
import com.periclao.fixflow.core.repository.ChamadoRepositoryPort
import com.periclao.fixflow.core.usecase.chamado.impl.MudarStatusChamadoService
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.UUID

class MudarStatusChamadoServiceTest {

    private val chamadoRepository: ChamadoRepositoryPort = mockk()
    private val service = MudarStatusChamadoService(chamadoRepository)

    private val chamadoId = UUID.randomUUID()

    private fun command(novoStatus: StatusChamado) =
        MudarStatusChamadoUseCase.Command(chamadoId, novoStatus)

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

    @Test
    fun `deve mover de aberto para em analise`() {
        val chamadoSlot = slot<Chamado>()
        every { chamadoRepository.buscarPorId(chamadoId) } returns chamado(StatusChamado.ABERTO)
        every { chamadoRepository.salvar(capture(chamadoSlot)) } answers { chamadoSlot.captured }

        val resultado = service.executar(command(StatusChamado.EM_ANALISE))

        assertEquals(StatusChamado.EM_ANALISE, resultado.status)
    }

    @Test
    fun `deve mover de tecnico atribuido para em andamento`() {
        val chamadoSlot = slot<Chamado>()
        every { chamadoRepository.buscarPorId(chamadoId) } returns chamado(StatusChamado.TECNICO_ATRIBUIDO)
        every { chamadoRepository.salvar(capture(chamadoSlot)) } answers { chamadoSlot.captured }

        val resultado = service.executar(command(StatusChamado.EM_ANDAMENTO))

        assertEquals(StatusChamado.EM_ANDAMENTO, resultado.status)
    }

    @Test
    fun `deve lancar excecao ao tentar status que exige operacao dedicada`() {
        assertThrows<TransicaoRequerOperacaoDedicadaException> {
            service.executar(command(StatusChamado.CONCLUIDO))
        }
        verify(exactly = 0) { chamadoRepository.buscarPorId(any()) }
    }

    @Test
    fun `deve lancar excecao quando transicao nao permitida`() {
        every { chamadoRepository.buscarPorId(chamadoId) } returns chamado(StatusChamado.ABERTO)

        assertThrows<TransicaoStatusInvalidaException> {
            service.executar(command(StatusChamado.EM_ANDAMENTO))
        }
        verify(exactly = 0) { chamadoRepository.salvar(any()) }
    }

    @Test
    fun `deve lancar excecao quando chamado nao encontrado`() {
        every { chamadoRepository.buscarPorId(chamadoId) } returns null

        assertThrows<ChamadoNaoEncontradoException> {
            service.executar(command(StatusChamado.EM_ANALISE))
        }
    }
}

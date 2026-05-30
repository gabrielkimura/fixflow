package com.periclao.fixflow.core.usecase.chamado

import com.periclao.fixflow.core.exception.ChamadoNaoEncontradoException
import com.periclao.fixflow.core.exception.TransicaoStatusInvalidaException
import com.periclao.fixflow.core.model.Chamado
import com.periclao.fixflow.core.model.enums.Categoria
import com.periclao.fixflow.core.model.enums.StatusChamado
import com.periclao.fixflow.core.repository.ChamadoRepositoryPort
import com.periclao.fixflow.core.usecase.chamado.impl.CancelarChamadoService
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.time.LocalDateTime
import java.util.UUID

class CancelarChamadoServiceTest {

    private val chamadoRepository: ChamadoRepositoryPort = mockk()
    private val service = CancelarChamadoService(chamadoRepository)

    private val chamadoId = UUID.randomUUID()

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

    @ParameterizedTest
    @EnumSource(names = ["ABERTO", "EM_ANALISE", "TECNICO_ATRIBUIDO"])
    fun `deve cancelar chamado nos estados cancelaveis`(status: StatusChamado) {
        val chamadoSlot = slot<Chamado>()
        every { chamadoRepository.buscarPorId(chamadoId) } returns chamado(status)
        every { chamadoRepository.salvar(capture(chamadoSlot)) } answers { chamadoSlot.captured }

        val resultado = service.executar(chamadoId)

        assertEquals(StatusChamado.CANCELADO, resultado.status)
    }

    @ParameterizedTest
    @EnumSource(names = ["EM_ANDAMENTO", "CONCLUIDO", "CANCELADO"])
    fun `nao deve cancelar chamado em andamento ou finalizado`(status: StatusChamado) {
        every { chamadoRepository.buscarPorId(chamadoId) } returns chamado(status)

        assertThrows<TransicaoStatusInvalidaException> { service.executar(chamadoId) }
        verify(exactly = 0) { chamadoRepository.salvar(any()) }
    }

    @Test
    fun `deve lancar excecao quando chamado nao encontrado`() {
        every { chamadoRepository.buscarPorId(chamadoId) } returns null

        assertThrows<ChamadoNaoEncontradoException> { service.executar(chamadoId) }
    }
}

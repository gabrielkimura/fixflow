package com.periclao.fixflow.core.usecase.chamado

import com.periclao.fixflow.core.exception.ChamadoNaoEncontradoException
import com.periclao.fixflow.core.exception.DescricaoEncerramentoObrigatoriaException
import com.periclao.fixflow.core.exception.TransicaoStatusInvalidaException
import com.periclao.fixflow.core.model.Chamado
import com.periclao.fixflow.core.model.enums.Categoria
import com.periclao.fixflow.core.model.enums.StatusChamado
import com.periclao.fixflow.core.repository.ChamadoRepositoryPort
import com.periclao.fixflow.core.usecase.chamado.impl.ConcluirChamadoService
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.UUID

class ConcluirChamadoServiceTest {

    private val chamadoRepository: ChamadoRepositoryPort = mockk()
    private val service = ConcluirChamadoService(chamadoRepository)

    private val chamadoId = UUID.randomUUID()
    private val command = ConcluirChamadoUseCase.Command(
        chamadoId = chamadoId,
        descricaoEncerramento = "Cano substituído e vazamento resolvido"
    )

    private fun chamado(status: StatusChamado) = Chamado(
        id = chamadoId,
        clienteId = UUID.randomUUID(),
        enderecoId = UUID.randomUUID(),
        descricao = "Tem um vazamento no cano",
        categoria = Categoria.HIDRAULICA,
        status = status,
        tecnicoId = UUID.randomUUID(),
        descricaoEncerramento = null,
        criadoEm = LocalDateTime.now(),
        atualizadoEm = LocalDateTime.now()
    )

    @Test
    fun `deve concluir chamado em andamento com descricao de encerramento`() {
        val chamadoSlot = slot<Chamado>()
        every { chamadoRepository.buscarPorId(chamadoId) } returns chamado(StatusChamado.EM_ANDAMENTO)
        every { chamadoRepository.salvar(capture(chamadoSlot)) } answers { chamadoSlot.captured }

        val resultado = service.executar(command)

        assertEquals(StatusChamado.CONCLUIDO, resultado.status)
        assertEquals(command.descricaoEncerramento, resultado.descricaoEncerramento)
    }

    @Test
    fun `deve lancar excecao quando descricao de encerramento em branco`() {
        assertThrows<DescricaoEncerramentoObrigatoriaException> {
            service.executar(command.copy(descricaoEncerramento = "   "))
        }
        verify(exactly = 0) { chamadoRepository.salvar(any()) }
    }

    @Test
    fun `deve lancar excecao quando status nao permite conclusao`() {
        every { chamadoRepository.buscarPorId(chamadoId) } returns chamado(StatusChamado.ABERTO)

        assertThrows<TransicaoStatusInvalidaException> { service.executar(command) }
        verify(exactly = 0) { chamadoRepository.salvar(any()) }
    }

    @Test
    fun `deve lancar excecao quando chamado nao encontrado`() {
        every { chamadoRepository.buscarPorId(chamadoId) } returns null

        assertThrows<ChamadoNaoEncontradoException> { service.executar(command) }
    }
}

package com.periclao.fixflow.core.usecase.endereco

import com.periclao.fixflow.core.exception.EnderecoNaoEncontradoException
import com.periclao.fixflow.core.model.Endereco
import com.periclao.fixflow.core.model.enums.UF
import com.periclao.fixflow.core.repository.EnderecoRepositoryPort
import com.periclao.fixflow.core.usecase.endereco.impl.RemoverEnderecoService
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class RemoverEnderecoServiceTest {

    private val enderecoRepository: EnderecoRepositoryPort = mockk()
    private val service = RemoverEnderecoService(enderecoRepository)

    private val endereco = Endereco(
        id = UUID.randomUUID(),
        clienteId = UUID.randomUUID(),
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
    fun `deve remover endereco existente`() {
        every { enderecoRepository.buscarPorId(endereco.id) } returns endereco
        justRun { enderecoRepository.deletar(endereco.id) }

        service.executar(endereco.id)

        verify(exactly = 1) { enderecoRepository.deletar(endereco.id) }
    }

    @Test
    fun `deve lancar excecao quando endereco nao encontrado`() {
        val id = UUID.randomUUID()
        every { enderecoRepository.buscarPorId(id) } returns null

        assertThrows<EnderecoNaoEncontradoException> { service.executar(id) }
        verify(exactly = 0) { enderecoRepository.deletar(any()) }
    }
}

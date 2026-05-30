package com.periclao.fixflow.core.usecase.chamado

import com.periclao.fixflow.core.model.enums.Categoria
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class CategorizadorChamadoTest {

    @Test
    fun `deve categorizar como hidraulica por palavra-chave`() {
        assertEquals(Categoria.HIDRAULICA, CategorizadorChamado.categorizar("Tem um vazamento no cano da pia"))
    }

    @Test
    fun `deve categorizar como eletrica por palavra-chave`() {
        assertEquals(Categoria.ELETRICA, CategorizadorChamado.categorizar("A tomada deu um curto"))
    }

    @Test
    fun `deve ignorar acentos e caixa ao categorizar`() {
        assertEquals(Categoria.HIDRAULICA, CategorizadorChamado.categorizar("INFILTRAÇÃO na parede"))
    }

    @Test
    fun `deve retornar null quando nenhuma palavra-chave for encontrada`() {
        assertNull(CategorizadorChamado.categorizar("Preciso de um orcamento geral"))
    }

    @Test
    fun `nao deve gerar falso positivo por substring`() {
        assertNull(CategorizadorChamado.categorizar("Aceitei o desafio"))
    }
}

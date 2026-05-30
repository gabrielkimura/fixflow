package com.periclao.fixflow.core.usecase.chamado

import com.periclao.fixflow.core.model.enums.Categoria
import java.text.Normalizer


object CategorizadorChamado {

    private val palavrasChavePorCategoria: Map<Categoria, List<String>> = mapOf(
        Categoria.HIDRAULICA to listOf("vazamento", "cano", "infiltracao", "encanamento", "torneira", "esgoto", "ralo"),
        Categoria.ELETRICA to listOf("curto", "tomada", "fio", "disjuntor", "fiacao", "choque", "lampada"),
        Categoria.PINTURA to listOf("pintura", "tinta", "verniz", "pintar"),
        Categoria.ALVENARIA to listOf("tijolo", "reboco", "rachadura", "muro", "alvenaria"),
        Categoria.CLIMATIZACAO to listOf("condicionado", "climatizacao", "ventilacao", "refrigeracao", "geladeira")
    )

    fun categorizar(descricao: String): Categoria? {
        val palavras = tokenizar(descricao)
        return palavrasChavePorCategoria.entries
            .firstOrNull { (_, chaves) -> chaves.any { it in palavras } }
            ?.key
    }

    private fun tokenizar(texto: String): Set<String> =
        normalizar(texto).split(Regex("[^a-z0-9]+")).toSet()

    private fun normalizar(texto: String): String =
        Normalizer.normalize(texto.lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
}

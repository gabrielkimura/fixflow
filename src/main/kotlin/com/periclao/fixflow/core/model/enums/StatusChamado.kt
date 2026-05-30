package com.periclao.fixflow.core.model.enums

enum class StatusChamado {
    ABERTO,
    EM_ANALISE,
    TECNICO_ATRIBUIDO,
    EM_ANDAMENTO,
    CONCLUIDO,
    CANCELADO;

    fun podeTransicionarPara(novo: StatusChamado): Boolean =
        novo in transicoesPermitidas()

    fun ehFinal(): Boolean = transicoesPermitidas().isEmpty()

    private fun transicoesPermitidas(): Set<StatusChamado> = when (this) {
        ABERTO -> setOf(EM_ANALISE, CANCELADO)
        EM_ANALISE -> setOf(TECNICO_ATRIBUIDO, CANCELADO)
        TECNICO_ATRIBUIDO -> setOf(EM_ANDAMENTO, CANCELADO)
        EM_ANDAMENTO -> setOf(CONCLUIDO)
        CONCLUIDO, CANCELADO -> emptySet()
    }
}

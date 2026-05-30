package com.periclao.fixflow.core.exception

import com.periclao.fixflow.core.model.enums.StatusChamado

class TransicaoStatusInvalidaException(de: StatusChamado, para: StatusChamado) :
    RuntimeException("Transição de status inválida: $de -> $para")

package com.periclao.fixflow.core.exception

import java.util.UUID

class TecnicoNaoEncontradoException(id: UUID) :
    RuntimeException("Técnico não encontrado: $id")

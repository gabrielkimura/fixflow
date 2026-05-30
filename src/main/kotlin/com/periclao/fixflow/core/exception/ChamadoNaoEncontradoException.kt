package com.periclao.fixflow.core.exception

import java.util.UUID

class ChamadoNaoEncontradoException(id: UUID) :
    RuntimeException("Chamado não encontrado: $id")

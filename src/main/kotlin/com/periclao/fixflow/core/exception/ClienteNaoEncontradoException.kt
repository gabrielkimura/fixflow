package com.periclao.fixflow.core.exception

import java.util.UUID

class ClienteNaoEncontradoException(id: UUID) :
    RuntimeException("Cliente não encontrado: $id")

package com.periclao.fixflow.core.exception

import java.util.UUID

class EnderecoNaoEncontradoException(id: UUID) :
    RuntimeException("Endereço não encontrado: $id")

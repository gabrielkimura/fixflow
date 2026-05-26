package com.periclao.fixflow.core.exception

import java.util.UUID

class ClienteInativoException(id: UUID) :
    RuntimeException("Cliente inativo e não pode realizar operações: $id")

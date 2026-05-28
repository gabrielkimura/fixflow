package com.periclao.fixflow.core.exception

import java.util.UUID

class TecnicoInativoException(id: UUID) :
    RuntimeException("Técnico inativo e não pode realizar operações: $id")

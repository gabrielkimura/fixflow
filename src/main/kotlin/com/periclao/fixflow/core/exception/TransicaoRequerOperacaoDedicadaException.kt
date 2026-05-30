package com.periclao.fixflow.core.exception

import com.periclao.fixflow.core.model.enums.StatusChamado

class TransicaoRequerOperacaoDedicadaException(status: StatusChamado) :
    RuntimeException("Mudança para o status $status requer operação dedicada (atribuir técnico, concluir ou cancelar)")

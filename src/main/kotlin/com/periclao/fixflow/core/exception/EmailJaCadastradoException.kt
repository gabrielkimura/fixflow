package com.periclao.fixflow.core.exception

class EmailJaCadastradoException(email: String) :
    RuntimeException("E-mail já cadastrado: $email")

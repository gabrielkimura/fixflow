package com.periclao.fixflow.api.exception

import com.periclao.fixflow.core.exception.ClienteInativoException
import com.periclao.fixflow.core.exception.ClienteNaoEncontradoException
import com.periclao.fixflow.core.exception.EmailJaCadastradoException
import com.periclao.fixflow.core.exception.EnderecoNaoEncontradoException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ClienteNaoEncontradoException::class, EnderecoNaoEncontradoException::class)
    fun handleNaoEncontrado(ex: RuntimeException): ResponseEntity<ErroResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErroResponse(status = 404, mensagem = ex.message ?: "Recurso não encontrado"))

    @ExceptionHandler(ClienteInativoException::class)
    fun handleClienteInativo(ex: ClienteInativoException): ResponseEntity<ErroResponse> =
        ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(ErroResponse(status = 422, mensagem = ex.message ?: "Cliente inativo"))

    @ExceptionHandler(EmailJaCadastradoException::class)
    fun handleEmailDuplicado(ex: EmailJaCadastradoException): ResponseEntity<ErroResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErroResponse(status = 409, mensagem = ex.message ?: "E-mail já cadastrado"))

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidacao(ex: MethodArgumentNotValidException): ResponseEntity<ErroResponse> {
        val erros = ex.bindingResult.fieldErrors
            .map { "${it.field}: ${it.defaultMessage}" }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErroResponse(status = 400, mensagem = "Dados inválidos na requisição", erros = erros))
    }

    @ExceptionHandler(Exception::class)
    fun handleGenerico(ex: Exception): ResponseEntity<ErroResponse> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErroResponse(status = 500, mensagem = "Erro interno no servidor"))
}

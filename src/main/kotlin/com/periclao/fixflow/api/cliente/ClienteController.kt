package com.periclao.fixflow.api.cliente

import com.periclao.fixflow.api.cliente.request.AtualizarClienteRequest
import com.periclao.fixflow.api.cliente.request.CriarClienteRequest
import com.periclao.fixflow.api.cliente.response.ClienteResponse
import com.periclao.fixflow.core.usecase.cliente.AtualizarClienteUseCase
import com.periclao.fixflow.core.usecase.cliente.ConsultarClienteUseCase
import com.periclao.fixflow.core.usecase.cliente.CriarClienteUseCase
import com.periclao.fixflow.core.usecase.cliente.InativarClienteUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/clientes")
class ClienteController(
    private val criarCliente: CriarClienteUseCase,
    private val atualizarCliente: AtualizarClienteUseCase,
    private val consultarCliente: ConsultarClienteUseCase,
    private val inativarCliente: InativarClienteUseCase
) {

    @PostMapping
    fun criar(@Valid @RequestBody request: CriarClienteRequest): ResponseEntity<ClienteResponse> {
        val cliente = criarCliente.executar(request.toCommand())
        return ResponseEntity.status(HttpStatus.CREATED).body(ClienteResponse.from(cliente))
    }

    @GetMapping
    fun listar(): ResponseEntity<List<ClienteResponse>> {
        val clientes = consultarCliente.listarTodos().map { ClienteResponse.from(it) }
        return ResponseEntity.ok(clientes)
    }

    @GetMapping("/{id}")
    fun buscar(@PathVariable id: UUID): ResponseEntity<ClienteResponse> {
        val cliente = consultarCliente.buscarPorId(id)
        return ResponseEntity.ok(ClienteResponse.from(cliente))
    }

    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable id: UUID,
        @Valid @RequestBody request: AtualizarClienteRequest
    ): ResponseEntity<ClienteResponse> {
        val cliente = atualizarCliente.executar(request.toCommand(id))
        return ResponseEntity.ok(ClienteResponse.from(cliente))
    }

    @DeleteMapping("/{id}")
    fun inativar(@PathVariable id: UUID): ResponseEntity<ClienteResponse> {
        val cliente = inativarCliente.executar(id)
        return ResponseEntity.ok(ClienteResponse.from(cliente))
    }
}

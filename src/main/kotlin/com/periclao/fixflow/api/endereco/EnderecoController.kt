package com.periclao.fixflow.api.endereco

import com.periclao.fixflow.api.endereco.request.AdicionarEnderecoRequest
import com.periclao.fixflow.api.endereco.request.AtualizarEnderecoRequest
import com.periclao.fixflow.api.endereco.response.EnderecoResponse
import com.periclao.fixflow.core.usecase.endereco.AdicionarEnderecoUseCase
import com.periclao.fixflow.core.usecase.endereco.AtualizarEnderecoUseCase
import com.periclao.fixflow.core.usecase.endereco.ConsultarEnderecosUseCase
import com.periclao.fixflow.core.usecase.endereco.RemoverEnderecoUseCase
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
@RequestMapping
class EnderecoController(
    private val adicionarEndereco: AdicionarEnderecoUseCase,
    private val atualizarEndereco: AtualizarEnderecoUseCase,
    private val consultarEnderecos: ConsultarEnderecosUseCase,
    private val removerEndereco: RemoverEnderecoUseCase
) {

    @PostMapping("/clientes/{clienteId}/enderecos")
    fun adicionar(
        @PathVariable clienteId: UUID,
        @Valid @RequestBody request: AdicionarEnderecoRequest
    ): ResponseEntity<EnderecoResponse> {
        val endereco = adicionarEndereco.executar(request.toCommand(clienteId))
        return ResponseEntity.status(HttpStatus.CREATED).body(EnderecoResponse.from(endereco))
    }

    @GetMapping("/clientes/{clienteId}/enderecos")
    fun listar(@PathVariable clienteId: UUID): ResponseEntity<List<EnderecoResponse>> {
        val enderecos = consultarEnderecos.listarPorCliente(clienteId).map { EnderecoResponse.from(it) }
        return ResponseEntity.ok(enderecos)
    }

    @GetMapping("/enderecos/{id}")
    fun buscar(@PathVariable id: UUID): ResponseEntity<EnderecoResponse> {
        val endereco = consultarEnderecos.buscarPorId(id)
        return ResponseEntity.ok(EnderecoResponse.from(endereco))
    }

    @PutMapping("/enderecos/{id}")
    fun atualizar(
        @PathVariable id: UUID,
        @Valid @RequestBody request: AtualizarEnderecoRequest
    ): ResponseEntity<EnderecoResponse> {
        val endereco = atualizarEndereco.executar(request.toCommand(id))
        return ResponseEntity.ok(EnderecoResponse.from(endereco))
    }

    @DeleteMapping("/enderecos/{id}")
    fun remover(@PathVariable id: UUID): ResponseEntity<Void> {
        removerEndereco.executar(id)
        return ResponseEntity.noContent().build()
    }
}

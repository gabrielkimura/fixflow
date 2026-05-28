package com.periclao.fixflow.api.tecnico

import com.periclao.fixflow.api.tecnico.request.AtualizarTecnicoRequest
import com.periclao.fixflow.api.tecnico.request.CriarTecnicoRequest
import com.periclao.fixflow.api.tecnico.response.TecnicoResponse
import com.periclao.fixflow.core.usecase.tecnico.AtualizarTecnicoUseCase
import com.periclao.fixflow.core.usecase.tecnico.ConsultarTecnicoUseCase
import com.periclao.fixflow.core.usecase.tecnico.CriarTecnicoUseCase
import com.periclao.fixflow.core.usecase.tecnico.InativarTecnicoUseCase
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
@RequestMapping("/tecnicos")
class TecnicoController(
    private val criarTecnico: CriarTecnicoUseCase,
    private val atualizarTecnico: AtualizarTecnicoUseCase,
    private val consultarTecnico: ConsultarTecnicoUseCase,
    private val inativarTecnico: InativarTecnicoUseCase
) {

    @PostMapping
    fun criar(@Valid @RequestBody request: CriarTecnicoRequest): ResponseEntity<TecnicoResponse> {
        val tecnico = criarTecnico.executar(request.toCommand())
        return ResponseEntity.status(HttpStatus.CREATED).body(TecnicoResponse.from(tecnico))
    }

    @GetMapping
    fun listar(): ResponseEntity<List<TecnicoResponse>> {
        val tecnicos = consultarTecnico.listarTodos().map { TecnicoResponse.from(it) }
        return ResponseEntity.ok(tecnicos)
    }

    @GetMapping("/{id}")
    fun buscar(@PathVariable id: UUID): ResponseEntity<TecnicoResponse> {
        val tecnico = consultarTecnico.buscarPorId(id)
        return ResponseEntity.ok(TecnicoResponse.from(tecnico))
    }

    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable id: UUID,
        @Valid @RequestBody request: AtualizarTecnicoRequest
    ): ResponseEntity<TecnicoResponse> {
        val tecnico = atualizarTecnico.executar(request.toCommand(id))
        return ResponseEntity.ok(TecnicoResponse.from(tecnico))
    }

    @DeleteMapping("/{id}")
    fun inativar(@PathVariable id: UUID): ResponseEntity<TecnicoResponse> {
        val tecnico = inativarTecnico.executar(id)
        return ResponseEntity.ok(TecnicoResponse.from(tecnico))
    }
}

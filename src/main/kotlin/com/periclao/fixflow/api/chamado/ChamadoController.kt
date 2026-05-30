package com.periclao.fixflow.api.chamado

import com.periclao.fixflow.api.chamado.request.AbrirChamadoRequest
import com.periclao.fixflow.api.chamado.request.AtribuirTecnicoChamadoRequest
import com.periclao.fixflow.api.chamado.request.ConcluirChamadoRequest
import com.periclao.fixflow.api.chamado.request.MudarStatusChamadoRequest
import com.periclao.fixflow.api.chamado.response.ChamadoResponse
import com.periclao.fixflow.core.usecase.chamado.AbrirChamadoUseCase
import com.periclao.fixflow.core.usecase.chamado.AtribuirTecnicoChamadoUseCase
import com.periclao.fixflow.core.usecase.chamado.CancelarChamadoUseCase
import com.periclao.fixflow.core.usecase.chamado.ConcluirChamadoUseCase
import com.periclao.fixflow.core.usecase.chamado.ConsultarChamadoUseCase
import com.periclao.fixflow.core.usecase.chamado.MudarStatusChamadoUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/chamados")
class ChamadoController(
    private val abrirChamado: AbrirChamadoUseCase,
    private val atribuirTecnico: AtribuirTecnicoChamadoUseCase,
    private val mudarStatus: MudarStatusChamadoUseCase,
    private val cancelarChamado: CancelarChamadoUseCase,
    private val concluirChamado: ConcluirChamadoUseCase,
    private val consultarChamado: ConsultarChamadoUseCase
) {

    @PostMapping
    fun abrir(@Valid @RequestBody request: AbrirChamadoRequest): ResponseEntity<ChamadoResponse> {
        val chamado = abrirChamado.executar(request.toCommand())
        return ResponseEntity.status(HttpStatus.CREATED).body(ChamadoResponse.from(chamado))
    }

    @GetMapping("/{id}")
    fun buscar(@PathVariable id: UUID): ResponseEntity<ChamadoResponse> {
        val chamado = consultarChamado.buscarPorId(id)
        return ResponseEntity.ok(ChamadoResponse.from(chamado))
    }

    @GetMapping(params = ["tecnicoId"])
    fun listarPorTecnico(@RequestParam tecnicoId: UUID): ResponseEntity<List<ChamadoResponse>> {
        val chamados = consultarChamado.listarPorTecnico(tecnicoId).map { ChamadoResponse.from(it) }
        return ResponseEntity.ok(chamados)
    }

    @GetMapping(params = ["clienteId"])
    fun listarPorCliente(@RequestParam clienteId: UUID): ResponseEntity<List<ChamadoResponse>> {
        val chamados = consultarChamado.listarPorCliente(clienteId).map { ChamadoResponse.from(it) }
        return ResponseEntity.ok(chamados)
    }

    @PatchMapping("/{id}/status")
    fun mudarStatus(
        @PathVariable id: UUID,
        @Valid @RequestBody request: MudarStatusChamadoRequest
    ): ResponseEntity<ChamadoResponse> {
        val chamado = mudarStatus.executar(request.toCommand(id))
        return ResponseEntity.ok(ChamadoResponse.from(chamado))
    }

    @PostMapping("/{id}/tecnico")
    fun atribuirTecnico(
        @PathVariable id: UUID,
        @Valid @RequestBody request: AtribuirTecnicoChamadoRequest
    ): ResponseEntity<ChamadoResponse> {
        val chamado = atribuirTecnico.executar(request.toCommand(id))
        return ResponseEntity.ok(ChamadoResponse.from(chamado))
    }

    @PostMapping("/{id}/conclusao")
    fun concluir(
        @PathVariable id: UUID,
        @Valid @RequestBody request: ConcluirChamadoRequest
    ): ResponseEntity<ChamadoResponse> {
        val chamado = concluirChamado.executar(request.toCommand(id))
        return ResponseEntity.ok(ChamadoResponse.from(chamado))
    }

    @PostMapping("/{id}/cancelamento")
    fun cancelar(@PathVariable id: UUID): ResponseEntity<ChamadoResponse> {
        val chamado = cancelarChamado.executar(id)
        return ResponseEntity.ok(ChamadoResponse.from(chamado))
    }
}

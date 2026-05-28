package com.periclao.fixflow.infrastructure.config

import com.periclao.fixflow.core.repository.ClienteRepositoryPort
import com.periclao.fixflow.core.repository.EnderecoRepositoryPort
import com.periclao.fixflow.core.repository.TecnicoRepositoryPort
import com.periclao.fixflow.core.usecase.cliente.AtualizarClienteUseCase
import com.periclao.fixflow.core.usecase.cliente.ConsultarClienteUseCase
import com.periclao.fixflow.core.usecase.cliente.CriarClienteUseCase
import com.periclao.fixflow.core.usecase.cliente.InativarClienteUseCase
import com.periclao.fixflow.core.usecase.cliente.impl.AtualizarClienteService
import com.periclao.fixflow.core.usecase.cliente.impl.ConsultarClienteService
import com.periclao.fixflow.core.usecase.cliente.impl.CriarClienteService
import com.periclao.fixflow.core.usecase.cliente.impl.InativarClienteService
import com.periclao.fixflow.core.usecase.endereco.AdicionarEnderecoUseCase
import com.periclao.fixflow.core.usecase.endereco.AtualizarEnderecoUseCase
import com.periclao.fixflow.core.usecase.endereco.ConsultarEnderecosUseCase
import com.periclao.fixflow.core.usecase.endereco.RemoverEnderecoUseCase
import com.periclao.fixflow.core.usecase.endereco.impl.AdicionarEnderecoService
import com.periclao.fixflow.core.usecase.endereco.impl.AtualizarEnderecoService
import com.periclao.fixflow.core.usecase.endereco.impl.ConsultarEnderecosService
import com.periclao.fixflow.core.usecase.endereco.impl.RemoverEnderecoService
import com.periclao.fixflow.core.usecase.tecnico.AtualizarTecnicoUseCase
import com.periclao.fixflow.core.usecase.tecnico.ConsultarTecnicoUseCase
import com.periclao.fixflow.core.usecase.tecnico.CriarTecnicoUseCase
import com.periclao.fixflow.core.usecase.tecnico.InativarTecnicoUseCase
import com.periclao.fixflow.core.usecase.tecnico.impl.AtualizarTecnicoService
import com.periclao.fixflow.core.usecase.tecnico.impl.ConsultarTecnicoService
import com.periclao.fixflow.core.usecase.tecnico.impl.CriarTecnicoService
import com.periclao.fixflow.core.usecase.tecnico.impl.InativarTecnicoService
import com.periclao.fixflow.infrastructure.repository.ClienteJpaRepository
import com.periclao.fixflow.infrastructure.repository.ClienteRepositoryAdapter
import com.periclao.fixflow.infrastructure.repository.EnderecoJpaRepository
import com.periclao.fixflow.infrastructure.repository.EnderecoRepositoryAdapter
import com.periclao.fixflow.infrastructure.repository.TecnicoJpaRepository
import com.periclao.fixflow.infrastructure.repository.TecnicoRepositoryAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UseCaseConfig(
    private val clienteJpaRepository: ClienteJpaRepository,
    private val enderecoJpaRepository: EnderecoJpaRepository,
    private val tecnicoJpaRepository: TecnicoJpaRepository
) {

    // ── Adapters de persistência ──────────────────────────────────────────────

    @Bean
    fun clienteRepositoryPort(): ClienteRepositoryPort =
        ClienteRepositoryAdapter(clienteJpaRepository, enderecoJpaRepository)

    @Bean
    fun enderecoRepositoryPort(): EnderecoRepositoryPort =
        EnderecoRepositoryAdapter(enderecoJpaRepository)

    // ── UseCases de Cliente ───────────────────────────────────────────────────

    @Bean
    fun criarClienteUseCase(clienteRepositoryPort: ClienteRepositoryPort): CriarClienteUseCase =
        CriarClienteService(clienteRepositoryPort)

    @Bean
    fun atualizarClienteUseCase(clienteRepositoryPort: ClienteRepositoryPort): AtualizarClienteUseCase =
        AtualizarClienteService(clienteRepositoryPort)

    @Bean
    fun consultarClienteUseCase(clienteRepositoryPort: ClienteRepositoryPort): ConsultarClienteUseCase =
        ConsultarClienteService(clienteRepositoryPort)

    @Bean
    fun inativarClienteUseCase(clienteRepositoryPort: ClienteRepositoryPort): InativarClienteUseCase =
        InativarClienteService(clienteRepositoryPort)

    // ── UseCases de Endereço ──────────────────────────────────────────────────

    @Bean
    fun adicionarEnderecoUseCase(
        clienteRepositoryPort: ClienteRepositoryPort,
        enderecoRepositoryPort: EnderecoRepositoryPort
    ): AdicionarEnderecoUseCase =
        AdicionarEnderecoService(clienteRepositoryPort, enderecoRepositoryPort)

    @Bean
    fun atualizarEnderecoUseCase(
        clienteRepositoryPort: ClienteRepositoryPort,
        enderecoRepositoryPort: EnderecoRepositoryPort
    ): AtualizarEnderecoUseCase =
        AtualizarEnderecoService(clienteRepositoryPort, enderecoRepositoryPort)

    @Bean
    fun consultarEnderecosUseCase(enderecoRepositoryPort: EnderecoRepositoryPort): ConsultarEnderecosUseCase =
        ConsultarEnderecosService(enderecoRepositoryPort)

    @Bean
    fun removerEnderecoUseCase(enderecoRepositoryPort: EnderecoRepositoryPort): RemoverEnderecoUseCase =
        RemoverEnderecoService(enderecoRepositoryPort)

    // ── Adapters e UseCases de Técnico ───────────────────────────────────────

    @Bean
    fun tecnicoRepositoryPort(): TecnicoRepositoryPort =
        TecnicoRepositoryAdapter(tecnicoJpaRepository)

    @Bean
    fun criarTecnicoUseCase(tecnicoRepositoryPort: TecnicoRepositoryPort): CriarTecnicoUseCase =
        CriarTecnicoService(tecnicoRepositoryPort)

    @Bean
    fun atualizarTecnicoUseCase(tecnicoRepositoryPort: TecnicoRepositoryPort): AtualizarTecnicoUseCase =
        AtualizarTecnicoService(tecnicoRepositoryPort)

    @Bean
    fun consultarTecnicoUseCase(tecnicoRepositoryPort: TecnicoRepositoryPort): ConsultarTecnicoUseCase =
        ConsultarTecnicoService(tecnicoRepositoryPort)

    @Bean
    fun inativarTecnicoUseCase(tecnicoRepositoryPort: TecnicoRepositoryPort): InativarTecnicoUseCase =
        InativarTecnicoService(tecnicoRepositoryPort)
}

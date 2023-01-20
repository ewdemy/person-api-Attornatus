package com.mrcruz.personapi.controller;

import com.mrcruz.personapi.model.Endereco;
import com.mrcruz.personapi.model.Pessoa;
import com.mrcruz.personapi.model.PessoaDTO;
import com.mrcruz.personapi.model.PessoaRequest;
import com.mrcruz.personapi.service.PessoaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Tag(name = "Pessoa Controller")
@AllArgsConstructor
@RestController
@RequestMapping("/pessoas")
public class PessoaController {

    private PessoaService pessoaService;

    @Operation(summary = "Salvar Nova Pessoa")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pessoa salvar(@RequestBody @Valid PessoaRequest pessoa){
        return pessoaService.salvar(pessoa);
    }

    @Operation(summary = "Listar Pessoas")
    @GetMapping
    public Page<PessoaDTO> listar(@RequestParam(required = false) String nome, @ParameterObject Pageable pageable){
        return pessoaService.listar(nome, pageable);
    }

    @Operation(summary = "Buscar Pessoa por Id")
    @GetMapping("/{id}")
    public Pessoa buscar(@PathVariable Long id){
        return pessoaService.buscar(id);
    }

    @Operation(summary = "Atualizar Pessoa")
    @PutMapping("/{id}")
    public Pessoa atualizar(@PathVariable Long id, @RequestBody @Valid PessoaRequest pessoa){
        return pessoaService.atualizar(id, pessoa);
    }

    @Operation(summary = "Deletar Pessoa")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id){
        pessoaService.deletar(id);
    }

    @Operation(summary = "Adicionar Novo Endereço para Pessoa")
    @PutMapping("/{idPessoa}/adicionar-endereco/{idEndereco}")
    public Pessoa adicionarEndereco(@PathVariable Long idPessoa, @PathVariable Long idEndereco){
        return pessoaService.adicionarEndereco(idPessoa, idEndereco);
    }

    @Operation(summary = "Adicionar Endereço Principal para Pessoa")
    @PutMapping("/{idPessoa}/adicionar-endereco-principal/{idEndereco}")
    public Pessoa adicionarEnderecoPrincipal(@PathVariable Long idPessoa, @PathVariable Long idEndereco){
        return pessoaService.adicionarEnderecoPrincipal(idPessoa, idEndereco);
    }

    @Operation(summary = "Remover Endereço da Pessoa")
    @PutMapping("/{idPessoa}/remover-endereco/{idEndereco}")
    public Pessoa removerEndereco(@PathVariable Long idPessoa, @PathVariable Long idEndereco){
        return pessoaService.removerEndereco(idPessoa, idEndereco);
    }

    @Operation(summary = "Listar Endereços da Pessoa")
    @GetMapping("/{idPessoa}/enderecos")
    public Set<Endereco> buscarEnderecosPessoa(@PathVariable Long idPessoa){
        return pessoaService.buscarEnderecosPessoa(idPessoa);
    }
}

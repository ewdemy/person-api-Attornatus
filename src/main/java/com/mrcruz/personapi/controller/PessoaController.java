package com.mrcruz.personapi.controller;

import com.mrcruz.personapi.model.Endereco;
import com.mrcruz.personapi.model.Pessoa;
import com.mrcruz.personapi.model.PessoaDTO;
import com.mrcruz.personapi.service.PessoaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping("/pessoas")
public class PessoaController {

    private PessoaService pessoaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pessoa salvar(@RequestBody @Valid Pessoa pessoa){
        return pessoaService.salvar(pessoa);
    }

    @GetMapping
    public Page<PessoaDTO> listar(@RequestParam(required = false) String nome, @ParameterObject Pageable pageable){
        return pessoaService.listar(nome, pageable);
    }

    @GetMapping("/{id}")
    public Pessoa buscar(@PathVariable Long id){
        return pessoaService.buscar(id);
    }

    @PutMapping("/{id}")
    public Pessoa atualizar(@PathVariable Long id, @RequestBody @Valid Pessoa pessoa){
        return pessoaService.atualizar(id, pessoa);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id){
        pessoaService.deletar(id);
    }

    @PutMapping("/{idPessoa}/adicionar-endereco/{idEndereco}")
    public Pessoa adicionarEndereco(@PathVariable Long idPessoa, @PathVariable Long idEndereco){
        return pessoaService.adicionarEndereco(idPessoa, idEndereco);
    }

    @PutMapping("/{idPessoa}/adicionar-endereco-principal/{idEndereco}")
    public Pessoa adicionarEnderecoPrincipal(@PathVariable Long idPessoa, @PathVariable Long idEndereco){
        return pessoaService.adicionarEnderecoPrincipal(idPessoa, idEndereco);
    }

    @PutMapping("/{idPessoa}/remover-endereco/{idEndereco}")
    public Pessoa removerEndereco(@PathVariable Long idPessoa, @PathVariable Long idEndereco){
        return pessoaService.removerEndereco(idPessoa, idEndereco);
    }

    @GetMapping("/{idPessoa}/enderecos")
    public Set<Endereco> buscarEnderecosPessoa(@PathVariable Long idPessoa){
        return pessoaService.buscarEnderecosPessoa(idPessoa);
    }
}

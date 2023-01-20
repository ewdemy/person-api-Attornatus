package com.mrcruz.personapi.controller;

import com.mrcruz.personapi.model.Endereco;
import com.mrcruz.personapi.service.EnderecoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Endereço Controller")
@AllArgsConstructor
@RestController
@RequestMapping("/enderecos")
public class EnderecoController {

    private EnderecoService enderecoService;

    @Operation(summary = "Salvar Novo Endereço")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Endereco salvar(@RequestBody @Valid Endereco endereco){
        return enderecoService.salvar(endereco);
    }

    @Operation(summary = "Listar Endereços")
    @GetMapping
    public Page<Endereco> listar(@ParameterObject Pageable pageable){
        return enderecoService.listar(pageable);
    }

    @Operation(summary = "Buscar Endereço por Id")
    @GetMapping("/{id}")
    public Endereco buscar(@PathVariable Long id){
        return enderecoService.buscar(id);
    }

    @Operation(summary = "Atualizar Endereço")
    @PutMapping("{id}")
    public Endereco atualizar(@PathVariable Long id, @RequestBody @Valid Endereco endereco){
        return enderecoService.atualizar(id, endereco);
    }

    @Operation(summary = "Deletar Endereço")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id){
        enderecoService.deletar(id);
    }
}

package com.mrcruz.personapi.controller;

import com.mrcruz.personapi.model.Endereco;
import com.mrcruz.personapi.service.EnderecoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/enderecos")
public class EnderecoController {

    private EnderecoService enderecoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Endereco salvar(@RequestBody @Valid Endereco endereco){
        return enderecoService.salvar(endereco);
    }

    @GetMapping
    public Page<Endereco> listar(Pageable pageable){
        return enderecoService.listar(pageable);
    }

    @GetMapping("/{id}")
    public Endereco buscar(@PathVariable Long id){
        return enderecoService.buscar(id);
    }

    @PutMapping("{id}")
    public Endereco atualizar(@PathVariable Long id, @RequestBody @Valid Endereco endereco){
        return enderecoService.atualizar(id, endereco);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id){
        enderecoService.deletar(id);
    }
}

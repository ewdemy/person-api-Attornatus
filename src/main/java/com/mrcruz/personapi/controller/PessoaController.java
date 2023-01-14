package com.mrcruz.personapi.controller;

import com.mrcruz.personapi.model.Pessoa;
import com.mrcruz.personapi.service.PessoaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    public Page<Pessoa> listar(Pageable pageable){
        return pessoaService.listar(pageable);
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
}

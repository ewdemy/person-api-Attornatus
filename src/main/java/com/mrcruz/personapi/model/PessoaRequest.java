package com.mrcruz.personapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PessoaRequest {

    @NotBlank( message = "Deve ser preenchido")
    private String nome;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataNascimento;

    public Pessoa toModel(){
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(this.getNome());
        pessoa.setDataNascimento(this.getDataNascimento());
        return pessoa;
    }
}

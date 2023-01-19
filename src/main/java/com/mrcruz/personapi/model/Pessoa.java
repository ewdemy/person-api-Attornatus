package com.mrcruz.personapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataNascimento;

    @ManyToOne
    private Endereco enderecoPrincipal;

    @ManyToMany(cascade ={CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(name="PESSOA_ENDERECO",
            joinColumns={@JoinColumn(name="PESSOA_ID")},
            inverseJoinColumns={@JoinColumn(name="ENDERECO_ID")})
    private Set<Endereco> enderecos = new HashSet<>();

    public Pessoa(String nome, LocalDate dataNascimento){
        this.nome = nome;
        this.dataNascimento = dataNascimento;
    }

}

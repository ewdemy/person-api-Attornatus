package com.mrcruz.personapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToMany(cascade ={CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(name="PESSOA_ENDERECO",
            joinColumns={@JoinColumn(name="PESSOA_ID")},
            inverseJoinColumns={@JoinColumn(name="ENDERECO_ID")})
    private List<Endereco> enderecos = new ArrayList<>();

}

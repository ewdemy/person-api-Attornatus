package com.mrcruz.personapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank( message = "Deve ser preenchido")
    private String logradouro;

    @NotBlank( message = "Deve ser preenchido")
    private String cep;
    private String numero;

    @NotBlank( message = "Deve ser preenchida")
    private String cidade;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private boolean principal = false;

}

package com.mrcruz.personapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Endereco {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank( message = "Deve ser preenchido")
    private String logradouro;

    @NotBlank( message = "Deve ser preenchido")
    private String cep;
    private String numero;

    @NotBlank( message = "Deve ser preenchida")
    private String cidade;

}

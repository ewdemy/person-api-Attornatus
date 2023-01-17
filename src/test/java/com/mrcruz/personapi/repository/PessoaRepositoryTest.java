package com.mrcruz.personapi.repository;

import com.mrcruz.personapi.model.Pessoa;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class PessoaRepositoryTest {

    @Autowired
    private PessoaRepository pessoaRepository;

    @BeforeEach
    void setUp() {
        pessoaRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void deveListarPessoaPorNome() {
        Pessoa p1 = pessoaRepository.save(new Pessoa("Ana Karla", LocalDate.of(2000, 12, 10)));
        Pessoa p2 = pessoaRepository.save(new Pessoa("Karen Rachel", LocalDate.of(2001, 2, 1)));
        Pessoa p3 = pessoaRepository.save(new Pessoa("Ana Maria", LocalDate.of(1999, 3, 16)));


        Page<Pessoa> pessoas = pessoaRepository.findByNomeContainingIgnoreCase("Ana", Pageable.ofSize(20));

        assertFalse(pessoas.isEmpty());
        assertEquals(2, pessoas.getContent().size());
        assertEquals(p1.getId(), pessoas.getContent().get(0).getId());
        assertEquals(p3.getId(), pessoas.getContent().get(1).getId());

    }
}
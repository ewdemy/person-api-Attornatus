package com.mrcruz.personapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mrcruz.personapi.model.Pessoa;
import com.mrcruz.personapi.model.PessoaRequest;
import com.mrcruz.personapi.service.PessoaService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PessoaController.class)
class PessoaControllerTest {

    @MockBean
    PessoaService pessoaService;

    ObjectMapper mapper = new ObjectMapper();


    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
    }

    @Autowired
    MockMvc mockMvc;

    @Test
    void deveSalvarPessoa() throws Exception {
        PessoaRequest pessoaRequest = getPessoaRequest();

        Mockito.when(pessoaService.salvar(Mockito.any(PessoaRequest.class))).thenReturn(getPessoa());

        mockMvc.perform(post("/pessoas")
                        .content(mapper.writeValueAsString(pessoaRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andExpect(jsonPath("$.nome", Matchers.is("Bob Jhon")));
    }

    @Test
    void deveLancarExcecaoAoSalvarPessoaComNomeEmBrancoOuNulo() throws Exception {
        PessoaRequest pessoaRequest = getPessoaRequest();
        pessoaRequest.setNome("");

        mockMvc.perform(post("/pessoas")
                        .content(mapper.writeValueAsString(pessoaRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem", Matchers.is("Um ou mais campos inválidos, preencha corretamente!")));
    }

    @Test
    void listar() {
    }

    @Test
    void buscar() {
    }

    @Test
    void atualizar() {
    }

    @Test
    void deletar() {
    }

    @Test
    void adicionarEndereco() {
    }

    @Test
    void adicionarEnderecoPrincipal() {
    }

    @Test
    void removerEndereco() {
    }

    @Test
    void buscarEnderecosPessoa() {
    }

    private PessoaRequest getPessoaRequest(){
        return new PessoaRequest("Bob Jhon", LocalDate.of(1995, 10, 05));
    }

    private Pessoa getPessoa(){
        Pessoa pessoa = new Pessoa("Bob Jhon", LocalDate.of(1995, 10, 05));
        pessoa.setId(1L);
        return pessoa;
    }
}
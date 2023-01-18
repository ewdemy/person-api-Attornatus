package com.mrcruz.personapi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrcruz.personapi.model.Endereco;
import com.mrcruz.personapi.service.EnderecoService;
import jakarta.persistence.EntityNotFoundException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EnderecoController.class)
class EnderecoControllerTest {

    @MockBean
    private EnderecoService enderecoService;

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void deveSalvarEndereco() throws Exception {
        Endereco enderecoResponse = getEndereco();
        Endereco enderecorequest = getEnderecoRequest();

        Mockito.when(enderecoService.buscar(Mockito.anyLong())).thenReturn(enderecoResponse);

        mockMvc.perform(post("/enderecos", mapper.writeValueAsString(enderecorequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andExpect(jsonPath("$.logradouro", Matchers.is("Rua A")))
                .andExpect(jsonPath("$.cidade", Matchers.is("Fogareiro City")));

    }

    @Test
    void deveLancarExcecaoAoSalvarComAlgumAtributoEmBrancoOuNulo() {


    }

    @Test
    void listar() throws Exception {
        Page<Endereco> pageResponse = new PageImpl<>(getEnderecos());

        Mockito.when(enderecoService.listar(Pageable.ofSize(20))).thenReturn(pageResponse);

        mockMvc.perform(get("/enderecos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(3)))
                .andExpect(jsonPath("content[0].id", Matchers.is(1)));
    }

    @Test
    void deveBuscarEnderecoPorId() throws Exception {

        Endereco endereco = getEndereco();
        Mockito.when(enderecoService.buscar(Mockito.anyLong())).thenReturn(endereco);

        mockMvc.perform(get("/enderecos/{id}",1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andExpect(jsonPath("$.logradouro", Matchers.is("Rua A")));
    }
    @Test
    void deveLancarExcecaoAoBuscarEnderecoPorIdInexistente() throws Exception {

        Long id = 10L;
        Mockito.when(enderecoService.buscar(id)).thenThrow(new EntityNotFoundException("Endereço não encontrado com ID: " + id));

        mockMvc.perform(get("/enderecos/{id}",id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem", Matchers.is("Endereço não encontrado com ID: 10")));
    }

    @Test
    void atualizar() {
    }

    @Test
    void deletar() {
    }

    private List<Endereco> getEnderecos() {
        Endereco end1 = new Endereco();
        end1.setId(1L);
        end1.setLogradouro("Rua 25 de Março");
        end1.setCep("54512161");
        end1.setNumero("568");
        end1.setCidade("Quixeramobim");

        Endereco end2 = new Endereco();
        end2.setId(2L);
        end2.setLogradouro("Rua 25 de Março");
        end2.setCep("54512161");
        end2.setNumero("568");
        end2.setCidade("Quixeramobim");

        Endereco end3 = new Endereco();
        end3.setId(3L);
        end3.setLogradouro("Rua 25 de Março");
        end3.setCep("54512161");
        end3.setNumero("568");
        end3.setCidade("Quixeramobim");

        return Arrays.asList(end1, end2, end3);
    }
    private Endereco getEndereco(){
        return new Endereco(1L, "Rua A", "32644878", "33", "Fogareiro City");
    }
    private Endereco getEnderecoRequest(){
        Endereco enderecorequest = new Endereco();
        enderecorequest.setLogradouro("Rua A");
        enderecorequest.setCep("32644878");
        enderecorequest.setNumero("33");
        enderecorequest.setCidade("Fogareiro City");

        return enderecorequest;
    }
}
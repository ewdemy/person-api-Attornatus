package com.mrcruz.personapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mrcruz.personapi.model.Pessoa;
import com.mrcruz.personapi.model.PessoaDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    void deveListarPessoa() throws Exception {
        Page<PessoaDTO> pageResponse = new PageImpl<>(getPessoasDTO());

        Mockito.when(pessoaService.listar(null, Pageable.ofSize(20))).thenReturn(pageResponse);

        mockMvc.perform(get("/pessoas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(4)))
                .andExpect(jsonPath("content[0].id", Matchers.is(1)));
    }

    @Test
    void deveListarPessoaComFiltro() throws Exception {
        String filtro = "Ana";
        Page<PessoaDTO> pageResponse = new PageImpl<>(getPessoasDTOComFiltro(filtro));

        Mockito.when(pessoaService.listar(filtro, Pageable.ofSize(20))).thenReturn(pageResponse);

        mockMvc.perform(get("/pessoas?nome=Ana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(2)))
                .andExpect(jsonPath("content[0].nome", Matchers.is("Ana Kelly")))
                .andExpect(jsonPath("content[1].nome", Matchers.is("Maria Ana")));
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
    private List<PessoaDTO> getPessoasDTO(){
        PessoaDTO p1 = new  PessoaDTO(1L, "Bob Jhon", LocalDate.of(1995, 10, 05), null);
        PessoaDTO p2 = new  PessoaDTO(2L, "Ana Kelly", LocalDate.of(2000, 5, 10), null);
        PessoaDTO p3 = new  PessoaDTO(3L, "Jacob Fernandes", LocalDate.of(1985, 11, 20), null);
        PessoaDTO p4 = new  PessoaDTO(3L, "Maria Ana", LocalDate.of(2002, 3, 15), null);

        return Arrays.asList(p1, p2, p3, p4);
    }

    private List<PessoaDTO> getPessoasDTOComFiltro(String filtro){
        return getPessoasDTO().stream().filter(pessoaDTO -> pessoaDTO.getNome().contains(filtro)).collect(Collectors.toList());
    }
}
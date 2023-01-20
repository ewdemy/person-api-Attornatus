package com.mrcruz.personapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mrcruz.personapi.model.Endereco;
import com.mrcruz.personapi.model.Pessoa;
import com.mrcruz.personapi.model.PessoaDTO;
import com.mrcruz.personapi.model.PessoaRequest;
import com.mrcruz.personapi.service.PessoaService;
import jakarta.persistence.EntityNotFoundException;
import org.hamcrest.Matchers;
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
import java.util.Set;
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
    void deveBuscarPessoa() throws Exception {

        Mockito.when(pessoaService.buscar(Mockito.anyLong())).thenReturn(getPessoa());

        mockMvc.perform(get("/pessoas/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andExpect(jsonPath("$.nome", Matchers.is("Bob Jhon")));
    }

    @Test
    void deveLancarAoBuscarPessoaComIdInexistente() throws Exception {
        Long id = 10L;

        Mockito.when(pessoaService.buscar(Mockito.anyLong())).thenThrow(new EntityNotFoundException("Pessoa não encontrada com ID: " + id));

        mockMvc.perform(get("/pessoas/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem", Matchers.is("Pessoa não encontrada com ID: " + id)));
    }

    @Test
    void deveAtualizarPessoa() throws Exception {
        PessoaRequest pessoaRequest = getPessoaRequest();

        Mockito.when(pessoaService.atualizar(Mockito.anyLong(), Mockito.any(PessoaRequest.class))).thenReturn(getPessoa());

        mockMvc.perform(put("/pessoas/{id}", 1L)
                        .content(mapper.writeValueAsString(pessoaRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andExpect(jsonPath("$.nome", Matchers.is("Bob Jhon")));
    }

    @Test
    void deveLancarExcecaoAoAtualizarPessoaComIdInexistente() throws Exception {
        Long id = 10L;
        PessoaRequest pessoaRequest = getPessoaRequest();

        Mockito.when(pessoaService.atualizar(Mockito.anyLong(), Mockito.any(PessoaRequest.class)))
                .thenThrow(new EntityNotFoundException("Pessoa não encontrada com ID: " + id));

        mockMvc.perform(put("/pessoas/{id}", id)
                        .content(mapper.writeValueAsString(pessoaRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem", Matchers.is("Pessoa não encontrada com ID: " + id)));
    }

    @Test
    void deveLancarExcecaoAoAtualizarPessoaComNomeEmBrancoOuNulo() throws Exception {
        PessoaRequest pessoaRequest = getPessoaRequest();
        pessoaRequest.setNome("");

        mockMvc.perform(put("/pessoas/{id}", 1L)
                        .content(mapper.writeValueAsString(pessoaRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem", Matchers.is("Um ou mais campos inválidos, preencha corretamente!")));
    }
    @Test
    void deveDeletarPessoa() throws Exception {
        Long id = 1L;

        Mockito.doNothing().when(pessoaService).deletar(id);

        mockMvc.perform(delete("/pessoas/{id}",id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveLancarExcecaoAoDeletarPessoaComIdInexistente() throws Exception {
        Long id = 10L;

        Mockito.doThrow(new EntityNotFoundException("Pessoa não encontrada com ID: " + id)).when(pessoaService).deletar(id);

        mockMvc.perform(delete("/pessoas/{id}",id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem", Matchers.is("Pessoa não encontrada com ID: " + id)));
    }

    @Test
    void deveAdicionarEnderecoAPessoa() throws Exception {

        Mockito.when(pessoaService.adicionarEndereco(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(getPessoaComEndereco());

        mockMvc.perform(put("/pessoas/{id-pessoa}/adicionar-endereco/{id-endereco}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andExpect(jsonPath("$.enderecoPrincipal", Matchers.notNullValue(Endereco.class)))
                .andExpect(jsonPath("$.enderecoPrincipal.id", Matchers.is(1)))
                .andExpect(jsonPath("$.enderecos", Matchers.notNullValue()))
                .andExpect(jsonPath("$.enderecos", Matchers.hasSize(1)));
    }

    @Test
    void deveLancarExcecaoAoAdicionarEnderecoAPessoaInexistente() throws Exception {

        Long idPessoa = 10L;
        Long idEndereco = 1L;

        Mockito.when(pessoaService.adicionarEndereco(Mockito.anyLong(), Mockito.anyLong())).thenThrow(new EntityNotFoundException("Pessoa não encontrada com ID: " + idPessoa));

        mockMvc.perform(put("/pessoas/{id-pessoa}/adicionar-endereco/{id-endereco}", idPessoa, idEndereco))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem", Matchers.is("Pessoa não encontrada com ID: " + idPessoa)));
    }

    @Test
    void deveLancarExcecaoAoAdicionarEnderecoInexistenteAPessoa() throws Exception {

        Long idPessoa = 1L;
        Long idEndereco = 10L;

        Mockito.when(pessoaService.adicionarEndereco(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new EntityNotFoundException("Endereço não encontrado com ID: " + idEndereco));

        mockMvc.perform(put("/pessoas/{id-pessoa}/adicionar-endereco/{id-endereco}", idPessoa, idEndereco))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem", Matchers.is("Endereço não encontrado com ID: " + idEndereco)));
    }

    @Test
    void deveRemoverEnderecoDaPessoa() throws Exception {
        Mockito.when(pessoaService.removerEndereco(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(getPessoa());

        mockMvc.perform(put("/pessoas/{id-pessoa}/remover-endereco/{id-endereco}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andExpect(jsonPath("$.enderecoPrincipal", Matchers.nullValue()))
                .andExpect(jsonPath("$.enderecos", Matchers.empty()));
    }

    @Test
    void deveLancarExcecaoAoRemoverEnderecoAPessoaInexistente() throws Exception {

        Long idPessoa = 10L;
        Long idEndereco = 1L;

        Mockito.when(pessoaService.removerEndereco(Mockito.anyLong(), Mockito.anyLong())).thenThrow(new EntityNotFoundException("Pessoa não encontrada com ID: " + idPessoa));

        mockMvc.perform(put("/pessoas/{id-pessoa}/remover-endereco/{id-endereco}", idPessoa, idEndereco))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem", Matchers.is("Pessoa não encontrada com ID: " + idPessoa)));
    }

    @Test
    void deveLancarExcecaoAoRemoverEnderecoInexistenteAPessoa() throws Exception {

        Long idPessoa = 1L;
        Long idEndereco = 10L;

        Mockito.when(pessoaService.removerEndereco(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new EntityNotFoundException("Endereço não encontrado com ID: " + idEndereco));

        mockMvc.perform(put("/pessoas/{id-pessoa}/remover-endereco/{id-endereco}", idPessoa, idEndereco))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem", Matchers.is("Endereço não encontrado com ID: " + idEndereco)));
    }

    @Test
    void deveLancarExcecaoAoRemoverEnderecoQueNaoPertenceAPessoa() throws Exception {

        Long idPessoa = 1L;
        Long idEndereco = 10L;

        Mockito.when(pessoaService.removerEndereco(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new UnsupportedOperationException("Pessoa com ID: " + idPessoa + " não possui endereço com ID: " + idEndereco));

        mockMvc.perform(put("/pessoas/{id-pessoa}/remover-endereco/{id-endereco}", idPessoa, idEndereco))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.mensagem", Matchers.is("Pessoa com ID: " + idPessoa + " não possui endereço com ID: " + idEndereco)));
    }

    @Test
    void adicionarEnderecoPrincipal() throws Exception {
        Mockito.when(pessoaService.adicionarEnderecoPrincipal(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(getPessoaComEndereco());

        mockMvc.perform(put("/pessoas/{id-pessoa}/adicionar-endereco-principal/{id-endereco}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andExpect(jsonPath("$.enderecoPrincipal", Matchers.notNullValue(Endereco.class)))
                .andExpect(jsonPath("$.enderecoPrincipal.id", Matchers.is(1)))
                .andExpect(jsonPath("$.enderecoPrincipal.logradouro", Matchers.is("Rua 25 de Março")));
    }

    @Test
    void deveLancarExcecaoAoAdicionarEnderecoPrincipalAPessoaInexistente() throws Exception {

        Long idPessoa = 10L;
        Long idEndereco = 1L;

        Mockito.when(pessoaService.adicionarEnderecoPrincipal(Mockito.anyLong(), Mockito.anyLong())).thenThrow(new EntityNotFoundException("Pessoa não encontrada com ID: " + idPessoa));

        mockMvc.perform(put("/pessoas/{id-pessoa}/adicionar-endereco-principal/{id-endereco}", idPessoa, idEndereco))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem", Matchers.is("Pessoa não encontrada com ID: " + idPessoa)));
    }

    @Test
    void deveLancarExcecaoAoAdicionarEnderecoPrincipalInexistenteAPessoa() throws Exception {

        Long idPessoa = 1L;
        Long idEndereco = 10L;

        Mockito.when(pessoaService.adicionarEnderecoPrincipal(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new EntityNotFoundException("Endereço não encontrado com ID: " + idEndereco));

        mockMvc.perform(put("/pessoas/{id-pessoa}/adicionar-endereco-principal/{id-endereco}", idPessoa, idEndereco))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem", Matchers.is("Endereço não encontrado com ID: " + idEndereco)));
    }

    @Test
    void deveLancarExcecaoAoAdicionarEnderecoPrincipalQueNaoPertenceAPessoa() throws Exception {

        Long idPessoa = 1L;
        Long idEndereco = 10L;

        Mockito.when(pessoaService.adicionarEnderecoPrincipal(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new UnsupportedOperationException("Pessoa com ID: " + idPessoa + " não possui endereço com ID: " + idEndereco));

        mockMvc.perform(put("/pessoas/{id-pessoa}/adicionar-endereco-principal/{id-endereco}", idPessoa, idEndereco))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.mensagem", Matchers.is("Pessoa com ID: " + idPessoa + " não possui endereço com ID: " + idEndereco)));
    }

    @Test
    void deveBuscarEnderecosDaPessoa() throws Exception {
        Mockito.when(pessoaService.buscarEnderecosPessoa(Mockito.anyLong())).thenReturn(getEnderecos());

        mockMvc.perform(get("/pessoas/{id}/enderecos", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(3)));
    }


    private PessoaRequest getPessoaRequest(){
        return new PessoaRequest("Bob Jhon", LocalDate.of(1995, 10, 5));
    }

    private Pessoa getPessoa(){

        Pessoa pessoa = new Pessoa("Bob Jhon", LocalDate.of(1995, 10, 5));
        pessoa.setId(1L);
        return pessoa;
    }
    private Pessoa getPessoaComEndereco(){
        Endereco endereco = new Endereco();
        endereco.setId(1L);
        endereco.setLogradouro("Rua 25 de Março");
        endereco.setCep("54512161");
        endereco.setNumero("568");
        endereco.setCidade("Quixeramobim");

        Pessoa pessoa = new Pessoa("Bob Jhon", LocalDate.of(1995, 10, 5));
        pessoa.setId(1L);
        pessoa.setEnderecoPrincipal(endereco);
        pessoa.setEnderecos(Set.of(endereco));
        return pessoa;
    }

    private List<PessoaDTO> getPessoasDTO(){
        PessoaDTO p1 = new  PessoaDTO(1L, "Bob Jhon", LocalDate.of(1995, 10, 5), null);
        PessoaDTO p2 = new  PessoaDTO(2L, "Ana Kelly", LocalDate.of(2000, 5, 10), null);
        PessoaDTO p3 = new  PessoaDTO(3L, "Jacob Fernandes", LocalDate.of(1985, 11, 20), null);
        PessoaDTO p4 = new  PessoaDTO(3L, "Maria Ana", LocalDate.of(2002, 3, 15), null);

        return Arrays.asList(p1, p2, p3, p4);
    }

    private List<PessoaDTO> getPessoasDTOComFiltro(String filtro){
        return getPessoasDTO().stream().filter(pessoaDTO -> pessoaDTO.getNome().contains(filtro)).collect(Collectors.toList());
    }
    private Set<Endereco> getEnderecos() {
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

        return Set.of(end1, end2, end3);
    }
}
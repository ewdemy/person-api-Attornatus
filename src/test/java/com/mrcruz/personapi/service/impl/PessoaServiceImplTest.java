package com.mrcruz.personapi.service.impl;

import com.mrcruz.personapi.model.Endereco;
import com.mrcruz.personapi.model.Pessoa;
import com.mrcruz.personapi.model.PessoaDTO;
import com.mrcruz.personapi.model.PessoaRequest;
import com.mrcruz.personapi.repository.PessoaRepository;
import com.mrcruz.personapi.service.EnderecoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {PessoaServiceImpl.class})
@ExtendWith(SpringExtension.class)
class PessoaServiceImplTest {
    @MockBean
    private EnderecoService enderecoService;

    @MockBean
    private PessoaRepository pessoaRepository;

    @Autowired
    private PessoaServiceImpl pessoaServiceImpl;

    @Test
    void DeveSalvarPessoa() {

        Pessoa pessoa = getPessoa();

        when(pessoaRepository.save(any(Pessoa.class))).thenReturn(pessoa);

        pessoaServiceImpl.salvar(new PessoaRequest("Bob Jhon", LocalDate.of(1995, 10, 5)));
        verify(pessoaRepository).save(any(Pessoa.class));
    }

    @Test
    void deveBuscarPessoa() {
        Pessoa pessoa = getPessoa();
        Optional<Pessoa> optionalPessoa = Optional.of(pessoa);
        when(pessoaRepository.findById(anyLong())).thenReturn(optionalPessoa);
        assertSame(pessoa, pessoaServiceImpl.buscar(1L));
        verify(pessoaRepository).findById(anyLong());
    }

    @Test
    void deveLancarExcecaoAoBuscarPessoaComIdInexistente() {
        when(pessoaRepository.findById(anyLong())).thenReturn(Optional.empty());

        var exception = assertThrows(EntityNotFoundException.class, () -> pessoaServiceImpl.buscar(10L));
        assertEquals(EntityNotFoundException.class, exception.getClass());
        assertEquals("Pessoa não encontrada com ID: 10", exception.getMessage());
        verify(pessoaRepository).findById(anyLong());
    }

    @Test
    void deveListarPessoa() {
        Page<Pessoa> pageResponse = new PageImpl<>(getPessoas());

        when(pessoaRepository.findAll(Pageable.ofSize(20))).thenReturn(pageResponse);
        Page<PessoaDTO> result = pessoaServiceImpl.listar(null, Pageable.ofSize(20));
        assertFalse(result.toList().isEmpty());
        assertEquals(4, result.getContent().size());
        verify(pessoaRepository).findAll(any(Pageable.class));
        verify(pessoaRepository, never()).findByNomeContainingIgnoreCase(any(),any(Pageable.class));
    }

    @Test
    void deveListarPessoaComFiltro() {
        String filtro = "Ana";
        Page<Pessoa> pageResponse = new PageImpl<>(getPessoasComFiltro(filtro));

        when(pessoaRepository.findByNomeContainingIgnoreCase(filtro, Pageable.ofSize(20))).thenReturn(pageResponse);
        Page<PessoaDTO> result = pessoaServiceImpl.listar(filtro, Pageable.ofSize(20));
        assertFalse(result.toList().isEmpty());
        assertEquals(2, result.getContent().size());
        verify(pessoaRepository).findByNomeContainingIgnoreCase(anyString(), any(Pageable.class));
        verify(pessoaRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void deveAtualizarPessoa() {
        Pessoa pessoa = getPessoa();
        Optional<Pessoa> optionalPessoa = Optional.of(pessoa);

        when(pessoaRepository.findById(anyLong())).thenReturn(optionalPessoa);
        when(pessoaRepository.save(any(Pessoa.class))).thenReturn(pessoa);

        pessoaServiceImpl.atualizar(1L, getPessoaRequest());
        verify(pessoaRepository).findById(anyLong());
        verify(pessoaRepository).save(any(Pessoa.class));
    }

    @Test
    void deveLancarexcecaoAoAtualizarPessoaComIdInexistente() {
        PessoaRequest pessoaRequest = getPessoaRequest();
        when(pessoaRepository.findById(anyLong())).thenReturn(Optional.empty());
        var exception = assertThrows(EntityNotFoundException.class, () -> pessoaServiceImpl.atualizar(10L, pessoaRequest));
        assertEquals(EntityNotFoundException.class, exception.getClass());
        assertEquals("Pessoa não encontrada com ID: 10", exception.getMessage());
        verify(pessoaRepository).findById(anyLong());
        verify(pessoaRepository, never()).save(any(Pessoa.class));
    }

    @Test
    void deveDeletarPessoa() {
        Pessoa pessoa = getPessoa();
        Optional<Pessoa> optionalPessoa = Optional.of(pessoa);

        when(pessoaRepository.findById(anyLong())).thenReturn(optionalPessoa);
        doNothing().when(pessoaRepository).delete(any(Pessoa.class));
        pessoaServiceImpl.deletar(1L);
        verify(pessoaRepository).findById(anyLong());
        verify(pessoaRepository).delete(any(Pessoa.class));
    }

    @Test
    void deveLancarExcecaoAoDeletarPessoaComIdInexistente() {
        when(pessoaRepository.findById(anyLong())).thenReturn(Optional.empty());

        var exception = assertThrows(EntityNotFoundException.class, () -> pessoaServiceImpl.deletar(10L));
        assertEquals(EntityNotFoundException.class, exception.getClass());
        assertEquals("Pessoa não encontrada com ID: 10", exception.getMessage());
        verify(pessoaRepository).findById(anyLong());
        verify(pessoaRepository, never()).delete(any(Pessoa.class));
    }

    @Test
    void deveAdicionarEnderecoAPessoa() {
        Endereco endereco = getEndereco();

        Pessoa pessoaComEndereco = getPessoaComEndereco();
        Pessoa pessoa = getPessoa();
        Optional<Pessoa> optionalPessoa = Optional.of(pessoa);

        when(pessoaRepository.findById(anyLong())).thenReturn(optionalPessoa);
        when(enderecoService.buscar(anyLong())).thenReturn(endereco);
        when(pessoaRepository.save(any(Pessoa.class))).thenReturn(pessoaComEndereco);

        pessoaServiceImpl.adicionarEndereco(1L, 1L);

        verify(pessoaRepository).findById(anyLong());
        verify(enderecoService).buscar(anyLong());
        verify(pessoaRepository).save(any(Pessoa.class));
    }

    @Test
    void deveLancarExcecaoAoAdicionarEnderecoAPessoaComIdInexistente() {
        when(pessoaRepository.findById(anyLong())).thenReturn(Optional.empty());

        var exception = assertThrows(EntityNotFoundException.class, () -> pessoaServiceImpl.adicionarEndereco(10L, 1L));
        assertEquals(EntityNotFoundException.class, exception.getClass());
        assertEquals("Pessoa não encontrada com ID: 10", exception.getMessage());
        verify(pessoaRepository).findById(anyLong());
        verify(enderecoService, never()).buscar(anyLong());
        verify(pessoaRepository, never()).save(any(Pessoa.class));
    }

    @Test
    void deveLancarExcecaoAoAdicionarEnderecoComIdInexistenteAPessoa() {
        Pessoa pessoa = getPessoa();
        Optional<Pessoa> optionalPessoa = Optional.of(pessoa);

        when(pessoaRepository.findById(anyLong())).thenReturn(optionalPessoa);
        when(enderecoService.buscar(anyLong())).thenThrow(new EntityNotFoundException("Endereço não encontrado com ID: 10"));

        var exception = assertThrows(EntityNotFoundException.class, () -> pessoaServiceImpl.adicionarEndereco(1L, 10L));
        assertEquals(EntityNotFoundException.class, exception.getClass());
        assertEquals("Endereço não encontrado com ID: 10", exception.getMessage());
        verify(pessoaRepository).findById(anyLong());
        verify(enderecoService).buscar(anyLong());
        verify(pessoaRepository, never()).save(any(Pessoa.class));
    }

    @Test
    void deveRemoverEnderecoDaPessoa() {
        Endereco endereco = getEndereco();

        Pessoa pessoaComEndereco = getPessoaComEndereco();
        Pessoa pessoa = getPessoa();
        Optional<Pessoa> optionalPessoa = Optional.of(pessoaComEndereco);

        when(pessoaRepository.findById(anyLong())).thenReturn(optionalPessoa);
        when(enderecoService.buscar(anyLong())).thenReturn(endereco);
        when(pessoaRepository.save(any(Pessoa.class))).thenReturn(pessoa);

        pessoaServiceImpl.removerEndereco(1L, 1L);

        verify(pessoaRepository).findById(anyLong());
        verify(enderecoService).buscar(anyLong());
        verify(pessoaRepository).save(any(Pessoa.class));
    }

    @Test
    void deveLancarExcecaoAoRemoverEnderecoDaPessoaComIdInexistente() {
        when(pessoaRepository.findById(anyLong())).thenReturn(Optional.empty());

        var exception = assertThrows(EntityNotFoundException.class, () -> pessoaServiceImpl.removerEndereco(10L, 1L));
        assertEquals(EntityNotFoundException.class, exception.getClass());
        assertEquals("Pessoa não encontrada com ID: 10", exception.getMessage());
        verify(pessoaRepository).findById(anyLong());
        verify(enderecoService, never()).buscar(anyLong());
        verify(pessoaRepository, never()).save(any(Pessoa.class));
    }

    @Test
    void deveLancarExcecaoAoRemoverEnderecoComIdInexistenteDaPessoa() {
        Pessoa pessoa = getPessoaComEndereco();
        Optional<Pessoa> optionalPessoa = Optional.of(pessoa);

        when(pessoaRepository.findById(anyLong())).thenReturn(optionalPessoa);
        when(enderecoService.buscar(anyLong())).thenThrow(new EntityNotFoundException("Endereço não encontrado com ID: 10"));

        var exception = assertThrows(EntityNotFoundException.class, () -> pessoaServiceImpl.removerEndereco(1L, 10L));
        assertEquals(EntityNotFoundException.class, exception.getClass());
        assertEquals("Endereço não encontrado com ID: 10", exception.getMessage());
        verify(pessoaRepository).findById(anyLong());
        verify(enderecoService).buscar(anyLong());
        verify(pessoaRepository, never()).save(any(Pessoa.class));
    }

    @Test
    void deveLancarExcecaoAoRemoverEnderecoQueNaoPertenceAPessoa() {
        Endereco endereco = getEndereco();
        endereco.setId(10L);

        Pessoa pessoaComEndereco = getPessoaComEndereco();
        Optional<Pessoa> optionalPessoa = Optional.of(pessoaComEndereco);

        when(pessoaRepository.findById(anyLong())).thenReturn(optionalPessoa);
        when(enderecoService.buscar(anyLong())).thenReturn(endereco);

        var exception = assertThrows(UnsupportedOperationException.class, () -> pessoaServiceImpl.removerEndereco(1L, 10L));
        assertEquals(UnsupportedOperationException.class, exception.getClass());
        assertEquals("Pessoa com ID: 1 não possui endereço com ID: 10", exception.getMessage());
        verify(pessoaRepository).findById(anyLong());
        verify(enderecoService).buscar(anyLong());
        verify(pessoaRepository, never()).save(any(Pessoa.class));
    }

    @Test
    void deveAdicionarEnderecoPrincipalAPessoa() {
        Endereco endereco = getEndereco();

        Pessoa pessoaComEndereco = getPessoaComEndereco();
        Pessoa pessoa = getPessoa();
        Optional<Pessoa> optionalPessoa = Optional.of(pessoaComEndereco);

        when(pessoaRepository.findById(anyLong())).thenReturn(optionalPessoa);
        when(enderecoService.buscar(anyLong())).thenReturn(endereco);
        when(pessoaRepository.save(any(Pessoa.class))).thenReturn(pessoa);

        pessoaServiceImpl.adicionarEnderecoPrincipal(1L, 1L);

        verify(pessoaRepository).findById(anyLong());
        verify(enderecoService).buscar(anyLong());
        verify(pessoaRepository).save(any(Pessoa.class));
    }

    @Test
    void deveLancarExcecaoAoAdicionarEnderecoPrincipalAPessoaComIdInexistente() {
        when(pessoaRepository.findById(anyLong())).thenReturn(Optional.empty());

        var exception = assertThrows(EntityNotFoundException.class, () -> pessoaServiceImpl.adicionarEnderecoPrincipal(10L, 1L));
        assertEquals(EntityNotFoundException.class, exception.getClass());
        assertEquals("Pessoa não encontrada com ID: 10", exception.getMessage());
        verify(pessoaRepository).findById(anyLong());
        verify(enderecoService, never()).buscar(anyLong());
        verify(pessoaRepository, never()).save(any(Pessoa.class));
    }

    @Test
    void deveLancarExcecaoAoAdicionarEnderecoPrincipalComIdInexistenteAPessoa() {
        Pessoa pessoa = getPessoaComEndereco();
        Optional<Pessoa> optionalPessoa = Optional.of(pessoa);

        when(pessoaRepository.findById(anyLong())).thenReturn(optionalPessoa);
        when(enderecoService.buscar(anyLong())).thenThrow(new EntityNotFoundException("Endereço não encontrado com ID: 10"));

        var exception = assertThrows(EntityNotFoundException.class, () -> pessoaServiceImpl.adicionarEnderecoPrincipal(1L, 10L));
        assertEquals(EntityNotFoundException.class, exception.getClass());
        assertEquals("Endereço não encontrado com ID: 10", exception.getMessage());
        verify(pessoaRepository).findById(anyLong());
        verify(enderecoService).buscar(anyLong());
        verify(pessoaRepository, never()).save(any(Pessoa.class));
    }

    @Test
    void deveLancarExcecaoAoAdicionarEnderecoPrincipalQueNaoPertenceAPessoa() {
        Endereco endereco = getEndereco();
        endereco.setId(10L);

        Pessoa pessoaComEndereco = getPessoaComEndereco();
        Optional<Pessoa> optionalPessoa = Optional.of(pessoaComEndereco);

        when(pessoaRepository.findById(anyLong())).thenReturn(optionalPessoa);
        when(enderecoService.buscar(anyLong())).thenReturn(endereco);

        var exception = assertThrows(UnsupportedOperationException.class, () -> pessoaServiceImpl.adicionarEnderecoPrincipal(1L, 10L));
        assertEquals(UnsupportedOperationException.class, exception.getClass());
        assertEquals("Pessoa com ID: 1 não possui endereço com ID: 10", exception.getMessage());
        verify(pessoaRepository).findById(anyLong());
        verify(enderecoService).buscar(anyLong());
        verify(pessoaRepository, never()).save(any(Pessoa.class));
    }


    @Test
    void testBuscarEnderecosPessoa() {
        Set<Endereco> enderecos = getEnderecos();
        Pessoa pessoa = getPessoa();
        pessoa.setEnderecos(enderecos);
        Optional<Pessoa> optionalPessoa = Optional.of(pessoa);

        when(pessoaRepository.findById(anyLong())).thenReturn(optionalPessoa);
        Set<Endereco> enderecosResult= pessoaServiceImpl.buscarEnderecosPessoa(1L);
        assertEquals(3, enderecosResult.size());
        verify(pessoaRepository).findById(anyLong());
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
        Endereco endereco = getEndereco();
        Pessoa pessoa = new Pessoa("Bob Jhon", LocalDate.of(1995, 10, 5));
        pessoa.setId(1L);
        pessoa.setEnderecoPrincipal(endereco);
        pessoa.setEnderecos(new HashSet<>(Set.of(endereco)));
        return pessoa;
    }

    private List<Pessoa> getPessoas(){
        Pessoa p1 = new  Pessoa("Bob Jhon", LocalDate.of(1995, 10, 5));
        p1.setId(1L);
        Pessoa p2 = new  Pessoa("Ana Kelly", LocalDate.of(2000, 5, 10));
        p2.setId(2L);
        Pessoa p3 = new  Pessoa("Jacob Fernandes", LocalDate.of(1985, 11, 20));
        p3.setId(3L);
        Pessoa p4 = new  Pessoa("Maria Ana", LocalDate.of(2002, 3, 15));
        p4.setId(4L);

        return Arrays.asList(p1, p2, p3, p4);
    }

    private List<Pessoa> getPessoasComFiltro(String filtro){
        return getPessoas().stream().filter(pessoa -> pessoa.getNome().contains(filtro)).collect(Collectors.toList());
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

    private Endereco getEndereco(){

        return new Endereco(1L, "Rua A", "32644878", "33", "Fogareiro City");
    }
}
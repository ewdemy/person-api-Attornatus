package com.mrcruz.personapi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.mrcruz.personapi.model.Endereco;
import com.mrcruz.personapi.model.Pessoa;
import com.mrcruz.personapi.model.PessoaDTO;
import com.mrcruz.personapi.model.PessoaRequest;
import com.mrcruz.personapi.repository.PessoaRepository;
import com.mrcruz.personapi.service.EnderecoService;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Disabled;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
    void testDeletar3() {
        when(pessoaRepository.findById(anyLong())).thenReturn(Optional.empty());

        var exception = assertThrows(EntityNotFoundException.class, () -> pessoaServiceImpl.deletar(10L));
        assertEquals(EntityNotFoundException.class, exception.getClass());
        assertEquals("Pessoa não encontrada com ID: 10", exception.getMessage());
        verify(pessoaRepository).findById(anyLong());
        verify(pessoaRepository, never()).delete(any(Pessoa.class));
    }

    /**
     * Method under test: {@link PessoaServiceImpl#adicionarEndereco(Long, Long)}
     */
    @Test
    void testAdicionarEndereco() {
        Endereco endereco = new Endereco();
        endereco.setCep("Cep");
        endereco.setCidade("Cidade");
        endereco.setId(123L);
        endereco.setLogradouro("Logradouro");
        endereco.setNumero("Numero");

        Pessoa pessoa = new Pessoa();
        pessoa.setDataNascimento(LocalDate.ofEpochDay(1L));
        pessoa.setEnderecoPrincipal(endereco);
        pessoa.setEnderecos(new HashSet<>());
        pessoa.setId(123L);
        pessoa.setNome("Nome");
        Optional<Pessoa> ofResult = Optional.of(pessoa);

        Endereco endereco1 = new Endereco();
        endereco1.setCep("Cep");
        endereco1.setCidade("Cidade");
        endereco1.setId(123L);
        endereco1.setLogradouro("Logradouro");
        endereco1.setNumero("Numero");

        Pessoa pessoa1 = new Pessoa();
        pessoa1.setDataNascimento(LocalDate.ofEpochDay(1L));
        pessoa1.setEnderecoPrincipal(endereco1);
        pessoa1.setEnderecos(new HashSet<>());
        pessoa1.setId(123L);
        pessoa1.setNome("Nome");
        when(pessoaRepository.save((Pessoa) any())).thenReturn(pessoa1);
        when(pessoaRepository.findById((Long) any())).thenReturn(ofResult);

        Endereco endereco2 = new Endereco();
        endereco2.setCep("Cep");
        endereco2.setCidade("Cidade");
        endereco2.setId(123L);
        endereco2.setLogradouro("Logradouro");
        endereco2.setNumero("Numero");
        when(enderecoService.buscar((Long) any())).thenReturn(endereco2);
        assertSame(pessoa1, pessoaServiceImpl.adicionarEndereco(1L, 1L));
        verify(pessoaRepository).save((Pessoa) any());
        verify(pessoaRepository).findById((Long) any());
        verify(enderecoService).buscar((Long) any());
    }

    /**
     * Method under test: {@link PessoaServiceImpl#adicionarEndereco(Long, Long)}
     */
    @Test
    void testAdicionarEndereco2() {
        Endereco endereco = new Endereco();
        endereco.setCep("Cep");
        endereco.setCidade("Cidade");
        endereco.setId(123L);
        endereco.setLogradouro("Logradouro");
        endereco.setNumero("Numero");

        Pessoa pessoa = new Pessoa();
        pessoa.setDataNascimento(LocalDate.ofEpochDay(1L));
        pessoa.setEnderecoPrincipal(endereco);
        pessoa.setEnderecos(new HashSet<>());
        pessoa.setId(123L);
        pessoa.setNome("Nome");
        Optional<Pessoa> ofResult = Optional.of(pessoa);

        Endereco endereco1 = new Endereco();
        endereco1.setCep("Cep");
        endereco1.setCidade("Cidade");
        endereco1.setId(123L);
        endereco1.setLogradouro("Logradouro");
        endereco1.setNumero("Numero");

        Pessoa pessoa1 = new Pessoa();
        pessoa1.setDataNascimento(LocalDate.ofEpochDay(1L));
        pessoa1.setEnderecoPrincipal(endereco1);
        pessoa1.setEnderecos(new HashSet<>());
        pessoa1.setId(123L);
        pessoa1.setNome("Nome");
        when(pessoaRepository.save((Pessoa) any())).thenReturn(pessoa1);
        when(pessoaRepository.findById((Long) any())).thenReturn(ofResult);
        when(enderecoService.buscar((Long) any())).thenThrow(new UnsupportedOperationException());
        assertThrows(UnsupportedOperationException.class, () -> pessoaServiceImpl.adicionarEndereco(1L, 1L));
        verify(pessoaRepository).findById((Long) any());
        verify(enderecoService).buscar((Long) any());
    }

    /**
     * Method under test: {@link PessoaServiceImpl#adicionarEndereco(Long, Long)}
     */
    @Test
    void testAdicionarEndereco3() {
        Endereco endereco = new Endereco();
        endereco.setCep("Cep");
        endereco.setCidade("Cidade");
        endereco.setId(123L);
        endereco.setLogradouro("Logradouro");
        endereco.setNumero("Numero");

        Pessoa pessoa = new Pessoa();
        pessoa.setDataNascimento(LocalDate.ofEpochDay(1L));
        pessoa.setEnderecoPrincipal(endereco);
        pessoa.setEnderecos(new HashSet<>());
        pessoa.setId(123L);
        pessoa.setNome("Nome");
        when(pessoaRepository.save((Pessoa) any())).thenReturn(pessoa);
        when(pessoaRepository.findById((Long) any())).thenReturn(Optional.empty());

        Endereco endereco1 = new Endereco();
        endereco1.setCep("Cep");
        endereco1.setCidade("Cidade");
        endereco1.setId(123L);
        endereco1.setLogradouro("Logradouro");
        endereco1.setNumero("Numero");

        Endereco endereco2 = new Endereco();
        endereco2.setCep("Cep");
        endereco2.setCidade("Cidade");
        endereco2.setId(123L);
        endereco2.setLogradouro("Logradouro");
        endereco2.setNumero("Numero");
        Pessoa pessoa1 = mock(Pessoa.class);
        when(pessoa1.getEnderecoPrincipal()).thenReturn(endereco2);
        when(pessoa1.getEnderecos()).thenReturn(new HashSet<>());
        doNothing().when(pessoa1).setDataNascimento((LocalDate) any());
        doNothing().when(pessoa1).setEnderecoPrincipal((Endereco) any());
        doNothing().when(pessoa1).setEnderecos((Set<Endereco>) any());
        doNothing().when(pessoa1).setId((Long) any());
        doNothing().when(pessoa1).setNome((String) any());
        pessoa1.setDataNascimento(LocalDate.ofEpochDay(1L));
        pessoa1.setEnderecoPrincipal(endereco1);
        pessoa1.setEnderecos(new HashSet<>());
        pessoa1.setId(123L);
        pessoa1.setNome("Nome");

        Endereco endereco3 = new Endereco();
        endereco3.setCep("Cep");
        endereco3.setCidade("Cidade");
        endereco3.setId(123L);
        endereco3.setLogradouro("Logradouro");
        endereco3.setNumero("Numero");
        when(enderecoService.buscar((Long) any())).thenReturn(endereco3);
        assertThrows(EntityNotFoundException.class, () -> pessoaServiceImpl.adicionarEndereco(1L, 1L));
        verify(pessoaRepository).findById((Long) any());
        verify(pessoa1).setDataNascimento((LocalDate) any());
        verify(pessoa1).setEnderecoPrincipal((Endereco) any());
        verify(pessoa1).setEnderecos((Set<Endereco>) any());
        verify(pessoa1).setId((Long) any());
        verify(pessoa1).setNome((String) any());
    }

    /**
     * Method under test: {@link PessoaServiceImpl#removerEndereco(Long, Long)}
     */
    @Test
    void testRemoverEndereco() {
        Endereco endereco = new Endereco();
        endereco.setCep("Cep");
        endereco.setCidade("Cidade");
        endereco.setId(123L);
        endereco.setLogradouro("Logradouro");
        endereco.setNumero("Numero");

        Pessoa pessoa = new Pessoa();
        pessoa.setDataNascimento(LocalDate.ofEpochDay(1L));
        pessoa.setEnderecoPrincipal(endereco);
        pessoa.setEnderecos(new HashSet<>());
        pessoa.setId(123L);
        pessoa.setNome("Nome");
        Optional<Pessoa> ofResult = Optional.of(pessoa);
        when(pessoaRepository.findById((Long) any())).thenReturn(ofResult);

        Endereco endereco1 = new Endereco();
        endereco1.setCep("Cep");
        endereco1.setCidade("Cidade");
        endereco1.setId(123L);
        endereco1.setLogradouro("Logradouro");
        endereco1.setNumero("Numero");
        when(enderecoService.buscar((Long) any())).thenReturn(endereco1);
        assertThrows(UnsupportedOperationException.class, () -> pessoaServiceImpl.removerEndereco(1L, 1L));
        verify(pessoaRepository).findById((Long) any());
        verify(enderecoService).buscar((Long) any());
    }

    /**
     * Method under test: {@link PessoaServiceImpl#removerEndereco(Long, Long)}
     */
    @Test
    void testRemoverEndereco2() {
        Endereco endereco = new Endereco();
        endereco.setCep("Cep");
        endereco.setCidade("Cidade");
        endereco.setId(123L);
        endereco.setLogradouro("Logradouro");
        endereco.setNumero("Numero");

        Pessoa pessoa = new Pessoa();
        pessoa.setDataNascimento(LocalDate.ofEpochDay(1L));
        pessoa.setEnderecoPrincipal(endereco);
        pessoa.setEnderecos(new HashSet<>());
        pessoa.setId(123L);
        pessoa.setNome("Nome");
        Optional<Pessoa> ofResult = Optional.of(pessoa);
        when(pessoaRepository.findById((Long) any())).thenReturn(ofResult);
        when(enderecoService.buscar((Long) any())).thenThrow(new UnsupportedOperationException());
        assertThrows(UnsupportedOperationException.class, () -> pessoaServiceImpl.removerEndereco(1L, 1L));
        verify(pessoaRepository).findById((Long) any());
        verify(enderecoService).buscar((Long) any());
    }

    /**
     * Method under test: {@link PessoaServiceImpl#removerEndereco(Long, Long)}
     */
    @Test
    void testRemoverEndereco3() {
        Endereco endereco = new Endereco();
        endereco.setCep("Cep");
        endereco.setCidade("Cidade");
        endereco.setId(123L);
        endereco.setLogradouro("Logradouro");
        endereco.setNumero("Numero");

        Endereco endereco1 = new Endereco();
        endereco1.setCep("Cep");
        endereco1.setCidade("Cidade");
        endereco1.setId(123L);
        endereco1.setLogradouro("Logradouro");
        endereco1.setNumero("Numero");

        HashSet<Endereco> enderecoSet = new HashSet<>();
        enderecoSet.add(endereco1);

        Pessoa pessoa = new Pessoa();
        pessoa.setDataNascimento(LocalDate.ofEpochDay(1L));
        pessoa.setEnderecoPrincipal(endereco);
        pessoa.setEnderecos(enderecoSet);
        pessoa.setId(123L);
        pessoa.setNome("Nome");
        Optional<Pessoa> ofResult = Optional.of(pessoa);
        when(pessoaRepository.findById((Long) any())).thenReturn(ofResult);

        Endereco endereco2 = new Endereco();
        endereco2.setCep("Cep");
        endereco2.setCidade("Cidade");
        endereco2.setId(123L);
        endereco2.setLogradouro("Logradouro");
        endereco2.setNumero("Numero");
        when(enderecoService.buscar((Long) any())).thenReturn(endereco2);
        assertThrows(UnsupportedOperationException.class, () -> pessoaServiceImpl.removerEndereco(1L, 1L));
        verify(pessoaRepository).findById((Long) any());
        verify(enderecoService).buscar((Long) any());
    }

    /**
     * Method under test: {@link PessoaServiceImpl#removerEndereco(Long, Long)}
     */
    @Test
    void testRemoverEndereco4() {
        when(pessoaRepository.findById((Long) any())).thenReturn(Optional.empty());

        Endereco endereco = new Endereco();
        endereco.setCep("Cep");
        endereco.setCidade("Cidade");
        endereco.setId(123L);
        endereco.setLogradouro("Logradouro");
        endereco.setNumero("Numero");
        when(enderecoService.buscar((Long) any())).thenReturn(endereco);
        assertThrows(EntityNotFoundException.class, () -> pessoaServiceImpl.removerEndereco(1L, 1L));
        verify(pessoaRepository).findById((Long) any());
    }

    /**
     * Method under test: {@link PessoaServiceImpl#adicionarEnderecoPrincipal(Long, Long)}
     */
    @Test
    void testAdicionarEnderecoPrincipal() {
        Endereco endereco = new Endereco();
        endereco.setCep("Cep");
        endereco.setCidade("Cidade");
        endereco.setId(123L);
        endereco.setLogradouro("Logradouro");
        endereco.setNumero("Numero");

        Pessoa pessoa = new Pessoa();
        pessoa.setDataNascimento(LocalDate.ofEpochDay(1L));
        pessoa.setEnderecoPrincipal(endereco);
        pessoa.setEnderecos(new HashSet<>());
        pessoa.setId(123L);
        pessoa.setNome("Nome");
        Optional<Pessoa> ofResult = Optional.of(pessoa);
        when(pessoaRepository.findById((Long) any())).thenReturn(ofResult);

        Endereco endereco1 = new Endereco();
        endereco1.setCep("Cep");
        endereco1.setCidade("Cidade");
        endereco1.setId(123L);
        endereco1.setLogradouro("Logradouro");
        endereco1.setNumero("Numero");
        when(enderecoService.buscar((Long) any())).thenReturn(endereco1);
        assertThrows(UnsupportedOperationException.class, () -> pessoaServiceImpl.adicionarEnderecoPrincipal(1L, 1L));
        verify(pessoaRepository).findById((Long) any());
        verify(enderecoService).buscar((Long) any());
    }

    /**
     * Method under test: {@link PessoaServiceImpl#adicionarEnderecoPrincipal(Long, Long)}
     */
    @Test
    void testAdicionarEnderecoPrincipal2() {
        Endereco endereco = new Endereco();
        endereco.setCep("Cep");
        endereco.setCidade("Cidade");
        endereco.setId(123L);
        endereco.setLogradouro("Logradouro");
        endereco.setNumero("Numero");

        Pessoa pessoa = new Pessoa();
        pessoa.setDataNascimento(LocalDate.ofEpochDay(1L));
        pessoa.setEnderecoPrincipal(endereco);
        pessoa.setEnderecos(new HashSet<>());
        pessoa.setId(123L);
        pessoa.setNome("Nome");
        Optional<Pessoa> ofResult = Optional.of(pessoa);
        when(pessoaRepository.findById((Long) any())).thenReturn(ofResult);
        when(enderecoService.buscar((Long) any())).thenThrow(new UnsupportedOperationException());
        assertThrows(UnsupportedOperationException.class, () -> pessoaServiceImpl.adicionarEnderecoPrincipal(1L, 1L));
        verify(pessoaRepository).findById((Long) any());
        verify(enderecoService).buscar((Long) any());
    }

    /**
     * Method under test: {@link PessoaServiceImpl#adicionarEnderecoPrincipal(Long, Long)}
     */
    @Test
    void testAdicionarEnderecoPrincipal3() {
        Endereco endereco = new Endereco();
        endereco.setCep("Cep");
        endereco.setCidade("Cidade");
        endereco.setId(123L);
        endereco.setLogradouro("Logradouro");
        endereco.setNumero("Numero");

        Endereco endereco1 = new Endereco();
        endereco1.setCep("Cep");
        endereco1.setCidade("Cidade");
        endereco1.setId(123L);
        endereco1.setLogradouro("Logradouro");
        endereco1.setNumero("Numero");

        HashSet<Endereco> enderecoSet = new HashSet<>();
        enderecoSet.add(endereco1);

        Pessoa pessoa = new Pessoa();
        pessoa.setDataNascimento(LocalDate.ofEpochDay(1L));
        pessoa.setEnderecoPrincipal(endereco);
        pessoa.setEnderecos(enderecoSet);
        pessoa.setId(123L);
        pessoa.setNome("Nome");
        Optional<Pessoa> ofResult = Optional.of(pessoa);
        when(pessoaRepository.findById((Long) any())).thenReturn(ofResult);

        Endereco endereco2 = new Endereco();
        endereco2.setCep("Cep");
        endereco2.setCidade("Cidade");
        endereco2.setId(123L);
        endereco2.setLogradouro("Logradouro");
        endereco2.setNumero("Numero");
        when(enderecoService.buscar((Long) any())).thenReturn(endereco2);
        assertThrows(UnsupportedOperationException.class, () -> pessoaServiceImpl.adicionarEnderecoPrincipal(1L, 1L));
        verify(pessoaRepository).findById((Long) any());
        verify(enderecoService).buscar((Long) any());
    }

    /**
     * Method under test: {@link PessoaServiceImpl#adicionarEnderecoPrincipal(Long, Long)}
     */
    @Test
    void testAdicionarEnderecoPrincipal4() {
        when(pessoaRepository.findById((Long) any())).thenReturn(Optional.empty());

        Endereco endereco = new Endereco();
        endereco.setCep("Cep");
        endereco.setCidade("Cidade");
        endereco.setId(123L);
        endereco.setLogradouro("Logradouro");
        endereco.setNumero("Numero");
        when(enderecoService.buscar((Long) any())).thenReturn(endereco);
        assertThrows(EntityNotFoundException.class, () -> pessoaServiceImpl.adicionarEnderecoPrincipal(1L, 1L));
        verify(pessoaRepository).findById((Long) any());
    }

    /**
     * Method under test: {@link PessoaServiceImpl#buscarEnderecosPessoa(Long)}
     */
    @Test
    void testBuscarEnderecosPessoa() {
        Endereco endereco = new Endereco();
        endereco.setCep("Cep");
        endereco.setCidade("Cidade");
        endereco.setId(123L);
        endereco.setLogradouro("Logradouro");
        endereco.setNumero("Numero");

        Pessoa pessoa = new Pessoa();
        pessoa.setDataNascimento(LocalDate.ofEpochDay(1L));
        pessoa.setEnderecoPrincipal(endereco);
        HashSet<Endereco> enderecoSet = new HashSet<>();
        pessoa.setEnderecos(enderecoSet);
        pessoa.setId(123L);
        pessoa.setNome("Nome");
        Optional<Pessoa> ofResult = Optional.of(pessoa);
        when(pessoaRepository.findById((Long) any())).thenReturn(ofResult);
        Set<Endereco> actualBuscarEnderecosPessoaResult = pessoaServiceImpl.buscarEnderecosPessoa(1L);
        assertSame(enderecoSet, actualBuscarEnderecosPessoaResult);
        assertTrue(actualBuscarEnderecosPessoaResult.isEmpty());
        verify(pessoaRepository).findById((Long) any());
    }

    /**
     * Method under test: {@link PessoaServiceImpl#buscarEnderecosPessoa(Long)}
     */
    @Test
    void testBuscarEnderecosPessoa2() {
        when(pessoaRepository.findById((Long) any())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> pessoaServiceImpl.buscarEnderecosPessoa(1L));
        verify(pessoaRepository).findById((Long) any());
    }

    /**
     * Method under test: {@link PessoaServiceImpl#buscarEnderecosPessoa(Long)}
     */
    @Test
    void testBuscarEnderecosPessoa3() {
        when(pessoaRepository.findById((Long) any())).thenThrow(new UnsupportedOperationException());
        assertThrows(UnsupportedOperationException.class, () -> pessoaServiceImpl.buscarEnderecosPessoa(1L));
        verify(pessoaRepository).findById((Long) any());
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
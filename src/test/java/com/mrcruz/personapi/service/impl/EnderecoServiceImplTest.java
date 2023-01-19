package com.mrcruz.personapi.service.impl;

import com.mrcruz.personapi.model.Endereco;
import com.mrcruz.personapi.repository.EnderecoRepository;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {EnderecoServiceImpl.class})
@ExtendWith(SpringExtension.class)
class EnderecoServiceImplTest {
    @MockBean
    private EnderecoRepository enderecoRepository;

    @Autowired
    private EnderecoServiceImpl enderecoServiceImpl;

    @Test
    void deveSalvarEndereco() {
        Endereco endereco = getEndereco();

        when(enderecoRepository.save(any(Endereco.class))).thenReturn(endereco);

        enderecoServiceImpl.salvar(getEnderecoRequest());
        verify(enderecoRepository).save(any(Endereco.class));
    }

    @Test
    void deveBuscarEndereco() {
        Endereco endereco = getEndereco();
        Optional<Endereco> enderecoOptional = Optional.of(endereco);
        when(enderecoRepository.findById( anyLong())).thenReturn(enderecoOptional);
        assertSame(endereco, enderecoServiceImpl.buscar(1L));
        verify(enderecoRepository).findById(anyLong());
    }

    @Test
    void deveLancarExcecaoAoBuscarEnderecoComIdInexistente() {
        when(enderecoRepository.findById(anyLong())).thenReturn(Optional.empty());
        var exception = assertThrows(EntityNotFoundException.class, () -> enderecoServiceImpl.buscar(10L));
        assertEquals(EntityNotFoundException.class, exception.getClass());
        assertEquals("Endereço não encontrado com ID: 10", exception.getMessage());
        verify(enderecoRepository).findById(anyLong());
    }

    @Test
    void DeveListarEnderecos() {
        Page<Endereco> pageResponse = new PageImpl(getEnderecos());

        when(enderecoRepository.findAll(Pageable.ofSize(20))).thenReturn(pageResponse);
        Page<Endereco> result = enderecoServiceImpl.listar(Pageable.ofSize(20));
        assertSame(pageResponse, result);
        assertFalse(result.toList().isEmpty());
        assertEquals(3, result.getContent().size());
        verify(enderecoRepository).findAll(any(Pageable.class));
    }
    @Test
    void testListar2() {
        when(enderecoRepository.findAll((Pageable) any())).thenThrow(new EntityNotFoundException("An error occurred"));
        assertThrows(EntityNotFoundException.class, () -> enderecoServiceImpl.listar(null));
        verify(enderecoRepository).findAll((Pageable) any());
    }

    /**
     * Method under test: {@link EnderecoServiceImpl#atualizar(Long, Endereco)}
     */
    @Test
    void testAtualizar() {
        Endereco endereco = new Endereco();
        endereco.setCep("Cep");
        endereco.setCidade("Cidade");
        endereco.setId(123L);
        endereco.setLogradouro("Logradouro");
        endereco.setNumero("Numero");
        Optional<Endereco> ofResult = Optional.of(endereco);

        Endereco endereco1 = new Endereco();
        endereco1.setCep("Cep");
        endereco1.setCidade("Cidade");
        endereco1.setId(123L);
        endereco1.setLogradouro("Logradouro");
        endereco1.setNumero("Numero");
        when(enderecoRepository.save((Endereco) any())).thenReturn(endereco1);
        when(enderecoRepository.findById((Long) any())).thenReturn(ofResult);

        Endereco endereco2 = new Endereco();
        endereco2.setCep("Cep");
        endereco2.setCidade("Cidade");
        endereco2.setId(123L);
        endereco2.setLogradouro("Logradouro");
        endereco2.setNumero("Numero");
        assertSame(endereco1, enderecoServiceImpl.atualizar(123L, endereco2));
        verify(enderecoRepository).save((Endereco) any());
        verify(enderecoRepository).findById((Long) any());
        assertEquals(123L, endereco2.getId().longValue());
    }

    /**
     * Method under test: {@link EnderecoServiceImpl#atualizar(Long, Endereco)}
     */
    @Test
    void testAtualizar2() {
        Endereco endereco = new Endereco();
        endereco.setCep("Cep");
        endereco.setCidade("Cidade");
        endereco.setId(123L);
        endereco.setLogradouro("Logradouro");
        endereco.setNumero("Numero");
        Optional<Endereco> ofResult = Optional.of(endereco);
        when(enderecoRepository.save((Endereco) any())).thenThrow(new EntityNotFoundException("An error occurred"));
        when(enderecoRepository.findById((Long) any())).thenReturn(ofResult);

        Endereco endereco1 = new Endereco();
        endereco1.setCep("Cep");
        endereco1.setCidade("Cidade");
        endereco1.setId(123L);
        endereco1.setLogradouro("Logradouro");
        endereco1.setNumero("Numero");
        assertThrows(EntityNotFoundException.class, () -> enderecoServiceImpl.atualizar(123L, endereco1));
        verify(enderecoRepository).save((Endereco) any());
        verify(enderecoRepository).findById((Long) any());
    }

    /**
     * Method under test: {@link EnderecoServiceImpl#atualizar(Long, Endereco)}
     */
    @Test
    void testAtualizar3() {
        Endereco endereco = new Endereco();
        endereco.setCep("Cep");
        endereco.setCidade("Cidade");
        endereco.setId(123L);
        endereco.setLogradouro("Logradouro");
        endereco.setNumero("Numero");
        when(enderecoRepository.save((Endereco) any())).thenReturn(endereco);
        when(enderecoRepository.findById((Long) any())).thenReturn(Optional.empty());

        Endereco endereco1 = new Endereco();
        endereco1.setCep("Cep");
        endereco1.setCidade("Cidade");
        endereco1.setId(123L);
        endereco1.setLogradouro("Logradouro");
        endereco1.setNumero("Numero");
        assertThrows(EntityNotFoundException.class, () -> enderecoServiceImpl.atualizar(123L, endereco1));
        verify(enderecoRepository).findById((Long) any());
    }

    /**
     * Method under test: {@link EnderecoServiceImpl#deletar(Long)}
     */
    @Test
    void testDeletar() {
        Endereco endereco = new Endereco();
        endereco.setCep("Cep");
        endereco.setCidade("Cidade");
        endereco.setId(123L);
        endereco.setLogradouro("Logradouro");
        endereco.setNumero("Numero");
        Optional<Endereco> ofResult = Optional.of(endereco);
        doNothing().when(enderecoRepository).delete((Endereco) any());
        when(enderecoRepository.findById((Long) any())).thenReturn(ofResult);
        enderecoServiceImpl.deletar(123L);
        verify(enderecoRepository).findById((Long) any());
        verify(enderecoRepository).delete((Endereco) any());
    }

    /**
     * Method under test: {@link EnderecoServiceImpl#deletar(Long)}
     */
    @Test
    void testDeletar2() {
        Endereco endereco = new Endereco();
        endereco.setCep("Cep");
        endereco.setCidade("Cidade");
        endereco.setId(123L);
        endereco.setLogradouro("Logradouro");
        endereco.setNumero("Numero");
        Optional<Endereco> ofResult = Optional.of(endereco);
        doThrow(new EntityNotFoundException("An error occurred")).when(enderecoRepository).delete((Endereco) any());
        when(enderecoRepository.findById((Long) any())).thenReturn(ofResult);
        assertThrows(EntityNotFoundException.class, () -> enderecoServiceImpl.deletar(123L));
        verify(enderecoRepository).findById((Long) any());
        verify(enderecoRepository).delete((Endereco) any());
    }

    /**
     * Method under test: {@link EnderecoServiceImpl#deletar(Long)}
     */
    @Test
    void testDeletar3() {
        doNothing().when(enderecoRepository).delete((Endereco) any());
        when(enderecoRepository.findById((Long) any())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> enderecoServiceImpl.deletar(123L));
        verify(enderecoRepository).findById((Long) any());
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
        Endereco enderecoRequest = new Endereco();
        enderecoRequest.setLogradouro("Rua A");
        enderecoRequest.setCep("32644878");
        enderecoRequest.setNumero("33");
        enderecoRequest.setCidade("Fogareiro City");

        return enderecoRequest;
    }
}


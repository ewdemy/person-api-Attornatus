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
    void DeveTrazerArryVazioQuandoListarEnderecos() {
        Page<Endereco> pageResponse = new PageImpl(new ArrayList<>());

        when(enderecoRepository.findAll(any(Pageable.class))).thenReturn(pageResponse);
        Page<Endereco> result = enderecoServiceImpl.listar(Pageable.ofSize(20));
        assertSame(pageResponse, result);
        assertTrue(result.toList().isEmpty());
        verify(enderecoRepository).findAll(any(Pageable.class));
    }

    @Test
    void deveAtualizarEndereco() {
        Endereco endereco = getEndereco();

        Optional<Endereco> enderecoOptional = Optional.of(endereco);

        when(enderecoRepository.findById(anyLong())).thenReturn(enderecoOptional);
        when(enderecoRepository.save(any(Endereco.class))).thenReturn(endereco);

        assertSame(endereco, enderecoServiceImpl.atualizar(1L, getEnderecoRequest()));
        verify(enderecoRepository).findById(anyLong());
        verify(enderecoRepository).save(any(Endereco.class));
    }

    @Test
    void develancarExcecaoAtualizarEnderecoComIdInexistente() {
        Endereco endereco = getEnderecoRequest();
        when(enderecoRepository.findById(anyLong())).thenReturn(Optional.empty());
        var exception = assertThrows(EntityNotFoundException.class, () -> enderecoServiceImpl.atualizar(10L, endereco));
        assertEquals(EntityNotFoundException.class, exception.getClass());
        assertEquals("Endereço não encontrado com ID: 10", exception.getMessage());
        verify(enderecoRepository).findById(anyLong());
        verify(enderecoRepository, never()).save(any(Endereco.class));
    }

    @Test
    void deveDeletarEndereco() {
        Endereco endereco = getEndereco();
        Optional<Endereco> result = Optional.of(endereco);

        when(enderecoRepository.findById(anyLong())).thenReturn(result);
        doNothing().when(enderecoRepository).delete(any(Endereco.class));

        enderecoServiceImpl.deletar(1L);
        verify(enderecoRepository).findById(anyLong());
        verify(enderecoRepository).delete(any(Endereco.class));
    }

    @Test
    void deveLancarExcecaoAoDeletarEnderecoComIdInexistente() {
        when(enderecoRepository.findById(anyLong())).thenReturn(Optional.empty());
        var exception = assertThrows(EntityNotFoundException.class, () -> enderecoServiceImpl.deletar(10L));
        assertEquals(EntityNotFoundException.class, exception.getClass());
        assertEquals("Endereço não encontrado com ID: 10", exception.getMessage());
        verify(enderecoRepository).findById(anyLong());
        verify(enderecoRepository, never()).delete(any(Endereco.class));
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


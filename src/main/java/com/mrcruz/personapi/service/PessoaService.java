package com.mrcruz.personapi.service;

import com.mrcruz.personapi.model.Endereco;
import com.mrcruz.personapi.model.Pessoa;
import com.mrcruz.personapi.model.PessoaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface PessoaService {

    Pessoa salvar(Pessoa pessoa);

    Pessoa buscar(Long id);

    Page<PessoaDTO> listar (String nome, Pageable pageable);

    Pessoa atualizar(Long id, Pessoa pessoa);

    void deletar(Long id);

    Pessoa adicionarEndereco(Long idPessoa, Long idEndereco);
    Pessoa adicionarEnderecoPrincipal(Long idPessoa, Long idEndereco);

    Set<Endereco> buscarEnderecosPessoa(Long idPessoa);
}

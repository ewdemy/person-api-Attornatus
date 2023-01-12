package com.mrcruz.personapi.service;

import com.mrcruz.personapi.model.Endereco;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EnderecoService {

    Endereco salvar(Endereco endereco);
    Endereco buscar(Long id);
    Page<Endereco> listar(Pageable pageable);
    Endereco atualizar(Long id, Endereco endereco);
    void deletar(Long id);
}

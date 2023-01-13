package com.mrcruz.personapi.service.impl;

import com.mrcruz.personapi.model.Endereco;
import com.mrcruz.personapi.repository.EnderecoRepository;
import com.mrcruz.personapi.service.EnderecoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EnderecoServiceImpl implements EnderecoService {

    private EnderecoRepository enderecoRepository;

    @Override
    public Endereco salvar(Endereco endereco) {
        return enderecoRepository.save(endereco);
    }

    @Override
    public Endereco buscar(Long id) {
        return enderecoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Endereço não encontrado com ID: " + id));
    }

    @Override
    public Page<Endereco> listar(Pageable pageable) {
        return enderecoRepository.findAll(pageable);
    }

    @Override
    public Endereco atualizar(Long id, Endereco endereco) {
        var enderecoBanco = buscar(id);
        endereco. setId(enderecoBanco.getId());
        return enderecoRepository.save(endereco);
    }

    @Override
    public void deletar(Long id) {
        var enderecoBanco = buscar(id);
        enderecoRepository.delete(enderecoBanco);
    }
}

package com.mrcruz.personapi.service.impl;

import com.mrcruz.personapi.model.Endereco;
import com.mrcruz.personapi.model.Pessoa;
import com.mrcruz.personapi.repository.PessoaRepository;
import com.mrcruz.personapi.service.EnderecoService;
import com.mrcruz.personapi.service.PessoaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class PessoaServiceImpl implements PessoaService {

    private PessoaRepository pessoaRepository;
    private EnderecoService enderecoService;
    @Override
    public Pessoa salvar(Pessoa pessoa) {
        return pessoaRepository.save(pessoa);
    }

    @Override
    public Pessoa buscar(Long id) {
        return pessoaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada com ID: " + id));
    }

    @Override
    public Page<Pessoa> listar(String nome, Pageable pageable) {
        if(nome != null && !nome.isBlank())
            return pessoaRepository.findByNomeContainingIgnoreCase(nome, pageable);

        return pessoaRepository.findAll(pageable);
    }

    @Override
    public Pessoa atualizar(Long id, Pessoa pessoa) {
        var pessoaBanco = buscar(id);
        pessoa.setId(pessoaBanco.getId());
        return pessoaRepository.save(pessoa);
    }

    @Override
    public void deletar(Long id) {
        var pessoaBanco = buscar(id);
        pessoaRepository.delete(pessoaBanco);
    }

    @Override
    public Pessoa adicionarEndereco(Long idPessoa, Long idEndereco) {
        Pessoa pessoa = buscar(idEndereco);
        Endereco endereco = enderecoService.buscar(idEndereco);
        pessoa.getEnderecos().add(endereco);
        return pessoaRepository.save(pessoa);
    }
}

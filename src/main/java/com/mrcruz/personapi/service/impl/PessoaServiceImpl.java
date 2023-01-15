package com.mrcruz.personapi.service.impl;

import com.mrcruz.personapi.model.Endereco;
import com.mrcruz.personapi.model.Pessoa;
import com.mrcruz.personapi.model.PessoaDTO;
import com.mrcruz.personapi.repository.PessoaRepository;
import com.mrcruz.personapi.service.EnderecoService;
import com.mrcruz.personapi.service.PessoaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;

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

    @Transactional
    @Override
    public Page<PessoaDTO> listar(String nome, Pageable pageable) {
        if(nome != null && !nome.isBlank()) {
            return pessoaRepository
                    .findByNomeContainingIgnoreCase(nome, pageable).map(this::toPessoaDTO);
        }

        return pessoaRepository.findAll(pageable).map(this::toPessoaDTO);
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
        Pessoa pessoa = buscar(idPessoa);
        Endereco endereco = enderecoService.buscar(idEndereco);
        pessoa.getEnderecos().add(endereco);

        if(pessoa.getEnderecoPrincipal() == null)
            pessoa.setEnderecoPrincipal(endereco);

        return pessoaRepository.save(pessoa);
    }

    @Override
    public Pessoa removerEndereco(Long idPessoa, Long idEndereco) {
        Pessoa pessoa = buscar(idPessoa);
        Endereco endereco = enderecoService.buscar(idEndereco);
        existeEnderecoPessoa(pessoa, endereco);

        pessoa.getEnderecos().remove(endereco);
        return pessoaRepository.save(pessoa);
    }

    @Override
    public Pessoa adicionarEnderecoPrincipal(Long idPessoa, Long idEndereco) {
        Pessoa pessoa = buscar(idPessoa);
        Endereco endereco = enderecoService.buscar(idEndereco);
        existeEnderecoPessoa(pessoa, endereco);

        pessoa.setEnderecoPrincipal(endereco);

        return pessoaRepository.save(pessoa);
    }

    @Override
    public Set<Endereco> buscarEnderecosPessoa(Long idPessoa) {
        Pessoa pessoa = buscar(idPessoa);
        return pessoa.getEnderecos();
    }

    private PessoaDTO toPessoaDTO(Pessoa pessoa){
        return new PessoaDTO(
                pessoa.getId(),
                pessoa.getNome(),
                pessoa.getDataNascimento(),
                pessoa.getEnderecoPrincipal());
    }

    private void existeEnderecoPessoa(Pessoa pessoa, Endereco endereco){
        if(pessoa.getEnderecos().isEmpty() || !pessoa.getEnderecos().contains(endereco))
            throw new UnsupportedOperationException("Pessoa com ID: " + pessoa.getId() + " não possui endereço com ID: " + endereco.getId());
    }
}

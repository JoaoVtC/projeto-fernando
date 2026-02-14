package com.avanade.livraria.repository;

import com.avanade.livraria.DTO.EmprestimoDTO;
import com.avanade.livraria.domain.Emprestimo;

import java.util.List;
import java.util.Optional;

public interface RepositorioEmprestimo {
    Emprestimo save(Emprestimo emprestimo);
    Optional<Emprestimo> findById(Long id);
    void saveDataDevolucao(Long EmprestimoId, java.time.LocalDateTime dataDevolvida);
    List<Emprestimo> findEmprestimosAtivos();
    List<Emprestimo> findEmprestimosPorUsuario(Long usuarioId);
}

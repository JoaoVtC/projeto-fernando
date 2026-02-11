package com.avanade.livraria.repository;

import com.avanade.livraria.domain.Multa;

import java.util.List;
import java.util.Optional;

public interface RepositorioMulta {
    Multa save(Multa multa);
    Multa findById(Long id);
    Multa findByEmprestimoId(Long emprestimoId);
    List<Multa> findMultasNaoPagas();
}

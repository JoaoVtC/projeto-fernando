package com.avanade.livraria.repository;

import com.avanade.livraria.domain.Livro;
import java.util.List;
public interface RepositorioLivro {
    Livro save(Livro livro);
    Livro findById(Long id);
    List<Livro> findAll();
    void update(Livro livro);
}

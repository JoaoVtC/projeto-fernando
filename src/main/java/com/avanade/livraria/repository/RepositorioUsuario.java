package com.avanade.livraria.repository;

import com.avanade.livraria.domain.Usuario;

public interface RepositorioUsuario {
    Usuario save(Usuario usuario);
    Usuario findById(Long id);
}

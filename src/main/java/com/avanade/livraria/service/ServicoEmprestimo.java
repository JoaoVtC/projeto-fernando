package com.avanade.livraria.service;

import com.avanade.livraria.domain.Livro;
import com.avanade.livraria.DTO.EmprestimoDTO;
import com.avanade.livraria.DTO.UsuarioMultaDTO;
import com.avanade.livraria.domain.Emprestimo;
import com.avanade.livraria.domain.Usuario;
import com.avanade.livraria.domain.TipoUsuario;
import com.avanade.livraria.repository.RepositorioLivro;
import com.avanade.livraria.repository.RepositorioEmprestimo;
import com.avanade.livraria.repository.RepositorioUsuario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ServicoEmprestimo {
    private final RepositorioLivro livroRepo;
    private final RepositorioUsuario usuarioRepo;
    private final RepositorioEmprestimo emprestimoRepo;
    private ServicoMulta servicoMulta;


    public ServicoEmprestimo(RepositorioLivro livroRepo, RepositorioUsuario usuarioRepo, RepositorioEmprestimo emprestimoRepo) {
        this.livroRepo = livroRepo;
        this.usuarioRepo = usuarioRepo;
        this.emprestimoRepo = emprestimoRepo;
    }

     public ServicoEmprestimo(RepositorioLivro livroRepo, RepositorioUsuario usuarioRepo, RepositorioEmprestimo emprestimoRepo, ServicoMulta servicoMulta) {
        this.livroRepo = livroRepo;
        this.usuarioRepo = usuarioRepo;
        this.emprestimoRepo = emprestimoRepo;
        this.servicoMulta = servicoMulta;
    }

    public Emprestimo devolverLivro(Long EmprestimoId, LocalDateTime dataDevolvida) {
            Optional<Emprestimo> emprestimoOpt = emprestimoRepo.findById(EmprestimoId);
            Emprestimo emprestimo = emprestimoOpt.get();
            emprestimoRepo.saveDataDevolucao(EmprestimoId, dataDevolvida);
            emprestimoOpt = emprestimoRepo.findById(EmprestimoId);
            emprestimo = emprestimoOpt.get();
            servicoMulta.calcularMulta(emprestimo.getId(), dataDevolvida, emprestimo.getDueDate());
            

       return emprestimo;
    }

    public Emprestimo criarEmprestimo(Long usuarioId, Long livroId) {
        Usuario ou = usuarioRepo.findById(usuarioId);
        Livro ob = livroRepo.findById(livroId);
        if (ou == null) throw new IllegalArgumentException("Usuario nao encontrado");
        if (ob == null) throw new IllegalArgumentException("Livro nao encontrado");
        Usuario usuario = ou;
        Livro livro = ob;
        if (livro.getAvailableCopies() <= 0) throw new IllegalStateException("Sem copias disponiveis");

        int days = usuario.getUserType() == TipoUsuario.PROFESSOR ? 30 : 14;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime due = now.plusDays(days);

        Emprestimo emprestimo = new Emprestimo(livro.getId(), usuario.getId(), now, due);
        Emprestimo saved = emprestimoRepo.save(emprestimo);

        livro.setAvailableCopies(livro.getAvailableCopies() - 1);
        livroRepo.update(livro);

        return saved;
    }

    public List<EmprestimoDTO> listarEmprestimosAtivos(){
        List<Emprestimo> emprestimosAtivos = emprestimoRepo.findEmprestimosAtivos();
        return emprestimosAtivos.stream().map(emprestimo ->{
           Usuario usuario = usuarioRepo.findById(emprestimo.getUserId());
           Livro livro = livroRepo.findById(emprestimo.getBookId());
           

           EmprestimoDTO emprestimoDTO = new EmprestimoDTO(livro, usuario, emprestimo.getId(), emprestimo.getLoanDate(), emprestimo.getDueDate(), null);

           return emprestimoDTO;
        }).toList();
    }
}

package com.avanade.livraria.service;

import com.avanade.livraria.domain.Livro;
import com.avanade.livraria.domain.Multa;
import com.avanade.livraria.domain.StatusEmprestimo;
import com.avanade.livraria.DTO.EmprestimoDTO;
import com.avanade.livraria.DTO.RelatorioUsuarioDTO;
import com.avanade.livraria.DTO.UsuarioMultaDTO;
import com.avanade.livraria.domain.Emprestimo;
import com.avanade.livraria.domain.Usuario;
import com.avanade.livraria.exceptions.InvalidarEmprestimoException;
import com.avanade.livraria.domain.TipoUsuario;
import com.avanade.livraria.repository.RepositorioLivro;
import com.avanade.livraria.repository.RepositorioMulta;
import com.avanade.livraria.repository.RepositorioEmprestimo;
import com.avanade.livraria.repository.RepositorioUsuario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ServicoEmprestimo {
    private final RepositorioLivro livroRepo;
    private final RepositorioUsuario usuarioRepo;
    private final RepositorioEmprestimo emprestimoRepo;
    private RepositorioMulta multaRepo;
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

    public ServicoEmprestimo(RepositorioLivro livroRepo, RepositorioUsuario usuarioRepo, RepositorioEmprestimo emprestimoRepo, ServicoMulta servicoMulta, RepositorioMulta multaRepo) {
        this.livroRepo = livroRepo;
        this.usuarioRepo = usuarioRepo;
        this.emprestimoRepo = emprestimoRepo;
        this.multaRepo = multaRepo;
        this.servicoMulta = servicoMulta;
    }

    public Emprestimo devolverLivro(Long EmprestimoId, LocalDateTime dataDevolvida) {
            Optional<Emprestimo> emprestimoOpt = emprestimoRepo.findById(EmprestimoId);
            Emprestimo emprestimo = emprestimoOpt.get();
            emprestimoRepo.saveDataDevolucao(EmprestimoId, dataDevolvida);
            emprestimoOpt = emprestimoRepo.findById(EmprestimoId);
            Livro livro = livroRepo.findById(emprestimo.getBookId());
            livro.setAvailableCopies(livro.getAvailableCopies() + 1);
            livroRepo.update(livro);
            emprestimo = emprestimoOpt.get();
            servicoMulta.calcularMulta(emprestimo.getId(), dataDevolvida, emprestimo.getDueDate());
            
       return emprestimo;
    }

    private StatusEmprestimo validarEmprestimo(List<Emprestimo> emprestimosUsuario, TipoUsuario tipoUsuario, Integer totalCopias ){
        Long emprestimosAtivos = emprestimosUsuario.stream()
            .filter(e -> e.getReturnDate() == null)
            .count();
        
        Boolean multaAtiva = emprestimosUsuario.stream()
                            .anyMatch(e -> {
                            Multa multa = multaRepo.findByEmprestimoId(e.getId());
                            return multa != null && multa.getDataPagamento() == null;
                            });
        
        if((emprestimosAtivos >= 3 && tipoUsuario == TipoUsuario.ESTUDANTE) || (emprestimosAtivos >= 5 && tipoUsuario == TipoUsuario.PROFESSOR)) {
            return StatusEmprestimo.LIMITE_EMPRESTIMOS_ATINGIDO;
        }

        if(totalCopias == 0) return StatusEmprestimo.LIVRO_INDISPONIVEL;

        if(multaAtiva) return StatusEmprestimo.MULTA_PENDENTE;
        
        return StatusEmprestimo.APROVADO;
    }

    public Emprestimo criarEmprestimo(Long usuarioId, Long livroId) throws InvalidarEmprestimoException{
        Usuario ou = usuarioRepo.findById(usuarioId);
        Livro ob = livroRepo.findById(livroId);
        
        if (ou == null) throw new IllegalArgumentException("Usuario nao encontrado");
        if (ob == null) throw new IllegalArgumentException("Livro nao encontrado");
        
        StatusEmprestimo validate = validarEmprestimo(emprestimoRepo.findEmprestimosPorUsuario(usuarioId), ou.getUserType(), ob.getAvailableCopies());

        if(validate == StatusEmprestimo.LIMITE_EMPRESTIMOS_ATINGIDO) throw new InvalidarEmprestimoException(ou.getName(), validate);

        if(validate == StatusEmprestimo.LIVRO_INDISPONIVEL) throw new InvalidarEmprestimoException(ou.getName(), validate);

        if(validate == StatusEmprestimo.MULTA_PENDENTE) throw new InvalidarEmprestimoException(ou.getName(), validate);
        
        Usuario usuario = ou;
        Livro livro = ob;
       

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

        public RelatorioUsuarioDTO gerarRelatorioUsuario(Long usuarioId){
            List<Emprestimo> emprestimos = emprestimoRepo.findEmprestimosPorUsuario(usuarioId);
            String usuario = usuarioRepo.findById(emprestimos.get(0).getUserId()).getName();
            List<Livro> livros = emprestimos.stream()
                                .map(emprestimo -> {
                                    Livro livro = livroRepo.findById(emprestimo.getBookId());
                                    
                                    return livro;
                                }).toList();

            List<Multa> multas = emprestimos.stream().map(emprestimo -> {
                Multa multa = multaRepo.findByEmprestimoId(emprestimo.getId());

                return multa;
            }).toList();

            RelatorioUsuarioDTO relatorioUsuario = new RelatorioUsuarioDTO(usuario, emprestimos, livros, multas);

            return relatorioUsuario;
        }

    
}

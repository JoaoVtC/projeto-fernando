package com.avanade.livraria.DTO;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.avanade.livraria.domain.Livro;
import com.avanade.livraria.domain.Usuario;

public class EmprestimoDTO {
    private Livro livro;
    private Usuario usuario;
    private Long emprestimoId;
    private LocalDateTime loanDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;

    public EmprestimoDTO(Livro livro, Usuario usuario, Long emprestimoId, LocalDateTime loanDate,LocalDateTime dueDate, LocalDateTime returnDate){
        this.livro = livro;
        this.usuario = usuario;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }
    
    
}

package com.avanade.livraria.domain;

import java.time.LocalDateTime;

public class Emprestimo {
    private Long id;
    private Long bookId;
    private Long userId;
    private LocalDateTime loanDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;

    public Emprestimo(Long id, Long bookId, Long userId, LocalDateTime loanDate, LocalDateTime dueDate, LocalDateTime returnDate) {
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    public Emprestimo(Long id, Long bookId, Long userId, LocalDateTime loanDate, LocalDateTime dueDate) {
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
    }

    public Emprestimo(Long bookId, Long userId, LocalDateTime loanDate, LocalDateTime dueDate) {
        this(null, bookId, userId, loanDate, dueDate, null);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBookId() { return bookId; }
    public Long getUserId() { return userId; }
    public LocalDateTime getLoanDate() { return loanDate; }
    public LocalDateTime getDueDate() { return dueDate; }
    public LocalDateTime getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDateTime returnDate) { this.returnDate = returnDate; }
}

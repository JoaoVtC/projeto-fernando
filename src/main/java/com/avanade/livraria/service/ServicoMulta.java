package com.avanade.livraria.service;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.avanade.livraria.DTO.EmprestimoDTO;
import com.avanade.livraria.DTO.UsuarioMultaDTO;
import com.avanade.livraria.domain.Emprestimo;
import com.avanade.livraria.domain.Livro;
import com.avanade.livraria.domain.Multa;
import com.avanade.livraria.domain.Usuario;
import com.avanade.livraria.repository.RepositorioEmprestimo;
import com.avanade.livraria.repository.RepositorioMulta;
import com.avanade.livraria.repository.RepositorioUsuario;

public class ServicoMulta {

    public RepositorioMulta repositorioMulta;
    public RepositorioUsuario repositorioUsuario;
    public RepositorioEmprestimo repositorioEmprestimo;


    public ServicoMulta(RepositorioMulta repositorioMulta) {
        this.repositorioMulta = repositorioMulta;
    }

    public ServicoMulta(RepositorioMulta repositorioMulta, RepositorioUsuario repositorioUsuario, RepositorioEmprestimo repositorioEmprestimo) {
        this.repositorioMulta = repositorioMulta;
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioEmprestimo = repositorioEmprestimo;
    }
    
    
    public BigDecimal calcularMulta(Long emprestimoId, LocalDateTime dataDevolvida, LocalDateTime dataVencimento) {

        Period atraso = Period.between(dataVencimento.toLocalDate(), dataDevolvida.toLocalDate());
        Integer diasAtraso = atraso.getDays();
        if(diasAtraso > 0){
            BigDecimal valor = BigDecimal.valueOf(diasAtraso * 1);
            Multa multa = new Multa(emprestimoId, valor, null);
            repositorioMulta.save(multa);
            return multa.getValor();
        }

        return BigDecimal.valueOf(0.0);
       
    }

    public List<UsuarioMultaDTO> listarUsuariosComMultas(){
        List<Multa> multas = repositorioMulta.findMultasNaoPagas();

        Map<Usuario, BigDecimal> totalMultasPorUsuario = multas.stream()
        .collect(Collectors.groupingBy(
            multa -> {
                Emprestimo emprestimo = repositorioEmprestimo.findById(multa.getEmprestimoId()).get();
                return repositorioUsuario.findById(emprestimo.getUserId());
            },
            Collectors.mapping(
                Multa::getValor,
                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
            )
        ));

        return totalMultasPorUsuario.entrySet().stream()
        .map(entry -> {
            Usuario usuario = entry.getKey();
            BigDecimal totalMultas = entry.getValue();
            return new UsuarioMultaDTO(usuario.getName(), usuario.getDocument(), totalMultas, null);
        })
        .toList();
    }

    
}

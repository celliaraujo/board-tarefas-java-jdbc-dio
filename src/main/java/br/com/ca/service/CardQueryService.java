package br.com.ca.service;

import br.com.ca.persistence.dao.CardDAO;
import br.com.ca.dto.CardDetailsDTO;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class CardQueryService {
    private final Connection connection;

    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        if (id == null) return Optional.empty();
        CardDAO dao = new CardDAO(connection);
        return dao.findById(id);
    }

}

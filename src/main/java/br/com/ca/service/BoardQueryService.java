package br.com.ca.service;

import br.com.ca.persistence.dao.BoardColumnDAO;
import br.com.ca.persistence.dao.BoardDAO;
import br.com.ca.dto.BoardColumnDTO;
import br.com.ca.dto.BoardDetailsDTO;
import br.com.ca.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class BoardQueryService {
    private final Connection connection;

    public Optional<BoardEntity> findById(final Long id) throws SQLException{

        BoardDAO dao = new BoardDAO(connection);
        BoardColumnDAO boardColumnDAO = new BoardColumnDAO(connection);
        Optional<BoardEntity> optional = dao.findById(id);

        if(optional.isPresent()){
            BoardEntity entity = optional.get();
            entity.setBoardColumns(boardColumnDAO.findByBoardId(entity.getId()));
            return Optional.of(entity);
        }
        return Optional.empty();
    }


    public Optional<BoardDetailsDTO> showBoardDetails(final Long id) throws SQLException{
        BoardDAO dao = new BoardDAO(connection);
        BoardColumnDAO boardColumnDAO = new BoardColumnDAO(connection);
        Optional<BoardEntity> optional = dao.findById(id);

        if(optional.isPresent()){
            BoardEntity entity = optional.get();
            List<BoardColumnDTO> columns = boardColumnDAO.findByBoardIdDetails(entity.getId());
            BoardDetailsDTO dto = new BoardDetailsDTO(entity.getId(), entity.getName(), columns);
            return Optional.of(dto);
        }
        return Optional.empty();

    }


}

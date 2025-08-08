package br.com.ca.service;

import br.com.ca.exception.CardBlockedException;
import br.com.ca.exception.CardFinishedException;
import br.com.ca.exception.EntityNotFoundException;
import br.com.ca.persistence.dao.CardDAO;
import br.com.ca.dto.BoardColumnInfoDTO;
import br.com.ca.dto.CardDetailsDTO;
import br.com.ca.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static br.com.ca.persistence.entity.BoardColumnKindEnum.CANCEL;
import static br.com.ca.persistence.entity.BoardColumnKindEnum.FINAL;

@AllArgsConstructor()
public class CardService {
    private final Connection connection;

    public CardEntity insert(final CardEntity entity) throws SQLException{
        try{
            CardDAO dao = new CardDAO(connection);
            dao.insert(entity);
            connection.commit();

        }catch (SQLException ex){
            connection.rollback();
            throw ex;
        }
        return entity;

    }

    public void moveToNextColumn(final Long cardId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException{
        try{
            CardDAO dao = new CardDAO(connection);
            Optional<CardDetailsDTO> optional = dao.findById(cardId);
            CardDetailsDTO dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId)));
            if(dto.blocked()){
                throw new CardBlockedException("Não é possível mover um card bloqueado.");
            }
            BoardColumnInfoDTO currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board."));
            if(currentColumn.kind().equals(FINAL)){
                throw new CardFinishedException("O card já foi finalizado!");
            }

            BoardColumnInfoDTO nextColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.order() == currentColumn.order() + 1)
                    .findFirst().orElseThrow(() -> new IllegalStateException("Card está cancelado."));


            dao.moveToColumn(nextColumn.id(), cardId);
            connection.commit();

        }catch(SQLException ex){
            connection.rollback();
            throw ex;
        }

    }
    public void cancel(final Long cardId, final Long cancelColumnId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException{
        try {
            CardDAO dao = new CardDAO(connection);
            Optional<CardDetailsDTO> optional = dao.findById(cardId);
            CardDetailsDTO dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId)));
            if(dto.blocked()){
                throw new CardBlockedException("Não é possível mover um card bloqueado.");
            }
            BoardColumnInfoDTO currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board."));
            if(currentColumn.kind().equals(FINAL)){
                throw new CardFinishedException("O card já foi finalizado!");
            }

            boardColumnsInfo.stream()
                    .filter(bc -> bc.order() == currentColumn.order() + 1)
                    .findFirst().orElseThrow(() -> new IllegalStateException("Card está cancelado."));

            dao.moveToColumn(cancelColumnId, cardId);
            connection.commit();

        }catch (SQLException ex){
            connection.rollback();
            throw ex;
        }
    }

    public void block(final Long cardId, final String reason, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException{
        try{
            CardDAO dao = new CardDAO(connection);
            Optional<CardDetailsDTO> optional = dao.findById(cardId);
            CardDetailsDTO dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId)));
            if(dto.blocked()){
                throw new CardBlockedException("O card já está bloqueado.");
            }
            BoardColumnInfoDTO currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(cardId))
                    .findFirst().orElseThrow();
            if(currentColumn.kind().equals(FINAL) || currentColumn.kind().equals(CANCEL)){
                String message = "Card em coluna do tipo %s não pode ser bloqueado."
                        .formatted(currentColumn.kind());
                throw  new IllegalStateException(message);

            }
            connection.commit();
        }catch (SQLException ex){
            connection.rollback();
            throw ex;
        }

    }


}

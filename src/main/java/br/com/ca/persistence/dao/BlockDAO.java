package br.com.ca.persistence.dao;

import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import static br.com.ca.persistence.converter.OffsetDateTimeConverter.toTimestamp;

@AllArgsConstructor
public class BlockDAO {
    private final Connection connection;

    public void block(final Long cardId, final String reason) throws SQLException {
        String sql = "INSERT INTO BLOCKS (blocked_at, block_reason, card_id) VALUES (?, ?, ?);";
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            int i = 1;
            statement.setTimestamp(i ++, toTimestamp(OffsetDateTime.now()));
            statement.setString(i ++, reason);
            statement.setLong(i, cardId);
            statement.executeUpdate();

        }

    }

    public void unblock(final Long cardId, final String reason) throws SQLException{
        String sql = "UPDATE BLOCKS SET unblocked_at = ?, unblock_reason = ?,  WHERE card_id = ? AND unblock_reason IS NULL;";
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            int i = 1;
            statement.setTimestamp(i ++, toTimestamp(OffsetDateTime.now()));
            statement.setString(i ++, reason);
            statement.setLong(i, cardId);
            statement.executeUpdate();
        }
    }
}

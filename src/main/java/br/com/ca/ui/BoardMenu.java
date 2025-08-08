package br.com.ca.ui;

import br.com.ca.dto.BoardColumnInfoDTO;
import br.com.ca.dto.BoardDetailsDTO;
import br.com.ca.dto.CardDetailsDTO;
import br.com.ca.persistence.entity.BoardColumnEntity;
import br.com.ca.persistence.entity.BoardEntity;
import br.com.ca.persistence.entity.CardEntity;
import br.com.ca.service.BoardColumnQueryService;
import br.com.ca.service.BoardQueryService;
import br.com.ca.service.CardQueryService;
import br.com.ca.service.CardService;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static br.com.ca.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class BoardMenu {
    private final BoardEntity entity;
    Scanner scanner = new Scanner(System.in);

    public void execute() throws SQLException{
        System.out.printf("""
                ====== BOARD NAME: %s =======
                === Selecione a opção desejada: === \n""", entity.getName().toUpperCase());

        int option = -1;

        while(option != 9) {
            System.out.println("""
                    [1] Criar um card
                    [2] Mover um card
                    [3] Bloquear um card
                    [4] Desbloquear um card
                    [5] Cancelar um card
                    [6] Visualizar board
                    [7] Visualizar coluna com cards
                    [8] Visualizar um card
                    [9] Voltar ao menu anterior
                    [10] Sair
                    """);
            option = scanner.nextInt();
            switch (option) {
                case 1:
                    createCard();
                    break;
                case 2:
                    moveCardNextColumn();
                    break;
                case 3:
                    blockCard();
                    break;
                case 4:
                    unblockCard();
                    break;
                case 5:
                    cancelCard();
                    break;
                case 6:
                    showBoard();
                    break;
                case 7:
                    showColumn();
                    break;
                case 8:
                    showCard();
                    break;
                case 9:
                    System.out.println("Voltando para o menu anterior...");
                    break;
                case 10:
                    System.exit(0);
                break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");

            }
        }
    }



    private void createCard() throws SQLException{

        CardEntity card = new CardEntity();
        scanner.nextLine();
        System.out.print("Informe o título do card: ");
        card.setTitle(scanner.nextLine());
        System.out.print("Informe a descrição do card: ");
        card.setDescription(scanner.nextLine());
        card.setBoardColumn(entity.getInitialColumn());

        try(Connection connection = getConnection()){
            new CardService(connection).insert(card);

        }
    }



    private void showColumn() throws SQLException{
        System.out.printf("Escolha uma coluna do board %s.\n", entity.getName());
        List<Long> columnsIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        Long selectedColumn = -1L;
        while(!columnsIds.contains(selectedColumn)){
            entity.getBoardColumns().forEach(c -> System.out.printf("%s - %s [%s]\n", c.getId(), c.getName(), c.getKind()));
            selectedColumn = scanner.nextLong();
        }
        try(Connection connection = getConnection()){
            Optional<BoardColumnEntity> column = new BoardColumnQueryService(connection).findById(selectedColumn);
            column.ifPresent(co ->{
                System.out.printf("Coluna %s tipo %s\n", co.getName(), co.getKind());
                co.getCards().forEach(ca -> System.out.printf("Card %s - %s \nDescrição: %s\n",
                        ca.getId(), ca.getTitle(), ca.getDescription()));
            });
        }

    }

    private void showBoard() throws SQLException {
        try(Connection connection = getConnection()){
            Optional<BoardDetailsDTO> board = new BoardQueryService(connection).showBoardDetails(entity.getId());
            board.ifPresent(b -> {
                System.out.printf("Board [%s,%s]\n", b.id(), b.name());
                b.columns().forEach(c -> {
                    System.out.printf("Coluna [%s] tipo: [%s] tem %s cards.\n", c.name(), c.kind(), c.cardsAmount());
                });
            });

        }

    }
    private void showCard() throws SQLException{
        System.out.print("Informe o id do card que deseja visualizar: ");
        Long selectedCardId = scanner.nextLong();
        try(Connection connection = getConnection()){
            CardQueryService service = new CardQueryService(connection);
            Optional<CardDetailsDTO> cardOpt = service.findById(selectedCardId);
            cardOpt.ifPresentOrElse(
                    c -> {
                        System.out.printf(
                                """
                                        Card: %s [%s]
                                        Descrição: %s
                                        Bloqueado %s vezes.\n""", c.id(), c.title(), c.description(), c.blocksAmount());
                        System.out.printf(c.blocked() ? "Está bloqueado. Motivo: %s\n"
                                : "Não está bloqueado.\n", c.blockReason());
                        System.out.printf("Referenciado na coluna: %s - %s\n", c.columnId(), c.columnName());

                    },
                    () -> System.out.printf("Card com id %s não encontrado.\n", selectedCardId));

        }

    }

    private void moveCardNextColumn() throws SQLException {
        System.out.print("Informe o id do card que deseja mover: ");
        Long cardId = scanner.nextLong();
        scanner.nextLine();
        List<BoardColumnInfoDTO> boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(Connection connection = getConnection()){
            new CardService(connection).moveToNextColumn(cardId, boardColumnsInfo);
            connection.commit();
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }

    }

    private void blockCard() throws SQLException{
        System.out.print("Informe o id do card que deseja bloquear: ");
        Long cardId = scanner.nextLong();
        scanner.nextLine();
        System.out.println("Informe o motivo do bloqueio: ");
        String reason = scanner.nextLine();
        List<BoardColumnInfoDTO> boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(Connection connection = getConnection()){
            new CardService(connection).block(cardId,reason,boardColumnsInfo);

        }catch(RuntimeException ex){
            System.out.println(ex.getMessage());
        }

    }
    private void unblockCard() throws SQLException{
        System.out.print("Informe o id do card que deseja desbloquear: ");
        Long cardId = scanner.nextLong();
        scanner.nextLine();
        System.out.println("Informe o motivo do desbloqueio: ");
        String reason = scanner.nextLine();

        try(Connection connection = getConnection()){
            new CardService(connection).unblock(cardId,reason);

        }catch(RuntimeException ex){
            System.out.println(ex.getMessage());
        }


    }

    private void cancelCard() throws SQLException{
        System.out.print("Informe o id do card que deseja cancelar: ");
        Long cardId = scanner.nextLong();
        BoardColumnEntity cancelColumn = entity.getCancelColumn();
        List<BoardColumnInfoDTO> boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(Connection connection = getConnection()){
            new CardService(connection).cancel(cardId, cancelColumn.getId(), boardColumnsInfo);
            connection.commit();
        }catch(RuntimeException ex){
            System.out.println(ex.getMessage());
        }

    }
}

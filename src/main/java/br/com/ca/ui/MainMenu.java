package br.com.ca.ui;

import br.com.ca.persistence.entity.BoardColumnEntity;
import br.com.ca.persistence.entity.BoardColumnKindEnum;
import br.com.ca.persistence.entity.BoardEntity;
import br.com.ca.service.BoardQueryService;
import br.com.ca.service.BoardService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static br.com.ca.persistence.config.ConnectionConfig.getConnection;
import static br.com.ca.persistence.entity.BoardColumnKindEnum.CANCEL;
import static br.com.ca.persistence.entity.BoardColumnKindEnum.FINAL;
import static br.com.ca.persistence.entity.BoardColumnKindEnum.INITIAL;
import static br.com.ca.persistence.entity.BoardColumnKindEnum.PENDING;

public class MainMenu {
    private final Scanner scanner = new Scanner(System.in);

    public void execute() throws SQLException{
        System.out.println("""
                ==========* Gerenciador de Boards *========== 
                          Escolha a opção desejada:
                ---------------------------------------------""");
        int option = 0;
        while(true){
            System.out.println("""
                    [1] Criar um novo board
                    [2] Selecionar um board
                    [3] Excluir um board
                    [4] Sair""");
            option = scanner.nextInt();

            switch (option){
                case 1:
                    createBoard();
                    break;
                case 2:
                    selectBoard();
                    break;
                case 3:
                    deleteBoard();
                    break;
                case 4:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opção inválida! Escolha uma opção do menu.");
            }
        }


    }
    private void createBoard() throws SQLException{
        System.out.printf("Informe o nome do board: ");
        String name = scanner.next();
        scanner.nextLine();

        System.out.println("Digite a quantidade de colunas adicionais ou 0 para 3 colunas padrões.");
        int additionalColumns = scanner.nextInt();
        scanner.nextLine();

        List<BoardColumnEntity> columns = new ArrayList<>();

        System.out.printf("Informe o nome da coluna inicial: ");
        String initialColumnName = scanner.nextLine();
        BoardColumnEntity initialColumn = createColumn(initialColumnName, INITIAL, 0);
        columns.add(initialColumn);

        for(int i = 0; i < additionalColumns; i++){
            System.out.printf("Informe o nome da coluna de tarefa pendente: ");
            String pendingColumnName = scanner.nextLine();
            BoardColumnEntity pendinglColumn = createColumn(pendingColumnName, PENDING, i + 1);
            columns.add(pendinglColumn);
        }

        System.out.printf("Informe o nome da coluna final: ");
        String finalColumnName = scanner.nextLine();
        BoardColumnEntity finalColumn = createColumn(finalColumnName, FINAL, additionalColumns + 1);
        columns.add(finalColumn);

        System.out.printf("Informe o nome da coluna de tarefas canceladas: ");
        String cancelColumnName = scanner.nextLine();
        BoardColumnEntity cancelColumn = createColumn(cancelColumnName, CANCEL, columns.size());
        columns.add(cancelColumn);

        BoardEntity entity = new BoardEntity();
        entity.setName(name);
        entity.setBoardColumns(columns);


        try(Connection connection = getConnection()){
            BoardService service = new BoardService(connection);
            service.insert(entity);
            System.out.println("Board criado com sucesso!");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
    private void selectBoard() throws SQLException{
        System.out.print("Informe o id do board que deseja selecionar: ");
        long id = scanner.nextLong();
        try(Connection connection = getConnection()){
            BoardQueryService queryService = new BoardQueryService(connection);
            Optional<BoardEntity> board = queryService.findById(id);
            if(board.isPresent()){
                BoardMenu boardMenu = new BoardMenu(board.get(), scanner);
                boardMenu.execute();

            }else System.out.printf("Não foi encontrado um board com id = %s.\n", id);
        }

    }


    private void deleteBoard() throws SQLException {
        System.out.print("Informe o id do board que deseja excluir: ");
        long id = scanner.nextLong();
        try(Connection connection = getConnection()){
            BoardService service = new BoardService(connection);
            if(service.delete(id)){
                System.out.printf("O board %s foi excluido.\n", id);
            }else{
                System.out.printf("Board %d não encontrado.\n", id);
            }

        }

    }

    private BoardColumnEntity createColumn(final String name, final BoardColumnKindEnum kind, final int order){
        BoardColumnEntity boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setKind(kind);
        boardColumn.setOrder(order);
        return boardColumn;

    }
}

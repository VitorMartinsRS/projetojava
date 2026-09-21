package com.chess;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainView {
    private final GameController controller;
    private final GridPane boardGrid;
    private final Label statusLabel;
    private final Label titleLabel;
    private final ListView<String> moveList;

    public MainView(GameController controller) {
        this.controller = controller;
        this.boardGrid = new GridPane();
        this.statusLabel = new Label("Turno: Brancas");
        this.titleLabel = new Label("Xadrez");
        this.moveList = new ListView<>();
    }

    public void show(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #121826;");

        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(Font.font("Segoe UI", 28));

        HBox topBar = new HBox(12, titleLabel);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 0, 14, 0));

        VBox sidePanel = new VBox(18);
        sidePanel.setPadding(new Insets(0, 0, 0, 20));
        sidePanel.setPrefWidth(220);
        sidePanel.setAlignment(Pos.TOP_CENTER);

        statusLabel.setTextFill(Color.web("#e8edf7"));
        statusLabel.setFont(Font.font("Segoe UI", 16));

        Button novoJogo = new Button("Novo jogo");
        Button reiniciar = new Button("Reiniciar");
        Button desfazer = new Button("Desfazer");
        Button telaCheia = new Button("Tela cheia");

        novoJogo.setPrefWidth(180);
        reiniciar.setPrefWidth(180);
        desfazer.setPrefWidth(180);
        telaCheia.setPrefWidth(180);

        moveList.setPlaceholder(new Label("Nenhuma jogada ainda"));
        moveList.setPrefHeight(340);
        moveList.setMaxWidth(Double.MAX_VALUE);

        novoJogo.setOnAction(event -> controller.start(stage));
        reiniciar.setOnAction(event -> controller.start(stage));
        telaCheia.setOnAction(event -> stage.setFullScreen(!stage.isFullScreen()));

        sidePanel.getChildren().addAll(statusLabel, novoJogo, reiniciar, desfazer, telaCheia,
            new Label("Jogadas"), moveList);

        boardGrid.setHgap(2);
        boardGrid.setVgap(2);
        boardGrid.setAlignment(Pos.CENTER);
        boardGrid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        boardGrid.setStyle("-fx-background-color: #d0b08c; -fx-padding: 10; -fx-border-color: #3d2b1f; -fx-border-width: 3;");
        for (int index = 0; index < 8; index++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(12.5);
            column.setHgrow(javafx.scene.layout.Priority.ALWAYS);
            boardGrid.getColumnConstraints().add(column);

            RowConstraints row = new RowConstraints();
            row.setPercentHeight(12.5);
            row.setVgrow(javafx.scene.layout.Priority.ALWAYS);
            boardGrid.getRowConstraints().add(row);
        }

        renderBoard();

        root.setTop(topBar);
        root.setCenter(boardGrid);
        root.setRight(sidePanel);

        Scene scene = new Scene(root, 900, 680);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Xadrez - Interface");
        stage.setResizable(true);
        stage.show();
        stage.setMaximized(true);
    }

    public void renderBoard() {
        boardGrid.getChildren().clear();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                BoardCell cell = controller.getBoard().getCell(row, col);
                StackPane square = new StackPane();
                square.setMinSize(0, 0);
                square.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                square.setAlignment(Pos.CENTER);

                boolean light = (row + col) % 2 == 0;
                square.setStyle(light
                        ? "-fx-background-color: #f0d9b5; -fx-border-color: #7a5b3a; -fx-border-width: 1;"
                        : "-fx-background-color: #b58863; -fx-border-color: #7a5b3a; -fx-border-width: 1;");

                if (controller.getSelectedCell() != null
                        && controller.getSelectedCell().getRow() == row
                        && controller.getSelectedCell().getColumn() == col) {
                    square.setStyle(square.getStyle() + "-fx-border-color: #1f7a8c; -fx-border-width: 3;");
                }

                if (cell.getPiece() != null
                        && cell.getPiece().getType() == Piece.Type.KING
                        && controller.getBoard().isKingInCheck(cell.getPiece().getColor())) {
                    square.setStyle("-fx-background-color: #d64545; -fx-border-color: #8f1d1d; -fx-border-width: 3;");
                }

                if (cell != null && cell.getPiece() != null) {
                    Label chessPiece = new Label(cell.getPiece().getUnicodeSymbol());
                    chessPiece.setFont(Font.font("Segoe UI Symbol", 34));
                    chessPiece.setTextFill(cell.getPiece().getColor() == Piece.Color.WHITE ? Color.WHITE : Color.BLACK);
                    square.getChildren().add(chessPiece);
                }

                final int finalRow = row;
                final int finalCol = col;
                square.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> controller.handleSquareClick(finalRow, finalCol));

                boardGrid.add(square, col, row);
            }
        }
    }

    public void updateStatus(String text) {
        statusLabel.setText(text);
    }

    public void updateMoveList(List<Move> moves) {
        moveList.getItems().clear();
        for (int index = 0; index < moves.size(); index++) {
            Move move = moves.get(index);
            String capture = move.getCapturedPiece() == null ? "-" : "x";
            String notation = String.format("%d. %s %s %s", index + 1,
                    squareName(move.getFrom()), capture, squareName(move.getTo()));
            moveList.getItems().add(notation);
        }
        if (!moveList.getItems().isEmpty()) {
            moveList.scrollTo(moveList.getItems().size() - 1);
        }
    }

    private String squareName(BoardCell cell) {
        return String.valueOf((char) ('a' + cell.getColumn())) + (8 - cell.getRow());
    }
}

package com.chess;

import java.util.ArrayList;
import java.util.List;

import javafx.stage.Stage;

public class GameController {
    private final ChessBoard board;
    private final MainView view;
    private Piece.Color currentTurn;
    private final List<Move> moveHistory;
    private BoardCell selectedCell;

    public GameController() {
        this.board = new ChessBoard();
        this.view = new MainView(this);
        this.currentTurn = Piece.Color.WHITE;
        this.moveHistory = new ArrayList<>();
        this.selectedCell = null;
    }

    public void start(Stage stage) {
        board.reset();
        selectedCell = null;
        currentTurn = Piece.Color.WHITE;
        moveHistory.clear();
        view.show(stage);
        refreshBoard();
    }

    public void switchTurn() {
        currentTurn = (currentTurn == Piece.Color.WHITE) ? Piece.Color.BLACK : Piece.Color.WHITE;
    }

    public ChessBoard getBoard() {
        return board;
    }

    public Piece.Color getCurrentTurn() {
        return currentTurn;
    }

    public List<Move> getMoveHistory() {
        return moveHistory;
    }

    public void refreshBoard() {
        view.renderBoard();
        String turn = currentTurn == Piece.Color.WHITE ? "Brancas" : "Pretas";
        view.updateStatus(board.isKingInCheck(currentTurn) ? "Xeque! Turno: " + turn : "Turno: " + turn);
        view.updateMoveList(moveHistory);
    }

    public void handleSquareClick(int row, int col) {
        BoardCell clickedCell = board.getCell(row, col);
        if (clickedCell == null) {
            return;
        }

        if (selectedCell == null) {
            if (clickedCell.getPiece() != null && clickedCell.getPiece().getColor() == currentTurn) {
                selectedCell = clickedCell;
                view.updateStatus("Peça selecionada: " + clickedCell.getPiece().getType());
                refreshBoard();
            }
            return;
        }

        if (selectedCell == clickedCell) {
            selectedCell = null;
            refreshBoard();
            return;
        }

        if (board.isValidMove(selectedCell, clickedCell, currentTurn)) {
            BoardCell origin = selectedCell;
            Piece capturedPiece = clickedCell.getPiece();
            board.movePiece(origin, clickedCell);
            moveHistory.add(new Move(origin, clickedCell, capturedPiece));
            selectedCell = null;
            switchTurn();
            refreshBoard();
            return;
        }

        if (clickedCell.getPiece() != null && clickedCell.getPiece().getColor() == currentTurn) {
            selectedCell = clickedCell;
            view.updateStatus("Nova seleção: " + clickedCell.getPiece().getType());
            refreshBoard();
            return;
        }

        selectedCell = null;
        view.updateStatus("Jogada inválida. Tente novamente.");
        refreshBoard();
    }

    public BoardCell getSelectedCell() {
        return selectedCell;
    }
}

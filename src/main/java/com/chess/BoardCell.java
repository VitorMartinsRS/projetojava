package com.chess;

public class BoardCell {
    private final int row;
    private final int column;
    private Piece piece;

    public BoardCell(int row, int column) {
        this.row = row;
        this.column = column;
        this.piece = null;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public boolean isEmpty() {
        return piece == null;
    }
}

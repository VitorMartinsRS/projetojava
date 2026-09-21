package com.chess;

public class Move {
    private final BoardCell from;
    private final BoardCell to;
    private final Piece capturedPiece;

    public Move(BoardCell from, BoardCell to, Piece capturedPiece) {
        this.from = from;
        this.to = to;
        this.capturedPiece = capturedPiece;
    }

    public BoardCell getFrom() {
        return from;
    }

    public BoardCell getTo() {
        return to;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }
}

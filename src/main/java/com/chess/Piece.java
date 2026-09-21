package com.chess;

public class Piece {
    public enum Type {
        KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN
    }

    public enum Color {
        WHITE, BLACK
    }

    private final Type type;
    private final Color color;

    public Piece(Type type, Color color) {
        this.type = type;
        this.color = color;
    }

    public Type getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }

    public String getSymbol() {
        String symbol = "";
        switch (type) {
            case KING:
                symbol = "K";
                break;
            case QUEEN:
                symbol = "Q";
                break;
            case ROOK:
                symbol = "R";
                break;
            case BISHOP:
                symbol = "B";
                break;
            case KNIGHT:
                symbol = "N";
                break;
            case PAWN:
                symbol = "P";
                break;
            default:
                break;
        }

        return (color == Color.WHITE) ? symbol.toUpperCase() : symbol.toLowerCase();
    }

    public String getUnicodeSymbol() {
        String symbol = "";
        switch (type) {
            case KING:
                symbol = "♔";
                break;
            case QUEEN:
                symbol = "♕";
                break;
            case ROOK:
                symbol = "♖";
                break;
            case BISHOP:
                symbol = "♗";
                break;
            case KNIGHT:
                symbol = "♘";
                break;
            case PAWN:
                symbol = "♙";
                break;
            default:
                break;
        }

        if (color == Color.BLACK) {
            switch (type) {
                case KING:
                    symbol = "♚";
                    break;
                case QUEEN:
                    symbol = "♛";
                    break;
                case ROOK:
                    symbol = "♜";
                    break;
                case BISHOP:
                    symbol = "♝";
                    break;
                case KNIGHT:
                    symbol = "♞";
                    break;
                case PAWN:
                    symbol = "♟";
                    break;
                default:
                    break;
            }
        }

        return symbol;
    }
}

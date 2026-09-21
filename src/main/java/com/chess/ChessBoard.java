package com.chess;

public class ChessBoard {
    private final BoardCell[][] cells;
    private boolean whiteKingMoved;
    private boolean blackKingMoved;
    private boolean whiteLeftRookMoved;
    private boolean whiteRightRookMoved;
    private boolean blackLeftRookMoved;
    private boolean blackRightRookMoved;
    private int lastMoveFromRow = -1;
    private int lastMoveFromColumn = -1;
    private int lastMoveToRow = -1;
    private int lastMoveToColumn = -1;
    private Piece.Type lastMovedPieceType;
    private Piece.Color lastMovedPieceColor;

    public ChessBoard() {
        this.cells = new BoardCell[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                cells[row][col] = new BoardCell(row, col);
            }
        }
        resetCastlingFlags();
        resetLastMove();
    }

    public BoardCell[][] getCells() {
        return cells;
    }

    public BoardCell getCell(int row, int column) {
        if (row < 0 || row >= 8 || column < 0 || column >= 8) {
            return null;
        }
        return cells[row][column];
    }

    public void reset() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                cells[row][col].setPiece(null);
            }
        }

        for (int col = 0; col < 8; col++) {
            cells[6][col].setPiece(new Piece(Piece.Type.PAWN, Piece.Color.WHITE));
            cells[1][col].setPiece(new Piece(Piece.Type.PAWN, Piece.Color.BLACK));
        }

        cells[7][0].setPiece(new Piece(Piece.Type.ROOK, Piece.Color.WHITE));
        cells[7][1].setPiece(new Piece(Piece.Type.KNIGHT, Piece.Color.WHITE));
        cells[7][2].setPiece(new Piece(Piece.Type.BISHOP, Piece.Color.WHITE));
        cells[7][3].setPiece(new Piece(Piece.Type.QUEEN, Piece.Color.WHITE));
        cells[7][4].setPiece(new Piece(Piece.Type.KING, Piece.Color.WHITE));
        cells[7][5].setPiece(new Piece(Piece.Type.BISHOP, Piece.Color.WHITE));
        cells[7][6].setPiece(new Piece(Piece.Type.KNIGHT, Piece.Color.WHITE));
        cells[7][7].setPiece(new Piece(Piece.Type.ROOK, Piece.Color.WHITE));

        cells[0][0].setPiece(new Piece(Piece.Type.ROOK, Piece.Color.BLACK));
        cells[0][1].setPiece(new Piece(Piece.Type.KNIGHT, Piece.Color.BLACK));
        cells[0][2].setPiece(new Piece(Piece.Type.BISHOP, Piece.Color.BLACK));
        cells[0][3].setPiece(new Piece(Piece.Type.QUEEN, Piece.Color.BLACK));
        cells[0][4].setPiece(new Piece(Piece.Type.KING, Piece.Color.BLACK));
        cells[0][5].setPiece(new Piece(Piece.Type.BISHOP, Piece.Color.BLACK));
        cells[0][6].setPiece(new Piece(Piece.Type.KNIGHT, Piece.Color.BLACK));
        cells[0][7].setPiece(new Piece(Piece.Type.ROOK, Piece.Color.BLACK));

        resetCastlingFlags();
        resetLastMove();
    }

    public boolean isValidMove(BoardCell from, BoardCell to, Piece.Color turn) {
        if (from == null || to == null || from == to) {
            return false;
        }

        Piece movingPiece = from.getPiece();
        if (movingPiece == null || movingPiece.getColor() != turn) {
            return false;
        }

        if (to.getPiece() != null && to.getPiece().getColor() == turn) {
            return false;
        }

        if (movingPiece.getType() == Piece.Type.KING && Math.abs(to.getColumn() - from.getColumn()) == 2) {
            if (!canCastle(turn, to.getColumn() > from.getColumn())) {
                return false;
            }
        }

        if (!matchesMovementPattern(from, to, movingPiece)) {
            if (!isEnPassantMove(from, to, movingPiece)) {
                return false;
            }
        }

        if (!isClearPath(from, to, movingPiece)) {
            return false;
        }

        ChessBoard simulation = copyBoard();
        BoardCell simulationFrom = simulation.getCell(from.getRow(), from.getColumn());
        BoardCell simulationTo = simulation.getCell(to.getRow(), to.getColumn());
        if (simulationFrom == null || simulationTo == null) {
            return false;
        }

        simulation.applyMove(simulationFrom, simulationTo);
        return !simulation.isKingInCheck(turn);
    }

    public void movePiece(BoardCell from, BoardCell to) {
        if (from == null || to == null) {
            return;
        }

        Piece movedPiece = from.getPiece();
        if (movedPiece == null) {
            return;
        }

        boolean enPassant = isEnPassantMove(from, to, movedPiece);

        if (movedPiece.getType() == Piece.Type.KING && Math.abs(to.getColumn() - from.getColumn()) == 2) {
            int rookFromCol = (to.getColumn() > from.getColumn()) ? 7 : 0;
            int rookToCol = (to.getColumn() > from.getColumn()) ? 5 : 3;
            BoardCell rookFrom = getCell(from.getRow(), rookFromCol);
            BoardCell rookTo = getCell(from.getRow(), rookToCol);
            if (rookFrom != null && rookFrom.getPiece() != null && rookFrom.getPiece().getType() == Piece.Type.ROOK) {
                rookTo.setPiece(rookFrom.getPiece());
                rookFrom.setPiece(null);
            }
        }

        if (movedPiece.getType() == Piece.Type.KING) {
            if (movedPiece.getColor() == Piece.Color.WHITE) {
                whiteKingMoved = true;
            } else {
                blackKingMoved = true;
            }
        }

        if (movedPiece.getType() == Piece.Type.ROOK) {
            if (from.getRow() == 7 && from.getColumn() == 0) {
                whiteLeftRookMoved = true;
            }
            if (from.getRow() == 7 && from.getColumn() == 7) {
                whiteRightRookMoved = true;
            }
            if (from.getRow() == 0 && from.getColumn() == 0) {
                blackLeftRookMoved = true;
            }
            if (from.getRow() == 0 && from.getColumn() == 7) {
                blackRightRookMoved = true;
            }
        }

        applyMove(from, to);
        if (enPassant) {
            BoardCell capturedPawn = getCell(from.getRow(), to.getColumn());
            if (capturedPawn != null) {
                capturedPawn.setPiece(null);
            }
        }
        recordLastMove(from, to, movedPiece);
    }

    private boolean matchesMovementPattern(BoardCell from, BoardCell to, Piece piece) {
        int rowDiff = to.getRow() - from.getRow();
        int colDiff = to.getColumn() - from.getColumn();
        int absRow = Math.abs(rowDiff);
        int absCol = Math.abs(colDiff);

        switch (piece.getType()) {
            case PAWN:
                int direction = (piece.getColor() == Piece.Color.WHITE) ? -1 : 1;
                if (colDiff == 0 && rowDiff == direction && to.getPiece() == null) {
                    return true;
                }
                if (colDiff == 0 && rowDiff == 2 * direction && from.getRow() == (piece.getColor() == Piece.Color.WHITE ? 6 : 1)
                        && to.getPiece() == null && getCell(from.getRow() + direction, from.getColumn()).getPiece() == null) {
                    return true;
                }
                return absRow == 1 && absCol == 1 && to.getPiece() != null && to.getPiece().getColor() != piece.getColor();
            case ROOK:
                return (rowDiff == 0 && colDiff != 0) || (rowDiff != 0 && colDiff == 0);
            case KNIGHT:
                return (absRow == 2 && absCol == 1) || (absRow == 1 && absCol == 2);
            case BISHOP:
                return absRow == absCol;
            case QUEEN:
                return (absRow == absCol) || (rowDiff == 0 && colDiff != 0) || (rowDiff != 0 && colDiff == 0);
            case KING:
                if (Math.abs(colDiff) == 2 && rowDiff == 0) {
                    return true;
                }
                return absRow <= 1 && absCol <= 1 && !(absRow == 0 && absCol == 0);
            default:
                return false;
        }
    }

    private boolean isClearPath(BoardCell from, BoardCell to, Piece piece) {
        if (piece.getType() == Piece.Type.PAWN || piece.getType() == Piece.Type.KNIGHT) {
            return true;
        }

        if (piece.getType() == Piece.Type.KING && Math.abs(to.getColumn() - from.getColumn()) == 2) {
            int startCol = from.getColumn();
            int endCol = to.getColumn();
            int step = Integer.compare(endCol - startCol, 0);
            int col = startCol + step;
            while (col != endCol) {
                BoardCell cell = getCell(from.getRow(), col);
                if (cell == null || cell.getPiece() != null) {
                    return false;
                }
                col += step;
            }
            return true;
        }

        int rowStep = Integer.compare(to.getRow() - from.getRow(), 0);
        int colStep = Integer.compare(to.getColumn() - from.getColumn(), 0);
        int row = from.getRow() + rowStep;
        int col = from.getColumn() + colStep;

        while (row != to.getRow() || col != to.getColumn()) {
            BoardCell cell = getCell(row, col);
            if (cell == null || cell.getPiece() != null) {
                return false;
            }
            row += rowStep;
            col += colStep;
        }

        return true;
    }

    public boolean isKingInCheck(Piece.Color color) {
        BoardCell kingCell = findKing(color);
        if (kingCell == null) {
            return true;
        }

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                BoardCell cell = getCell(row, col);
                Piece piece = cell.getPiece();
                if (piece != null && piece.getColor() != color && canAttack(cell, kingCell)) {
                    return true;
                }
            }
        }

        return false;
    }

    private BoardCell findKing(Piece.Color color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                BoardCell cell = getCell(row, col);
                Piece piece = cell.getPiece();
                if (piece != null && piece.getType() == Piece.Type.KING && piece.getColor() == color) {
                    return cell;
                }
            }
        }
        return null;
    }

    private boolean canAttack(BoardCell from, BoardCell target) {
        Piece piece = from.getPiece();
        if (piece == null) {
            return false;
        }

        int rowDiff = target.getRow() - from.getRow();
        int colDiff = target.getColumn() - from.getColumn();
        int absRow = Math.abs(rowDiff);
        int absCol = Math.abs(colDiff);

        switch (piece.getType()) {
            case PAWN:
                int direction = (piece.getColor() == Piece.Color.WHITE) ? -1 : 1;
                return absRow == 1 && absCol == 1 && rowDiff == direction;
            case ROOK:
                if (rowDiff == 0 && colDiff != 0) {
                    return isClearPath(from, target, piece);
                }
                if (rowDiff != 0 && colDiff == 0) {
                    return isClearPath(from, target, piece);
                }
                return false;
            case KNIGHT:
                return (absRow == 2 && absCol == 1) || (absRow == 1 && absCol == 2);
            case BISHOP:
                return absRow == absCol && isClearPath(from, target, piece);
            case QUEEN:
                if (absRow == absCol) {
                    return isClearPath(from, target, piece);
                }
                if (rowDiff == 0 || colDiff == 0) {
                    return isClearPath(from, target, piece);
                }
                return false;
            case KING:
                return absRow <= 1 && absCol <= 1 && !(absRow == 0 && absCol == 0);
            default:
                return false;
        }
    }

    private boolean canCastle(Piece.Color color, boolean kingSide) {
        if (color == Piece.Color.WHITE && whiteKingMoved) {
            return false;
        }
        if (color == Piece.Color.BLACK && blackKingMoved) {
            return false;
        }

        int row = (color == Piece.Color.WHITE) ? 7 : 0;
        int rookCol = kingSide ? 7 : 0;
        int kingFromCol = 4;

        if (kingSide) {
            if (color == Piece.Color.WHITE && whiteRightRookMoved) {
                return false;
            }
            if (color == Piece.Color.BLACK && blackRightRookMoved) {
                return false;
            }
        } else {
            if (color == Piece.Color.WHITE && whiteLeftRookMoved) {
                return false;
            }
            if (color == Piece.Color.BLACK && blackLeftRookMoved) {
                return false;
            }
        }

        BoardCell rookCell = getCell(row, rookCol);
        if (rookCell == null || rookCell.getPiece() == null || rookCell.getPiece().getType() != Piece.Type.ROOK || rookCell.getPiece().getColor() != color) {
            return false;
        }

        BoardCell kingCell = getCell(row, kingFromCol);
        if (kingCell == null || kingCell.getPiece() == null || kingCell.getPiece().getType() != Piece.Type.KING || kingCell.getPiece().getColor() != color) {
            return false;
        }

        int step = kingSide ? 1 : -1;
        for (int col = kingFromCol + step; col != rookCol; col += step) {
            BoardCell square = getCell(row, col);
            if (square == null || square.getPiece() != null) {
                return false;
            }
        }

        int[] passingSquares = kingSide ? new int[] {5, 6} : new int[] {1, 2, 3};
        for (int col : passingSquares) {
            if (isSquareAttacked(row, col, color == Piece.Color.WHITE ? Piece.Color.BLACK : Piece.Color.WHITE)) {
                return false;
            }
        }

        if (isSquareAttacked(row, 4, color == Piece.Color.WHITE ? Piece.Color.BLACK : Piece.Color.WHITE)) {
            return false;
        }

        return true;
    }

    private boolean isSquareAttacked(int row, int col, Piece.Color attackingColor) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                BoardCell cell = getCell(r, c);
                Piece piece = cell.getPiece();
                if (piece != null && piece.getColor() == attackingColor && canAttack(cell, getCell(row, col))) {
                    return true;
                }
            }
        }
        return false;
    }

    private ChessBoard copyBoard() {
        ChessBoard copy = new ChessBoard();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece original = this.cells[row][col].getPiece();
                if (original != null) {
                    copy.cells[row][col].setPiece(new Piece(original.getType(), original.getColor()));
                }
            }
        }
        copy.whiteKingMoved = this.whiteKingMoved;
        copy.blackKingMoved = this.blackKingMoved;
        copy.whiteLeftRookMoved = this.whiteLeftRookMoved;
        copy.whiteRightRookMoved = this.whiteRightRookMoved;
        copy.blackLeftRookMoved = this.blackLeftRookMoved;
        copy.blackRightRookMoved = this.blackRightRookMoved;
        copy.lastMoveFromRow = this.lastMoveFromRow;
        copy.lastMoveFromColumn = this.lastMoveFromColumn;
        copy.lastMoveToRow = this.lastMoveToRow;
        copy.lastMoveToColumn = this.lastMoveToColumn;
        copy.lastMovedPieceType = this.lastMovedPieceType;
        copy.lastMovedPieceColor = this.lastMovedPieceColor;
        return copy;
    }

    private void resetCastlingFlags() {
        whiteKingMoved = false;
        blackKingMoved = false;
        whiteLeftRookMoved = false;
        whiteRightRookMoved = false;
        blackLeftRookMoved = false;
        blackRightRookMoved = false;
    }

    private void resetLastMove() {
        lastMoveFromRow = -1;
        lastMoveFromColumn = -1;
        lastMoveToRow = -1;
        lastMoveToColumn = -1;
        lastMovedPieceType = null;
        lastMovedPieceColor = null;
    }

    private boolean isEnPassantMove(BoardCell from, BoardCell to, Piece piece) {
        if (piece.getType() != Piece.Type.PAWN || to.getPiece() != null
                || Math.abs(to.getColumn() - from.getColumn()) != 1
                || to.getRow() - from.getRow() != (piece.getColor() == Piece.Color.WHITE ? -1 : 1)) {
            return false;
        }

        if (lastMovedPieceType != Piece.Type.PAWN || lastMovedPieceColor == piece.getColor()
                || lastMoveFromColumn != to.getColumn() || lastMoveToColumn != to.getColumn()
                || Math.abs(lastMoveToRow - lastMoveFromRow) != 2
                || lastMoveToRow != from.getRow()) {
            return false;
        }

        BoardCell adjacentCell = getCell(from.getRow(), to.getColumn());
        return adjacentCell != null && adjacentCell.getPiece() != null
                && adjacentCell.getPiece().getType() == Piece.Type.PAWN
                && adjacentCell.getPiece().getColor() == lastMovedPieceColor;
    }

    private void recordLastMove(BoardCell from, BoardCell to, Piece movedPiece) {
        lastMoveFromRow = from.getRow();
        lastMoveFromColumn = from.getColumn();
        lastMoveToRow = to.getRow();
        lastMoveToColumn = to.getColumn();
        lastMovedPieceType = movedPiece.getType();
        lastMovedPieceColor = movedPiece.getColor();
    }

    private void applyMove(BoardCell from, BoardCell to) {
        Piece movedPiece = from.getPiece();
        if (movedPiece == null) {
            return;
        }
        boolean enPassant = isEnPassantMove(from, to, movedPiece);
        to.setPiece(movedPiece);
        from.setPiece(null);
        if (enPassant) {
            BoardCell capturedPawn = getCell(from.getRow(), to.getColumn());
            if (capturedPawn != null) {
                capturedPawn.setPiece(null);
            }
        }
    }
}

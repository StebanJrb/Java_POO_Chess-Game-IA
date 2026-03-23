package org.Chess.model;

import org.Chess.model.pieces.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the 8x8 chess board.
 */
public class Board {
    private final Piece[][] grid;
    private Position enPassantTarget;

    public Board() {
        grid = new Piece[8][8];
    }

    /**
     * Initialize the board to the standard starting position.
     */
    public void setupInitialPosition() {
        // Clear the board
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                grid[r][c] = null;

        // Place black pieces (top)
        grid[0][0] = new Rook(PieceColor.BLACK);
        grid[0][1] = new Knight(PieceColor.BLACK);
        grid[0][2] = new Bishop(PieceColor.BLACK);
        grid[0][3] = new Queen(PieceColor.BLACK);
        grid[0][4] = new King(PieceColor.BLACK);
        grid[0][5] = new Bishop(PieceColor.BLACK);
        grid[0][6] = new Knight(PieceColor.BLACK);
        grid[0][7] = new Rook(PieceColor.BLACK);
        for (int c = 0; c < 8; c++) {
            grid[1][c] = new Pawn(PieceColor.BLACK);
        }

        // Place white pieces (bottom)
        grid[7][0] = new Rook(PieceColor.WHITE);
        grid[7][1] = new Knight(PieceColor.WHITE);
        grid[7][2] = new Bishop(PieceColor.WHITE);
        grid[7][3] = new Queen(PieceColor.WHITE);
        grid[7][4] = new King(PieceColor.WHITE);
        grid[7][5] = new Bishop(PieceColor.WHITE);
        grid[7][6] = new Knight(PieceColor.WHITE);
        grid[7][7] = new Rook(PieceColor.WHITE);
        for (int c = 0; c < 8; c++) {
            grid[6][c] = new Pawn(PieceColor.WHITE);
        }

        enPassantTarget = null;
    }

    public Piece getPiece(Position pos) {
        if (!pos.isValid()) return null;
        return grid[pos.getRow()][pos.getCol()];
    }

    public void setPiece(Position pos, Piece piece) {
        if (pos.isValid()) {
            grid[pos.getRow()][pos.getCol()] = piece;
        }
    }

    public void removePiece(Position pos) {
        if (pos.isValid()) {
            grid[pos.getRow()][pos.getCol()] = null;
        }
    }

    public Position getEnPassantTarget() {
        return enPassantTarget;
    }

    public void setEnPassantTarget(Position target) {
        this.enPassantTarget = target;
    }

    /**
     * Check if a square is attacked by a given color.
     * Used for castling validation and check detection.
     */
    public boolean isSquareAttacked(Position pos, PieceColor byColor) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = grid[r][c];
                if (piece != null && piece.getColor() == byColor) {
                    Position from = new Position(r, c);
                    // For king, only check normal moves (not castling) to avoid recursion
                    if (piece.getType() == PieceType.KING) {
                        int[][] offsets = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};
                        for (int[] off : offsets) {
                            Position target = new Position(r + off[0], c + off[1]);
                            if (target.equals(pos)) return true;
                        }
                    } else {
                        List<Position> rawMoves = piece.getRawMoves(from, this);
                        for (Position move : rawMoves) {
                            if (move.equals(pos)) return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Find the king position for a given color.
     */
    public Position findKing(PieceColor color) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = grid[r][c];
                if (piece != null && piece.getType() == PieceType.KING && piece.getColor() == color) {
                    return new Position(r, c);
                }
            }
        }
        return null; // Should never happen in a valid game
    }

    /**
     * Check if a given color's king is in check.
     */
    public boolean isInCheck(PieceColor color) {
        Position kingPos = findKing(color);
        if (kingPos == null) return false;
        return isSquareAttacked(kingPos, color.opposite());
    }

    /**
     * Create a deep copy of the board for simulation.
     */
    public Board copy() {
        Board newBoard = new Board();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = grid[r][c];
                if (piece != null) {
                    Piece newPiece = switch (piece.getType()) {
                        case PAWN -> new Pawn(piece.getColor());
                        case ROOK -> new Rook(piece.getColor());
                        case KNIGHT -> new Knight(piece.getColor());
                        case BISHOP -> new Bishop(piece.getColor());
                        case QUEEN -> new Queen(piece.getColor());
                        case KING -> new King(piece.getColor());
                    };
                    newPiece.setHasMoved(piece.hasMoved());
                    newBoard.grid[r][c] = newPiece;
                }
            }
        }
        if (enPassantTarget != null) {
            newBoard.enPassantTarget = new Position(enPassantTarget.getRow(), enPassantTarget.getCol());
        }
        return newBoard;
    }

    /**
     * Get all pieces of a given color.
     */
    public List<Position> getPiecePositions(PieceColor color) {
        List<Position> positions = new ArrayList<>();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = grid[r][c];
                if (piece != null && piece.getColor() == color) {
                    positions.add(new Position(r, c));
                }
            }
        }
        return positions;
    }
}

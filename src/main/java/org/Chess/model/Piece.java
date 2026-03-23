package org.Chess.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all chess pieces.
 */
public abstract class Piece {
    protected PieceColor color;
    protected PieceType type;
    protected boolean hasMoved;

    public Piece(PieceColor color, PieceType type) {
        this.color = color;
        this.type = type;
        this.hasMoved = false;
    }

    public PieceColor getColor() { return color; }
    public PieceType getType() { return type; }
    public boolean hasMoved() { return hasMoved; }
    public void setHasMoved(boolean hasMoved) { this.hasMoved = hasMoved; }

    /**
     * Returns raw candidate moves without checking if they leave the king in check.
     */
    public abstract List<Position> getRawMoves(Position position, Board board);

    /**
     * Returns the image file name for this piece.
     */
    public String getImageName() {
        String colorPrefix = (color == PieceColor.WHITE) ? "w" : "b";
        String pieceName = switch (type) {
            case PAWN -> "p";
            case ROOK -> "r";
            case KNIGHT -> "n";
            case BISHOP -> "b";
            case QUEEN -> "q";
            case KING -> "k";
        };
        return colorPrefix + pieceName + ".png";
    }

    /**
     * Helper to add moves in a direction (for sliding pieces: rook, bishop, queen).
     */
    protected List<Position> getSlidingMoves(Position position, Board board, int[][] directions) {
        List<Position> moves = new ArrayList<>();
        for (int[] dir : directions) {
            int r = position.getRow() + dir[0];
            int c = position.getCol() + dir[1];
            while (r >= 0 && r < 8 && c >= 0 && c < 8) {
                Position target = new Position(r, c);
                Piece targetPiece = board.getPiece(target);
                if (targetPiece == null) {
                    moves.add(target);
                } else {
                    if (targetPiece.getColor() != this.color) {
                        moves.add(target);
                    }
                    break;
                }
                r += dir[0];
                c += dir[1];
            }
        }
        return moves;
    }

    @Override
    public String toString() {
        return color.name().charAt(0) + "" + type.name().charAt(0);
    }
}

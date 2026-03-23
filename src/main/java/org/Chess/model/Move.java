package org.Chess.model;

/**
 * Represents a chess move from one position to another.
 */
public class Move {
    private final Position from;
    private final Position to;
    private final Piece movedPiece;
    private final Piece capturedPiece;
    private final boolean isCastling;
    private final boolean isEnPassant;
    private final PieceType promotionType;

    public Move(Position from, Position to, Piece movedPiece, Piece capturedPiece) {
        this(from, to, movedPiece, capturedPiece, false, false, null);
    }

    public Move(Position from, Position to, Piece movedPiece, Piece capturedPiece,
                boolean isCastling, boolean isEnPassant, PieceType promotionType) {
        this.from = from;
        this.to = to;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.isCastling = isCastling;
        this.isEnPassant = isEnPassant;
        this.promotionType = promotionType;
    }

    public Position getFrom() { return from; }
    public Position getTo() { return to; }
    public Piece getMovedPiece() { return movedPiece; }
    public Piece getCapturedPiece() { return capturedPiece; }
    public boolean isCastling() { return isCastling; }
    public boolean isEnPassant() { return isEnPassant; }
    public PieceType getPromotionType() { return promotionType; }

    @Override
    public String toString() {
        return from + " -> " + to;
    }
}

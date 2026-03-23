package org.Chess.model.pieces;

import org.Chess.model.*;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    private static final int[][] OFFSETS = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
    };

    public King(PieceColor color) {
        super(color, PieceType.KING);
    }

    @Override
    public List<Position> getRawMoves(Position position, Board board) {
        List<Position> moves = new ArrayList<>();

        // Normal king moves
        for (int[] offset : OFFSETS) {
            Position target = new Position(position.getRow() + offset[0], position.getCol() + offset[1]);
            if (target.isValid()) {
                Piece targetPiece = board.getPiece(target);
                if (targetPiece == null || targetPiece.getColor() != this.color) {
                    moves.add(target);
                }
            }
        }

        // Castling
        if (!hasMoved) {
            int row = position.getRow();

            // King-side castling
            Piece kRook = board.getPiece(new Position(row, 7));
            if (kRook != null && kRook.getType() == PieceType.ROOK && !kRook.hasMoved()) {
                if (board.getPiece(new Position(row, 5)) == null &&
                        board.getPiece(new Position(row, 6)) == null) {
                    if (!board.isSquareAttacked(new Position(row, 4), color.opposite()) &&
                            !board.isSquareAttacked(new Position(row, 5), color.opposite()) &&
                            !board.isSquareAttacked(new Position(row, 6), color.opposite())) {
                        moves.add(new Position(row, 6));
                    }
                }
            }

            // Queen-side castling
            Piece qRook = board.getPiece(new Position(row, 0));
            if (qRook != null && qRook.getType() == PieceType.ROOK && !qRook.hasMoved()) {
                if (board.getPiece(new Position(row, 1)) == null &&
                        board.getPiece(new Position(row, 2)) == null &&
                        board.getPiece(new Position(row, 3)) == null) {
                    if (!board.isSquareAttacked(new Position(row, 4), color.opposite()) &&
                            !board.isSquareAttacked(new Position(row, 3), color.opposite()) &&
                            !board.isSquareAttacked(new Position(row, 2), color.opposite())) {
                        moves.add(new Position(row, 2));
                    }
                }
            }
        }

        return moves;
    }
}

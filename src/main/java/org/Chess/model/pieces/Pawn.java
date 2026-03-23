package org.Chess.model.pieces;

import org.Chess.model.*;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    public Pawn(PieceColor color) {
        super(color, PieceType.PAWN);
    }

    @Override
    public List<Position> getRawMoves(Position position, Board board) {
        List<Position> moves = new ArrayList<>();
        int direction = (color == PieceColor.WHITE) ? -1 : 1;
        int startRow = (color == PieceColor.WHITE) ? 6 : 1;
        int row = position.getRow();
        int col = position.getCol();

        // Move forward one square
        Position oneForward = new Position(row + direction, col);
        if (oneForward.isValid() && board.getPiece(oneForward) == null) {
            moves.add(oneForward);

            // Move forward two squares from starting position
            if (row == startRow) {
                Position twoForward = new Position(row + 2 * direction, col);
                if (twoForward.isValid() && board.getPiece(twoForward) == null) {
                    moves.add(twoForward);
                }
            }
        }

        // Diagonal captures
        for (int dc : new int[]{-1, 1}) {
            Position diag = new Position(row + direction, col + dc);
            if (diag.isValid()) {
                Piece target = board.getPiece(diag);
                if (target != null && target.getColor() != this.color) {
                    moves.add(diag);
                }
            }
        }

        // En passant
        Position enPassantTarget = board.getEnPassantTarget();
        if (enPassantTarget != null) {
            if (enPassantTarget.getRow() == row + direction &&
                    Math.abs(enPassantTarget.getCol() - col) == 1) {
                moves.add(enPassantTarget);
            }
        }

        return moves;
    }
}

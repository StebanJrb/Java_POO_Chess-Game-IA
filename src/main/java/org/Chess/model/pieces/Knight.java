package org.Chess.model.pieces;

import org.Chess.model.*;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
    private static final int[][] OFFSETS = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            {1, -2}, {1, 2}, {2, -1}, {2, 1}
    };

    public Knight(PieceColor color) {
        super(color, PieceType.KNIGHT);
    }

    @Override
    public List<Position> getRawMoves(Position position, Board board) {
        List<Position> moves = new ArrayList<>();
        for (int[] offset : OFFSETS) {
            Position target = new Position(position.getRow() + offset[0], position.getCol() + offset[1]);
            if (target.isValid()) {
                Piece targetPiece = board.getPiece(target);
                if (targetPiece == null || targetPiece.getColor() != this.color) {
                    moves.add(target);
                }
            }
        }
        return moves;
    }
}

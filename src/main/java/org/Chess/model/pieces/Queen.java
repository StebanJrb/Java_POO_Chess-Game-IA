package org.Chess.model.pieces;

import org.Chess.model.*;

import java.util.List;
import java.util.ArrayList;

public class Queen extends Piece {
    private static final int[][] DIRECTIONS = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
    };

    public Queen(PieceColor color) {
        super(color, PieceType.QUEEN);
    }

    @Override
    public List<Position> getRawMoves(Position position, Board board) {
        return getSlidingMoves(position, board, DIRECTIONS);
    }
}

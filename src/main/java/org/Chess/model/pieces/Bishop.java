package org.Chess.model.pieces;

import org.Chess.model.*;

import java.util.List;

public class Bishop extends Piece {
    private static final int[][] DIRECTIONS = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

    public Bishop(PieceColor color) {
        super(color, PieceType.BISHOP);
    }

    @Override
    public List<Position> getRawMoves(Position position, Board board) {
        return getSlidingMoves(position, board, DIRECTIONS);
    }
}

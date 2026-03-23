package org.Chess.model.pieces;

import org.Chess.model.*;

import java.util.List;

public class Rook extends Piece {
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    public Rook(PieceColor color) {
        super(color, PieceType.ROOK);
    }

    @Override
    public List<Position> getRawMoves(Position position, Board board) {
        return getSlidingMoves(position, board, DIRECTIONS);
    }
}

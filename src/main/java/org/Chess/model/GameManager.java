package org.Chess.model;

import org.Chess.model.pieces.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controls the game state: turns, move validation, check/checkmate, captured pieces.
 */
public class GameManager {

    public enum GameState {
        PLAYING, CHECK, CHECKMATE, STALEMATE
    }

    private final Board board;
    private PieceColor currentTurn;
    private GameState gameState;
    private final List<Piece> whiteCaptured; // pieces captured BY white (i.e., black pieces)
    private final List<Piece> blackCaptured; // pieces captured BY black (i.e., white pieces)
    private final List<Move> moveHistory;
    private boolean isVsAI;

    public GameManager() {
        board = new Board();
        whiteCaptured = new ArrayList<>();
        blackCaptured = new ArrayList<>();
        moveHistory = new ArrayList<>();
        currentTurn = PieceColor.WHITE;
        gameState = GameState.PLAYING;
        isVsAI = false;
    }

    public void startNewGame(boolean vsAI) {
        board.setupInitialPosition();
        whiteCaptured.clear();
        blackCaptured.clear();
        moveHistory.clear();
        currentTurn = PieceColor.WHITE;
        gameState = GameState.PLAYING;
        this.isVsAI = vsAI;
    }

    public Board getBoard() { return board; }
    public PieceColor getCurrentTurn() { return currentTurn; }
    public GameState getGameState() { return gameState; }
    public List<Piece> getWhiteCaptured() { return whiteCaptured; }
    public List<Piece> getBlackCaptured() { return blackCaptured; }
    public List<Move> getMoveHistory() { return moveHistory; }
    public boolean isVsAI() { return isVsAI; }

    /**
     * Get legal moves for a piece at a given position (filtered to avoid leaving king in check).
     */
    public List<Position> getLegalMoves(Position from) {
        Piece piece = board.getPiece(from);
        if (piece == null || piece.getColor() != currentTurn) {
            return new ArrayList<>();
        }

        List<Position> rawMoves = piece.getRawMoves(from, board);
        List<Position> legalMoves = new ArrayList<>();

        for (Position to : rawMoves) {
            if (isMoveLegal(from, to, piece)) {
                legalMoves.add(to);
            }
        }

        return legalMoves;
    }

    /**
     * Check if a move would leave our own king in check.
     */
    private boolean isMoveLegal(Position from, Position to, Piece piece) {
        // Simulate the move on a copy of the board
        Board simBoard = board.copy();
        Piece simPiece = simBoard.getPiece(from);

        // Handle en passant capture on the simulation
        if (simPiece.getType() == PieceType.PAWN && to.equals(board.getEnPassantTarget())) {
            int capturedRow = from.getRow();
            int capturedCol = to.getCol();
            simBoard.removePiece(new Position(capturedRow, capturedCol));
        }

        // Handle castling on the simulation
        if (simPiece.getType() == PieceType.KING && Math.abs(to.getCol() - from.getCol()) == 2) {
            int row = from.getRow();
            if (to.getCol() == 6) { // King-side
                simBoard.setPiece(new Position(row, 5), simBoard.getPiece(new Position(row, 7)));
                simBoard.removePiece(new Position(row, 7));
            } else if (to.getCol() == 2) { // Queen-side
                simBoard.setPiece(new Position(row, 3), simBoard.getPiece(new Position(row, 0)));
                simBoard.removePiece(new Position(row, 0));
            }
        }

        simBoard.setPiece(to, simPiece);
        simBoard.removePiece(from);

        // Check if our king is in check after the move
        return !simBoard.isInCheck(piece.getColor());
    }

    /**
     * Execute a move on the board. Returns the Move object, or null if invalid.
     */
    public Move makeMove(Position from, Position to) {
        return makeMove(from, to, null);
    }

    public Move makeMove(Position from, Position to, PieceType promotionType) {
        Piece piece = board.getPiece(from);
        if (piece == null || piece.getColor() != currentTurn) return null;

        List<Position> legalMoves = getLegalMoves(from);
        boolean isLegal = false;
        for (Position lm : legalMoves) {
            if (lm.equals(to)) { isLegal = true; break; }
        }
        if (!isLegal) return null;

        Piece captured = board.getPiece(to);
        boolean isCastling = false;
        boolean isEnPassant = false;

        // En passant capture
        if (piece.getType() == PieceType.PAWN && to.equals(board.getEnPassantTarget())) {
            isEnPassant = true;
            Position capturedPawnPos = new Position(from.getRow(), to.getCol());
            captured = board.getPiece(capturedPawnPos);
            board.removePiece(capturedPawnPos);
        }

        // Castling
        if (piece.getType() == PieceType.KING && Math.abs(to.getCol() - from.getCol()) == 2) {
            isCastling = true;
            int row = from.getRow();
            if (to.getCol() == 6) { // King-side
                Piece rook = board.getPiece(new Position(row, 7));
                board.removePiece(new Position(row, 7));
                board.setPiece(new Position(row, 5), rook);
                rook.setHasMoved(true);
            } else if (to.getCol() == 2) { // Queen-side
                Piece rook = board.getPiece(new Position(row, 0));
                board.removePiece(new Position(row, 0));
                board.setPiece(new Position(row, 3), rook);
                rook.setHasMoved(true);
            }
        }

        // Set en passant target for next turn
        if (piece.getType() == PieceType.PAWN && Math.abs(to.getRow() - from.getRow()) == 2) {
            int epRow = (from.getRow() + to.getRow()) / 2;
            board.setEnPassantTarget(new Position(epRow, from.getCol()));
        } else {
            board.setEnPassantTarget(null);
        }

        // Execute move
        board.removePiece(from);
        board.setPiece(to, piece);
        piece.setHasMoved(true);

        // Pawn promotion
        if (piece.getType() == PieceType.PAWN) {
            int promotionRow = (piece.getColor() == PieceColor.WHITE) ? 0 : 7;
            if (to.getRow() == promotionRow) {
                if (promotionType == null) promotionType = PieceType.QUEEN;
                Piece promoted = switch (promotionType) {
                    case QUEEN -> new Queen(piece.getColor());
                    case ROOK -> new Rook(piece.getColor());
                    case BISHOP -> new Bishop(piece.getColor());
                    case KNIGHT -> new Knight(piece.getColor());
                    default -> new Queen(piece.getColor());
                };
                promoted.setHasMoved(true);
                board.setPiece(to, promoted);
            }
        }

        // Record capture
        if (captured != null) {
            if (currentTurn == PieceColor.WHITE) {
                whiteCaptured.add(captured);
            } else {
                blackCaptured.add(captured);
            }
        }

        Move move = new Move(from, to, piece, captured, isCastling, isEnPassant, promotionType);
        moveHistory.add(move);

        // Switch turns
        currentTurn = currentTurn.opposite();

        // Update game state
        updateGameState();

        return move;
    }

    /**
     * Update game state after a move: check, checkmate, or stalemate.
     */
    private void updateGameState() {
        boolean inCheck = board.isInCheck(currentTurn);
        boolean hasLegalMoves = hasAnyLegalMoves(currentTurn);

        if (inCheck && !hasLegalMoves) {
            gameState = GameState.CHECKMATE;
        } else if (!inCheck && !hasLegalMoves) {
            gameState = GameState.STALEMATE;
        } else if (inCheck) {
            gameState = GameState.CHECK;
        } else {
            gameState = GameState.PLAYING;
        }
    }

    /**
     * Check if a color has any legal moves at all.
     */
    private boolean hasAnyLegalMoves(PieceColor color) {
        List<Position> positions = board.getPiecePositions(color);
        for (Position pos : positions) {
            Piece piece = board.getPiece(pos);
            List<Position> rawMoves = piece.getRawMoves(pos, board);
            for (Position to : rawMoves) {
                if (isMoveLegal(pos, to, piece)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if pawn promotion is needed for a move to a given position.
     */
    public boolean isPromotionMove(Position from, Position to) {
        Piece piece = board.getPiece(from);
        if (piece == null || piece.getType() != PieceType.PAWN) return false;
        int promotionRow = (piece.getColor() == PieceColor.WHITE) ? 0 : 7;
        return to.getRow() == promotionRow;
    }
}

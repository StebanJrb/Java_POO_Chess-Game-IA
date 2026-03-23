package org.Chess.gui;

import org.Chess.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * The main chess board drawing panel. Handles piece rendering and click interaction.
 */
public class BoardPanel extends JPanel implements MouseListener {

    public interface BoardClickListener {
        void onSquareClicked(Position position);
    }

    private final GameManager gameManager;
    private final BoardClickListener clickListener;
    private Position selectedPosition;
    private List<Position> highlightedMoves;

    // Colors
    private static final Color LIGHT_SQUARE = new Color(240, 217, 181);
    private static final Color DARK_SQUARE = new Color(181, 136, 99);
    private static final Color SELECTED_LIGHT = new Color(247, 247, 105, 180);
    private static final Color SELECTED_DARK = new Color(218, 195, 71, 180);
    private static final Color MOVE_DOT = new Color(0, 0, 0, 60);
    private static final Color CAPTURE_RING = new Color(0, 0, 0, 60);
    private static final Color CHECK_COLOR = new Color(235, 64, 52, 160);
    private static final Color LAST_MOVE_LIGHT = new Color(205, 210, 106, 120);
    private static final Color LAST_MOVE_DARK = new Color(170, 162, 58, 120);
    private static final Color BOARD_BORDER = new Color(60, 50, 40);
    private static final Color COORD_COLOR = new Color(120, 100, 80);

    private static final int BOARD_MARGIN = 28;

    public BoardPanel(GameManager gameManager, BoardClickListener clickListener) {
        this.gameManager = gameManager;
        this.clickListener = clickListener;
        this.highlightedMoves = List.of();
        addMouseListener(this);
        setPreferredSize(new Dimension(648, 648));
        setBackground(BOARD_BORDER);
    }

    public void setSelectedPosition(Position pos) {
        this.selectedPosition = pos;
    }

    public void setHighlightedMoves(List<Position> moves) {
        this.highlightedMoves = (moves != null) ? moves : List.of();
    }

    private int getSquareSize() {
        int available = Math.min(getWidth(), getHeight()) - 2 * BOARD_MARGIN;
        return available / 8;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int sqSize = getSquareSize();
        int boardSize = sqSize * 8;

        // Background
        g2.setColor(BOARD_BORDER);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Draw coordinates
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.setColor(COORD_COLOR);
        for (int c = 0; c < 8; c++) {
            String col = String.valueOf((char) ('a' + c));
            FontMetrics fm = g2.getFontMetrics();
            int x = BOARD_MARGIN + c * sqSize + sqSize / 2 - fm.stringWidth(col) / 2;
            g2.drawString(col, x, BOARD_MARGIN + boardSize + 18);
            g2.drawString(col, x, BOARD_MARGIN - 8);
        }
        for (int r = 0; r < 8; r++) {
            String row = String.valueOf(8 - r);
            FontMetrics fm = g2.getFontMetrics();
            int y = BOARD_MARGIN + r * sqSize + sqSize / 2 + fm.getAscent() / 2 - 2;
            g2.drawString(row, BOARD_MARGIN - 18, y);
            g2.drawString(row, BOARD_MARGIN + boardSize + 8, y);
        }

        // Get last move for highlighting
        Move lastMove = null;
        List<Move> history = gameManager.getMoveHistory();
        if (!history.isEmpty()) {
            lastMove = history.get(history.size() - 1);
        }

        // Draw board squares
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                int x = BOARD_MARGIN + c * sqSize;
                int y = BOARD_MARGIN + r * sqSize;
                boolean isLight = (r + c) % 2 == 0;
                Position pos = new Position(r, c);

                // Base square color
                Color squareColor = isLight ? LIGHT_SQUARE : DARK_SQUARE;
                g2.setColor(squareColor);
                g2.fillRect(x, y, sqSize, sqSize);

                // Last move highlight
                if (lastMove != null &&
                        (pos.equals(lastMove.getFrom()) || pos.equals(lastMove.getTo()))) {
                    g2.setColor(isLight ? LAST_MOVE_LIGHT : LAST_MOVE_DARK);
                    g2.fillRect(x, y, sqSize, sqSize);
                }

                // Selected square highlight
                if (selectedPosition != null && pos.equals(selectedPosition)) {
                    g2.setColor(isLight ? SELECTED_LIGHT : SELECTED_DARK);
                    g2.fillRect(x, y, sqSize, sqSize);
                }

                // Check highlight on king
                Piece piece = gameManager.getBoard().getPiece(pos);
                if (piece != null && piece.getType() == PieceType.KING &&
                        piece.getColor() == gameManager.getCurrentTurn() &&
                        (gameManager.getGameState() == GameManager.GameState.CHECK ||
                                gameManager.getGameState() == GameManager.GameState.CHECKMATE)) {
                    g2.setColor(CHECK_COLOR);
                    g2.fillRect(x, y, sqSize, sqSize);
                }
            }
        }

        // Draw pieces
        Board board = gameManager.getBoard();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.getPiece(new Position(r, c));
                if (piece != null) {
                    int x = BOARD_MARGIN + c * sqSize;
                    int y = BOARD_MARGIN + r * sqSize;
                    Image img = PieceImageLoader.getScaledImage(piece.getType(), piece.getColor(), (int)(sqSize * 0.85));
                    if (img != null) {
                        int offset = (int)(sqSize * 0.075);
                        g2.drawImage(img, x + offset, y + offset, null);
                    }
                }
            }
        }

        // Draw valid move indicators
        for (Position move : highlightedMoves) {
            int x = BOARD_MARGIN + move.getCol() * sqSize;
            int y = BOARD_MARGIN + move.getRow() * sqSize;

            Piece targetPiece = board.getPiece(move);
            if (targetPiece != null) {
                // Capture indicator: ring around the square
                g2.setColor(CAPTURE_RING);
                g2.setStroke(new BasicStroke(3.5f));
                int margin = 3;
                g2.draw(new Ellipse2D.Float(x + margin, y + margin, sqSize - 2 * margin, sqSize - 2 * margin));
                g2.setStroke(new BasicStroke(1f));
            } else {
                // Move indicator: small dot in center
                g2.setColor(MOVE_DOT);
                int dotSize = sqSize / 4;
                int dotX = x + (sqSize - dotSize) / 2;
                int dotY = y + (sqSize - dotSize) / 2;
                g2.fill(new Ellipse2D.Float(dotX, dotY, dotSize, dotSize));
            }
        }

        // Draw en passant indicator (it's an empty square but results in capture)
        Position enPassant = board.getEnPassantTarget();
        if (enPassant != null && highlightedMoves.contains(enPassant)) {
            int x = BOARD_MARGIN + enPassant.getCol() * sqSize;
            int y = BOARD_MARGIN + enPassant.getRow() * sqSize;
            g2.setColor(CAPTURE_RING);
            g2.setStroke(new BasicStroke(3.5f));
            int margin = 3;
            g2.draw(new Ellipse2D.Float(x + margin, y + margin, sqSize - 2 * margin, sqSize - 2 * margin));
            g2.setStroke(new BasicStroke(1f));
        }

        g2.dispose();
    }

    // MouseListener
    @Override
    public void mouseClicked(MouseEvent e) {
        int sqSize = getSquareSize();
        int col = (e.getX() - BOARD_MARGIN) / sqSize;
        int row = (e.getY() - BOARD_MARGIN) / sqSize;
        Position clickedPos = new Position(row, col);
        if (clickedPos.isValid()) {
            clickListener.onSquareClicked(clickedPos);
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}

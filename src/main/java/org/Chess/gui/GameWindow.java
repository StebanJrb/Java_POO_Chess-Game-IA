package org.Chess.gui;

import org.Chess.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The main game window that ties all components together:
 * board, captured pieces panels, and status display.
 */
public class GameWindow extends JPanel implements BoardPanel.BoardClickListener {

    private final GameManager gameManager;
    private final BoardPanel boardPanel;
    private final CapturedPiecesPanel whiteCapturedPanel;
    private final CapturedPiecesPanel blackCapturedPanel;
    private final JLabel statusLabel;
    private final JLabel turnIndicator;
    private final JButton newGameButton;
    private final JButton menuButton;
    private final JFrame parentFrame;

    private Position selectedPosition;

    // Colors
    private static final Color BG = new Color(28, 28, 32);
    private static final Color STATUS_BG = new Color(38, 40, 52);
    private static final Color ACCENT_GOLD = new Color(218, 165, 32);
    private static final Color TEXT_PRIMARY = new Color(240, 240, 245);
    private static final Color TEXT_SECONDARY = new Color(180, 180, 195);
    private static final Color BTN_BG = new Color(55, 58, 72);
    private static final Color BTN_HOVER = new Color(75, 78, 92);

    public interface GameWindowListener {
        void onReturnToMenu();
    }

    private GameWindowListener windowListener;

    public GameWindow(JFrame parentFrame, GameManager gameManager) {
        this.parentFrame = parentFrame;
        this.gameManager = gameManager;
        this.selectedPosition = null;

        setLayout(new BorderLayout(0, 0));
        setBackground(BG);

        // ========== TOP: Status bar ==========
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(STATUS_BG);
        topBar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        turnIndicator = new JLabel("● Turno: Blancas");
        turnIndicator.setFont(new Font("SansSerif", Font.BOLD, 16));
        turnIndicator.setForeground(TEXT_PRIMARY);
        topBar.add(turnIndicator, BorderLayout.WEST);

        statusLabel = new JLabel("¡Buena suerte!");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        statusLabel.setForeground(TEXT_SECONDARY);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topBar.add(statusLabel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        newGameButton = createSmallButton("Nueva Partida");
        newGameButton.addActionListener(e -> {
            gameManager.startNewGame(gameManager.isVsAI());
            selectedPosition = null;
            refreshAll();
        });
        btnPanel.add(newGameButton);

        menuButton = createSmallButton("Menú");
        menuButton.addActionListener(e -> {
            if (windowListener != null) windowListener.onReturnToMenu();
        });
        btnPanel.add(menuButton);

        topBar.add(btnPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // ========== CENTER: Board ==========
        boardPanel = new BoardPanel(gameManager, this);
        JPanel boardWrapper = new JPanel(new GridBagLayout());
        boardWrapper.setBackground(BG);
        boardWrapper.add(boardPanel);
        add(boardWrapper, BorderLayout.CENTER);

        // ========== LEFT: Black captured pieces ==========
        blackCapturedPanel = new CapturedPiecesPanel(PieceColor.BLACK);
        JPanel leftWrapper = new JPanel(new GridBagLayout());
        leftWrapper.setBackground(BG);
        leftWrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
        leftWrapper.add(blackCapturedPanel);
        add(leftWrapper, BorderLayout.WEST);

        // ========== RIGHT: White captured pieces ==========
        whiteCapturedPanel = new CapturedPiecesPanel(PieceColor.WHITE);
        JPanel rightWrapper = new JPanel(new GridBagLayout());
        rightWrapper.setBackground(BG);
        rightWrapper.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
        rightWrapper.add(whiteCapturedPanel);
        add(rightWrapper, BorderLayout.EAST);

        // Initial refresh
        refreshAll();
    }

    public void setWindowListener(GameWindowListener listener) {
        this.windowListener = listener;
    }

    @Override
    public void onSquareClicked(Position position) {
        if (gameManager.getGameState() == GameManager.GameState.CHECKMATE ||
                gameManager.getGameState() == GameManager.GameState.STALEMATE) {
            return; // Game is over
        }

        Piece clickedPiece = gameManager.getBoard().getPiece(position);

        if (selectedPosition == null) {
            // First click: select a piece
            if (clickedPiece != null && clickedPiece.getColor() == gameManager.getCurrentTurn()) {
                selectedPosition = position;
                List<Position> moves = gameManager.getLegalMoves(position);
                boardPanel.setSelectedPosition(position);
                boardPanel.setHighlightedMoves(moves);
                boardPanel.repaint();
            }
        } else {
            // Second click
            if (position.equals(selectedPosition)) {
                // Deselect
                clearSelection();
                return;
            }

            // Check if clicking another piece of the same color
            if (clickedPiece != null && clickedPiece.getColor() == gameManager.getCurrentTurn()) {
                // Switch selection to this piece
                selectedPosition = position;
                List<Position> moves = gameManager.getLegalMoves(position);
                boardPanel.setSelectedPosition(position);
                boardPanel.setHighlightedMoves(moves);
                boardPanel.repaint();
                return;
            }

            // Try to make the move
            List<Position> legalMoves = gameManager.getLegalMoves(selectedPosition);
            boolean isLegalTarget = false;
            for (Position lm : legalMoves) {
                if (lm.equals(position)) { isLegalTarget = true; break; }
            }

            if (isLegalTarget) {
                // Check for promotion
                PieceType promotionType = null;
                if (gameManager.isPromotionMove(selectedPosition, position)) {
                    promotionType = PromotionDialog.showDialog(parentFrame, gameManager.getCurrentTurn());
                }

                Move move = gameManager.makeMove(selectedPosition, position, promotionType);
                if (move != null) {
                    clearSelection();
                    refreshAll();
                    return;
                }
            }

            clearSelection();
        }
    }

    private void clearSelection() {
        selectedPosition = null;
        boardPanel.setSelectedPosition(null);
        boardPanel.setHighlightedMoves(List.of());
        boardPanel.repaint();
    }

    private void refreshAll() {
        // Update captured pieces
        whiteCapturedPanel.setCapturedPieces(gameManager.getWhiteCaptured());
        blackCapturedPanel.setCapturedPieces(gameManager.getBlackCaptured());

        // Update turn indicator
        String turnText = (gameManager.getCurrentTurn() == PieceColor.WHITE) ? "● Turno: Blancas" : "● Turno: Negras";
        turnIndicator.setText(turnText);
        turnIndicator.setForeground(
                gameManager.getCurrentTurn() == PieceColor.WHITE ? TEXT_PRIMARY : new Color(160, 160, 175)
        );

        // Update status
        switch (gameManager.getGameState()) {
            case PLAYING:
                statusLabel.setText("¡Buena suerte!");
                statusLabel.setForeground(TEXT_SECONDARY);
                break;
            case CHECK:
                statusLabel.setText("¡Jaque!");
                statusLabel.setForeground(new Color(235, 150, 50));
                break;
            case CHECKMATE:
                PieceColor winner = gameManager.getCurrentTurn().opposite();
                String winnerName = (winner == PieceColor.WHITE) ? "Blancas" : "Negras";
                statusLabel.setText("¡Jaque Mate! Ganan las " + winnerName + " 🏆");
                statusLabel.setForeground(ACCENT_GOLD);
                break;
            case STALEMATE:
                statusLabel.setText("Tablas por ahogado");
                statusLabel.setForeground(TEXT_SECONDARY);
                break;
        }

        boardPanel.repaint();
    }

    private JButton createSmallButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setForeground(TEXT_PRIMARY);
        btn.setBackground(BTN_BG);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 72, 84), 1),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(BTN_HOVER);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(BTN_BG);
            }
        });
        return btn;
    }
}

package org.Chess.gui;

import org.Chess.model.Piece;
import org.Chess.model.PieceColor;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel that displays captured pieces for one side.
 */
public class CapturedPiecesPanel extends JPanel {

    private final PieceColor captorColor;
    private List<Piece> capturedPieces;

    private static final Color BG_COLOR = new Color(38, 40, 52);
    private static final Color BORDER_COLOR = new Color(60, 62, 74);
    private static final Color LABEL_COLOR = new Color(180, 180, 195);
    private static final Color TITLE_COLOR = new Color(218, 165, 32);

    public CapturedPiecesPanel(PieceColor captorColor) {
        this.captorColor = captorColor;
        this.capturedPieces = List.of();
        setPreferredSize(new Dimension(180, 300));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
    }

    public void setCapturedPieces(List<Piece> pieces) {
        this.capturedPieces = pieces;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Background
        g2.setColor(BG_COLOR);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Title
        String title = (captorColor == PieceColor.WHITE) ? "♔ Blancas capturan" : "♚ Negras capturan";
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        g2.setColor(TITLE_COLOR);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, (getWidth() - fm.stringWidth(title)) / 2, 25);

        // Separator
        g2.setColor(BORDER_COLOR);
        g2.drawLine(10, 35, getWidth() - 10, 35);

        // Draw captured pieces
        if (capturedPieces.isEmpty()) {
            g2.setFont(new Font("SansSerif", Font.ITALIC, 12));
            g2.setColor(LABEL_COLOR);
            String msg = "Sin capturas";
            fm = g2.getFontMetrics();
            g2.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, 65);
        } else {
            int pieceSize = 32;
            int padding = 6;
            int startX = 10;
            int startY = 45;
            int maxPerRow = (getWidth() - 20) / (pieceSize + padding);
            int col = 0;
            int row = 0;

            for (Piece piece : capturedPieces) {
                int x = startX + col * (pieceSize + padding);
                int y = startY + row * (pieceSize + padding);
                Image img = PieceImageLoader.getScaledImage(piece.getType(), piece.getColor(), pieceSize);
                if (img != null) {
                    g2.drawImage(img, x, y, null);
                }
                col++;
                if (col >= maxPerRow) {
                    col = 0;
                    row++;
                }
            }
        }

        g2.dispose();
    }
}

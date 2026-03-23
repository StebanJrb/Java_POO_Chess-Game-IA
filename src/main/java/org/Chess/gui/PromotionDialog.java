package org.Chess.gui;

import org.Chess.model.PieceColor;
import org.Chess.model.PieceType;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Dialog for pawn promotion - lets the player choose which piece to promote to.
 */
public class PromotionDialog extends JDialog {

    private PieceType selectedType = PieceType.QUEEN;

    private static final Color BG = new Color(38, 40, 52);
    private static final Color TITLE_COLOR = new Color(218, 165, 32);
    private static final Color BTN_BG = new Color(55, 58, 72);
    private static final Color BTN_HOVER = new Color(75, 78, 92);
    private static final Color BTN_BORDER = new Color(80, 82, 96);

    public PromotionDialog(JFrame parent, PieceColor color) {
        super(parent, "Promoción de Peón", true);
        setUndecorated(true);
        setSize(320, 140);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("Selecciona pieza para promoción", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setForeground(TITLE_COLOR);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Piece options
        JPanel piecePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 5));
        piecePanel.setOpaque(false);

        PieceType[] options = {PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT};
        for (PieceType type : options) {
            JButton btn = new JButton() {
                private boolean hovered = false;

                {
                    addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseEntered(java.awt.event.MouseEvent e) {
                            hovered = true;
                            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                            repaint();
                        }

                        @Override
                        public void mouseExited(java.awt.event.MouseEvent e) {
                            hovered = false;
                            setCursor(Cursor.getDefaultCursor());
                            repaint();
                        }
                    });
                }

                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                    g2.setColor(hovered ? BTN_HOVER : BTN_BG);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                    g2.setColor(BTN_BORDER);
                    g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));

                    Image img = PieceImageLoader.getScaledImage(type, color, 48);
                    if (img != null) {
                        int x = (getWidth() - 48) / 2;
                        int y = (getHeight() - 48) / 2;
                        g2.drawImage(img, x, y, null);
                    }
                    g2.dispose();
                }
            };
            btn.setPreferredSize(new Dimension(60, 60));
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.addActionListener(e -> {
                selectedType = type;
                dispose();
            });
            piecePanel.add(btn);
        }

        add(piecePanel, BorderLayout.CENTER);

        // Rounded border effect
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(80, 82, 96), 2));
    }

    public PieceType getSelectedType() {
        return selectedType;
    }

    public static PieceType showDialog(JFrame parent, PieceColor color) {
        PromotionDialog dialog = new PromotionDialog(parent, color);
        dialog.setVisible(true);
        return dialog.getSelectedType();
    }
}

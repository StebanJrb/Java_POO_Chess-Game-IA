package org.Chess.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Main menu panel with options to play vs Player or vs AI.
 */
public class MainMenuPanel extends JPanel {

    public interface MenuListener {
        void onStartGame(boolean vsAI);
    }

    private final MenuListener listener;
    private final JButton pvpButton;
    private final JButton aiButton;
    private float alpha = 0f;

    // Colors
    private static final Color BG_DARK = new Color(28, 28, 32);
    private static final Color BG_GRADIENT_TOP = new Color(40, 42, 54);
    private static final Color BG_GRADIENT_BOTTOM = new Color(20, 20, 28);
    private static final Color ACCENT_GOLD = new Color(218, 165, 32);
    private static final Color ACCENT_GOLD_DARK = new Color(180, 130, 20);
    private static final Color CARD_BG = new Color(44, 46, 58, 220);
    private static final Color TEXT_PRIMARY = new Color(240, 240, 245);
    private static final Color TEXT_SECONDARY = new Color(180, 180, 195);
    private static final Color BTN_PVP = new Color(76, 139, 245);
    private static final Color BTN_PVP_HOVER = new Color(96, 159, 255);
    private static final Color BTN_AI = new Color(138, 92, 246);
    private static final Color BTN_AI_HOVER = new Color(158, 112, 255);

    public MainMenuPanel(MenuListener listener) {
        this.listener = listener;
        setLayout(new GridBagLayout());
        setBackground(BG_DARK);
        setPreferredSize(new Dimension(800, 700));

        // Main container panel
        JPanel container = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30));
                g2.setColor(new Color(255, 255, 255, 15));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
            }
        };
        container.setOpaque(false);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createEmptyBorder(50, 60, 50, 60));

        // Chess icon (Unicode)
        JLabel iconLabel = new JLabel("♚");
        iconLabel.setFont(new Font("Serif", Font.PLAIN, 80));
        iconLabel.setForeground(ACCENT_GOLD);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(iconLabel);

        container.add(Box.createRigidArea(new Dimension(0, 10)));

        // Title
        JLabel titleLabel = new JLabel("CHESS");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(titleLabel);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Selecciona un modo de juego");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(subtitleLabel);

        container.add(Box.createRigidArea(new Dimension(0, 40)));

        // Separator line
        JSeparator sep = new JSeparator() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, new Color(218, 165, 32, 0), getWidth() / 2f, 0, ACCENT_GOLD);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth() / 2, 2);
                GradientPaint gp2 = new GradientPaint(getWidth() / 2f, 0, ACCENT_GOLD, getWidth(), 0, new Color(218, 165, 32, 0));
                g2.setPaint(gp2);
                g2.fillRect(getWidth() / 2, 0, getWidth() / 2, 2);
                g2.dispose();
            }
        };
        sep.setPreferredSize(new Dimension(300, 2));
        sep.setMaximumSize(new Dimension(300, 2));
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(sep);

        container.add(Box.createRigidArea(new Dimension(0, 40)));

        // Player vs Player button
        pvpButton = createStyledButton("♟  Jugador vs Jugador", BTN_PVP, BTN_PVP_HOVER);
        pvpButton.addActionListener(e -> listener.onStartGame(false));
        pvpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(pvpButton);

        container.add(Box.createRigidArea(new Dimension(0, 20)));

        // Player vs AI button
        aiButton = createStyledButton("🤖  Jugador vs IA", BTN_AI, BTN_AI_HOVER);
        aiButton.addActionListener(e -> listener.onStartGame(true));
        aiButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(aiButton);

        container.add(Box.createRigidArea(new Dimension(0, 30)));

        // Footer
        JLabel footerLabel = new JLabel("Desarrollado por Steban");
        footerLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        footerLabel.setForeground(new Color(120, 120, 140));
        footerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(footerLabel);

        add(container);
    }

    private JButton createStyledButton(String text, Color baseColor, Color hoverColor) {
        JButton button = new JButton(text) {
            private boolean hovered = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
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

                Color bg = hovered ? hoverColor : baseColor;
                // Shadow
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fill(new RoundRectangle2D.Float(3, 3, getWidth() - 3, getHeight() - 3, 16, 16));
                // Button fill
                GradientPaint gp = new GradientPaint(0, 0, bg, 0, getHeight(), bg.darker());
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 3, 16, 16));

                // Text
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2 - 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };

        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(320, 55));
        button.setMaximumSize(new Dimension(320, 55));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);

        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background gradient
        GradientPaint bgGrad = new GradientPaint(0, 0, BG_GRADIENT_TOP, 0, getHeight(), BG_GRADIENT_BOTTOM);
        g2.setPaint(bgGrad);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Subtle decorative circles
        g2.setColor(new Color(218, 165, 32, 10));
        g2.fillOval(-100, -100, 400, 400);
        g2.fillOval(getWidth() - 200, getHeight() - 200, 400, 400);

        // Subtle grid pattern
        g2.setColor(new Color(255, 255, 255, 5));
        for (int x = 0; x < getWidth(); x += 60) {
            for (int y = 0; y < getHeight(); y += 60) {
                if ((x / 60 + y / 60) % 2 == 0) {
                    g2.fillRect(x, y, 60, 60);
                }
            }
        }

        g2.dispose();
    }
}

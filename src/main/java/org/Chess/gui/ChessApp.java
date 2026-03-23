package org.Chess.gui;

import org.Chess.model.GameManager;

import javax.swing.*;
import java.awt.*;

/**
 * Main application frame that manages the menu and game views.
 */
public class ChessApp extends JFrame implements MainMenuPanel.MenuListener, GameWindow.GameWindowListener {

    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final GameManager gameManager;
    private GameWindow gameWindow;

    public ChessApp() {
        super("♚ Chess - Ajedrez");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setMinimumSize(new Dimension(1050, 750));

        gameManager = new GameManager();
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Menu panel
        MainMenuPanel menuPanel = new MainMenuPanel(this);
        mainPanel.add(menuPanel, "MENU");

        // Game panel
        gameWindow = new GameWindow(this, gameManager);
        gameWindow.setWindowListener(this);
        mainPanel.add(gameWindow, "GAME");

        add(mainPanel);
        cardLayout.show(mainPanel, "MENU");

        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void onStartGame(boolean vsAI) {
        gameManager.startNewGame(vsAI);
        // Recreate game window for fresh state
        mainPanel.remove(gameWindow);
        gameWindow = new GameWindow(this, gameManager);
        gameWindow.setWindowListener(this);
        mainPanel.add(gameWindow, "GAME");
        cardLayout.show(mainPanel, "GAME");
    }

    @Override
    public void onReturnToMenu() {
        cardLayout.show(mainPanel, "MENU");
    }
}

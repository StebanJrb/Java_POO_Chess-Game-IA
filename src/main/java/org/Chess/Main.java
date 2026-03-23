package org.Chess;

import org.Chess.gui.ChessApp;
import org.Chess.gui.PieceImageLoader;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default LAF
        }

        // Start GUI on EDT
        SwingUtilities.invokeLater(() -> {
            ChessApp app = new ChessApp();
            app.setVisible(true);

            // Load images in background after window is visible
            new Thread(() -> {
                PieceImageLoader.loadImages();
                app.repaint(); // Force repaint when images are ready
            }).start();
        });
    }
}
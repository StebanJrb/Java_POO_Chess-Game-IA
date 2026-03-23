package org.Chess.gui;

import org.Chess.model.PieceColor;
import org.Chess.model.PieceType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads and caches chess piece images from the local src/pieces folder.
 */
public class PieceImageLoader {
    private static final Map<String, Image> imageCache = new HashMap<>();
    private static boolean loaded = false;

    /**
     * Load all piece images from the local pieces folder.
     */
    public static void loadImages() {
        if (loaded) return;

        // Try to find the pieces folder relative to the project root
        String[] possiblePaths = {
                "src/pieces",
                "src\\pieces",
                "../src/pieces",
                "../src\\pieces"
        };

        File piecesDir = null;
        for (String path : possiblePaths) {
            File dir = new File(path);
            if (dir.exists() && dir.isDirectory()) {
                piecesDir = dir;
                break;
            }
        }

        // Fallback: try to locate via classpath resource or absolute known path
        if (piecesDir == null) {
            // Use the known project structure
            String userDir = System.getProperty("user.dir");
            File dir = new File(userDir, "src/pieces");
            if (dir.exists()) {
                piecesDir = dir;
            }
        }

        String[] colors = {"White", "Black"};
        String[] pieceNames = {"King", "Queen", "Rook", "Bishop", "Knight", "Pawn"};
        String[] keys =       {"k",    "q",     "r",    "b",      "n",      "p"};

        for (String color : colors) {
            String prefix = color.equals("White") ? "w" : "b";
            for (int i = 0; i < pieceNames.length; i++) {
                String fileName = color + " " + pieceNames[i] + ".png";
                String cacheKey = prefix + keys[i];

                if (piecesDir != null) {
                    File imgFile = new File(piecesDir, fileName);
                    if (imgFile.exists()) {
                        try {
                            BufferedImage img = ImageIO.read(imgFile);
                            if (img != null) {
                                imageCache.put(cacheKey, img);
                                continue;
                            }
                        } catch (IOException e) {
                            System.err.println("Failed to load piece image: " + fileName + " - " + e.getMessage());
                        }
                    } else {
                        System.err.println("Piece image not found: " + imgFile.getAbsolutePath());
                    }
                }

                // Fallback: generate a simple image
                imageCache.put(cacheKey, createFallbackImage(cacheKey));
            }
        }
        loaded = true;
    }

    /**
     * Create a simple fallback image with the piece letter.
     */
    private static Image createFallbackImage(String key) {
        BufferedImage img = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color pieceColor = key.startsWith("w") ? Color.WHITE : Color.DARK_GRAY;
        Color outlineColor = key.startsWith("w") ? Color.BLACK : Color.WHITE;

        g2.setColor(pieceColor);
        g2.fillOval(10, 10, 60, 60);
        g2.setColor(outlineColor);
        g2.drawOval(10, 10, 60, 60);

        g2.setFont(new Font("Serif", Font.BOLD, 32));
        String letter = key.substring(1).toUpperCase();
        FontMetrics fm = g2.getFontMetrics();
        int x = (80 - fm.stringWidth(letter)) / 2;
        int y = (80 + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(letter, x, y);
        g2.dispose();
        return img;
    }

    /**
     * Get image for a piece type and color.
     */
    public static Image getImage(PieceType type, PieceColor color) {
        if (!loaded) loadImages();
        String colorPrefix = (color == PieceColor.WHITE) ? "w" : "b";
        String pieceSuffix = switch (type) {
            case KING -> "k";
            case QUEEN -> "q";
            case ROOK -> "r";
            case BISHOP -> "b";
            case KNIGHT -> "n";
            case PAWN -> "p";
        };
        return imageCache.get(colorPrefix + pieceSuffix);
    }

    /**
     * Get a scaled image for a given target size.
     */
    public static Image getScaledImage(PieceType type, PieceColor color, int size) {
        Image base = getImage(type, color);
        if (base == null) return null;
        return base.getScaledInstance(size, size, Image.SCALE_SMOOTH);
    }
}

package com.escapetheblock;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * Swing view: draws a snapshot of the game model and does not change its state.
 */
public class GamePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int PLAYER_DIAMETER = 50;
    private final GameModel model;

    public GamePanel(GameModel model) {
        this.model = model;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        // Work on a copy so rendering hints and drawing state do not leak to Swing.
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            drawGame(g);
        } finally {
            g.dispose();
        }
    }

    private void drawGame(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.decode("#F7DC6F"));
        g.fillRect(0, 0, GameModel.BOARD_SIZE, GameModel.BOARD_SIZE);
        g.setColor(Color.WHITE);
        g.fillRect(60, 60, GameModel.BOARD_SIZE - 120, GameModel.BOARD_SIZE - 120);

        g.setColor(Color.RED);
        g.fillOval(
                model.getPlayerX() - PLAYER_DIAMETER / 2,
                model.getPlayerY() - PLAYER_DIAMETER / 2,
                PLAYER_DIAMETER,
                PLAYER_DIAMETER);

        g.setColor(Color.BLUE);
        for (Rectangle obstacle : model.getObstacleBounds()) {
            g.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
        }

        if (model.isGameOver()) {
            drawGameOver(g);
        }
    }

    private void drawGameOver(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 48));
        drawCentered(g, "Game Over", 150);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
        drawCentered(g, String.format("Time: %.2fs", model.getElapsedSeconds()), 300);
        drawCentered(g, String.format("Best: %.2fs", model.getBestTimeSeconds()), 450);
    }

    private void drawCentered(Graphics2D g, String text, int y) {
        int x = (GameModel.BOARD_SIZE - g.getFontMetrics().stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }
}

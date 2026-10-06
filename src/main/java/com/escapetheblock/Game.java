package com.escapetheblock;

import java.awt.Dimension;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 * Connects the game model, Swing view, input listener, and update timer.
 */
public class Game {
    private final GameModel model;
    private final GamePanel gamePanel;
    private final Mouse mouse;
    private final JFrame frame;
    private final Timer timer;

    public Game() throws IOException {
        model = new GameModel(new Configuration());
        gamePanel = new GamePanel(model);
        mouse = new Mouse();
        frame = new JFrame("Escape the Block");
        timer = new Timer(1000 / 60, event -> updateGame());

        gamePanel.setPreferredSize(new Dimension(GameModel.BOARD_SIZE, GameModel.BOARD_SIZE));
        gamePanel.addMouseListener(mouse);
        gamePanel.addMouseMotionListener(mouse);
        frame.add(gamePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        timer.start();
    }

    private void updateGame() {
        try {
            model.update(mouse);
            gamePanel.repaint();
        } catch (IOException exception) {
            timer.stop();
            JOptionPane.showMessageDialog(
                    frame,
                    "Could not save the best time: " + exception.getMessage(),
                    "Configuration error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

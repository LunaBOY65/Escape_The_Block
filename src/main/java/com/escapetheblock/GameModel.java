package com.escapetheblock;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds game state and applies the rules without depending on Swing.
 */
public class GameModel {
    public static final int BOARD_SIZE = 600;
    private static final int BOARD_MARGIN = 60;
    private static final int PLAYER_RADIUS = 25;

    private final Configuration configuration;
    private final Player player = new Player(BOARD_SIZE / 2, BOARD_SIZE / 2);
    private final List<Obstacle> obstacles = new ArrayList<>();

    private boolean draggingPlayer;
    private boolean started;
    private boolean gameOver;
    private boolean releasedAfterGameOver;
    private long startTimeNanos;
    private double elapsedSeconds;
    private double bestTimeSeconds;

    public GameModel(Configuration configuration) throws IOException {
        this.configuration = configuration;
        bestTimeSeconds = configuration.loadBestTime();
        resetRound();
    }

    public void update(Mouse mouse) throws IOException {
        if (gameOver) {
            if (!mouse.isDragging()) {
                releasedAfterGameOver = true;
                return;
            }
            if (!releasedAfterGameOver) {
                return;
            }
            resetRound();
        }

        if (mouse.isDragging()) {
            if (draggingPlayer || player.contains(mouse.getX(), mouse.getY())) {
                if (!started) {
                    started = true;
                    startTimeNanos = System.nanoTime();
                }
                player.moveTo(mouse.getX(), mouse.getY());
                draggingPlayer = true;
            }
        } else {
            draggingPlayer = false;
        }

        if (!started) {
            return;
        }

        for (Obstacle obstacle : obstacles) {
            obstacle.move(BOARD_SIZE);
        }

        if (hasCollision() || isOutsidePlayArea()) {
            finishRound();
        }
    }

    public int getPlayerX() {
        return player.getX();
    }

    public int getPlayerY() {
        return player.getY();
    }

    public List<Rectangle> getObstacleBounds() {
        List<Rectangle> bounds = new ArrayList<>(obstacles.size());
        for (Obstacle obstacle : obstacles) {
            bounds.add(obstacle.getBounds());
        }
        return List.copyOf(bounds);
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public double getElapsedSeconds() {
        return elapsedSeconds;
    }

    public double getBestTimeSeconds() {
        return bestTimeSeconds;
    }

    private boolean hasCollision() {
        Rectangle playerBounds = new Rectangle(
                player.getX() - PLAYER_RADIUS,
                player.getY() - PLAYER_RADIUS,
                PLAYER_RADIUS * 2,
                PLAYER_RADIUS * 2);

        for (Obstacle obstacle : obstacles) {
            if (playerBounds.intersects(obstacle.getBounds())) {
                return true;
            }
        }
        return false;
    }

    private boolean isOutsidePlayArea() {
        int minimumCenter = BOARD_MARGIN + PLAYER_RADIUS;
        int maximumCenter = BOARD_SIZE - BOARD_MARGIN - PLAYER_RADIUS;
        return player.getX() < minimumCenter
                || player.getY() < minimumCenter
                || player.getX() > maximumCenter
                || player.getY() > maximumCenter;
    }

    private void finishRound() throws IOException {
        elapsedSeconds = (System.nanoTime() - startTimeNanos) / 1_000_000_000.0;
        gameOver = true;
        if (elapsedSeconds > bestTimeSeconds) {
            configuration.saveBestTime(elapsedSeconds);
            bestTimeSeconds = elapsedSeconds;
        }
    }

    private void resetRound() {
        player.moveTo(BOARD_SIZE / 2, BOARD_SIZE / 2);
        obstacles.clear();
        obstacles.add(new Obstacle(100, 100, 85, 85, 7, 4));
        obstacles.add(new Obstacle(355, 90, 90, 75, -4, 5));
        obstacles.add(new Obstacle(100, 430, 40, 80, 7, -6));
        obstacles.add(new Obstacle(415, 450, 125, 30, -5, -8));
        draggingPlayer = false;
        started = false;
        gameOver = false;
        releasedAfterGameOver = false;
        startTimeNanos = 0;
        elapsedSeconds = 0;
    }
}

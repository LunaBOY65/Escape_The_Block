package com.escapetheblock;

import java.awt.Rectangle;

/**
 * A moving rectangular obstacle with its own position and velocity.
 */
public class Obstacle {
    private int x;
    private int y;
    private final int width;
    private final int height;
    private int velocityX;
    private int velocityY;

    public Obstacle(int x, int y, int width, int height, int velocityX, int velocityY) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void move(int boardSize) {
        x += velocityX;
        y += velocityY;
        if (x < 0 || x + width > boardSize) {
            velocityX = -velocityX;
        }
        if (y < 0 || y + height > boardSize) {
            velocityY = -velocityY;
        }
    }
}

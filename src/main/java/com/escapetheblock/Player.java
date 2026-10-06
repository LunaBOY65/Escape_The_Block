package com.escapetheblock;

/**
 * The player model owns the circle's position and hit-test.
 */
public class Player {
    private int x;
    private int y;

    public Player(int x, int y) {
        moveTo(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean contains(int pointX, int pointY) {
        int radius = 25;
        return pointX > x - radius
                && pointX < x + radius
                && pointY > y - radius
                && pointY < y + radius;
    }
}

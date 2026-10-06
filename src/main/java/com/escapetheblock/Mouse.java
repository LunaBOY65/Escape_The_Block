package com.escapetheblock;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Adapts Swing mouse events into the small input state the game model needs.
 */
public class Mouse extends MouseAdapter {
    private int x = -1;
    private int y = -1;
    private boolean dragging;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isDragging() {
        return dragging;
    }

    @Override
    public void mousePressed(MouseEvent event) {
        updatePosition(event);
        dragging = true;
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        updatePosition(event);
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        updatePosition(event);
        dragging = false;
    }

    private void updatePosition(MouseEvent event) {
        x = event.getX();
        y = event.getY();
    }
}

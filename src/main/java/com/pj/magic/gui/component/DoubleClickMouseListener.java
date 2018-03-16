package com.pj.magic.gui.component;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

@FunctionalInterface
public interface DoubleClickMouseListener extends MouseListener {

    void onDoubleClick();
    
    @Override
    default void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            onDoubleClick();
        }
    }

    @Override
    default void mousePressed(MouseEvent e) {
    }

    @Override
    default void mouseReleased(MouseEvent e) {
    }

    @Override
    default void mouseEntered(MouseEvent e) {
    }

    @Override
    default void mouseExited(MouseEvent e) {
    }
    
}

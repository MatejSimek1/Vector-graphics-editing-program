package hr.unizg.fer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Canvas extends JComponent {
    private DocumentModel model;
    private GUI gui;

    public Canvas(DocumentModel model, GUI gui) {
        this.model = model;
        this.gui = gui;

        setFocusable(true);
        requestFocusInWindow();


        // dodavanje keyListenera
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e){
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
                    gui.setCurrentState(new IdleState());
                } else{
                    gui.getCurrentState().keyPressed(e.getKeyCode());
                }
            }
        });

        // dodavanje mouse listenera
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                gui.getCurrentState().mouseDown(new Point(e.getX(), e.getY()), e.isShiftDown(), e.isControlDown());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                gui.getCurrentState().mouseUp(new Point(e.getX(), e.getY()), e.isShiftDown(), e.isControlDown());
            }
        });

        // dodavanje mouse motion listenera
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                gui.getCurrentState().mouseDragged(new Point(e.getX(), e.getY()));
            }
        });

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);   // čisti površinu

        Graphics2D g2d = (Graphics2D)g;
        Renderer r = new G2DRendererImpl(g2d);

        for (GraphicalObject go : model.list()){
            go.render(r);
            this.gui.getCurrentState().afterDraw(r, go);
        }
        this.gui.getCurrentState().afterDraw(r);
    }

}

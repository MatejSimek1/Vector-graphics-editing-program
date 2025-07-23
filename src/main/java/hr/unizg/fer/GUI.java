package hr.unizg.fer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GUI extends JFrame {
    private List<GraphicalObject> graphicalObjects;
    private DocumentModel documentModel;
    private JToolBar toolBar;
    private Canvas canvas;
    private State currentState;
//    private List<JButton> buttons;

    // konstruktor
    public GUI(List<GraphicalObject> graphicalObjects) {
        this.graphicalObjects = graphicalObjects;
        this.documentModel = new DocumentModel(new ArrayList<>());
        this.toolBar = new JToolBar();
        this.currentState = new IdleState();

        // Pretplata na promjene modela
        this.documentModel.addDocumentModelListener(() -> {
            canvas.repaint();   // ovo je zapravo DocumentModelListener.documentChange() - mogu ovako definirati jer je
        });                     // u sučelju definirana samo jedna metoda

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800,700);
        this.setLayout(new BorderLayout());

        // stvori po jedan gumb za svaki graphicalObject imena graphicalObject.getName()
        for(GraphicalObject graphicalObject : graphicalObjects){
            JButton button = new JButton(graphicalObject.getShapeName());
            button.addActionListener(e -> {
                setCurrentState(new AddShapeState(this.documentModel, graphicalObject));
                canvas.requestFocusInWindow();
            });
            toolBar.add(button);
        }
        // stvori "selektiraj" button
        JButton selectButton = new JButton("Selektiraj");
        selectButton.addActionListener(e -> {
            setCurrentState(new SelectShapeState(this.documentModel));
            canvas.requestFocusInWindow();
        });
        toolBar.add(selectButton);

        // stvori "brisalo" button
        JButton deleteButton = new JButton("Brisalo");
        deleteButton.addActionListener(e -> {
            setCurrentState(new EraserState(this.documentModel));
            canvas.requestFocusInWindow();
        });
        toolBar.add(deleteButton);


        this.add(toolBar, BorderLayout.NORTH);

        this.canvas = new Canvas(this.documentModel, this);
        canvas.setFocusable(true);
        this.add(canvas, BorderLayout.CENTER);
//        canvas.requestFocusInWindow();

        this.canvas.repaint();
        SwingUtilities.invokeLater(() -> canvas.requestFocusInWindow());

    }

    public State getCurrentState() {
        return currentState;
    }
    public void setCurrentState(State newState) {
        this.currentState.onLeaving();  // očistit prethodno stanje ako treba
        this.currentState = newState;   // postavi novo stanje
    }
}

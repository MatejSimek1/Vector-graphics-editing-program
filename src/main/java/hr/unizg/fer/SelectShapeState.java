package hr.unizg.fer;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class SelectShapeState implements State{
    private DocumentModel documentModel;
    private int selectedHotPointIndex = -1;
    private GraphicalObject selectedHotPointObject = null;

    public SelectShapeState(DocumentModel document) {
        this.documentModel = document;
    }

    @Override
    public void mouseDown(Point mousePoint, boolean shiftDown, boolean ctrlDown) {
        GraphicalObject go = this.documentModel.findSelectedGraphicalObject(mousePoint);
        if (go == null) return; // ako nije selektiran objekt ne radi ništa
        if(this.documentModel.getSelectedObjects().size() == 1){
            GraphicalObject selectedObject = this.documentModel.getSelectedObjects().getFirst();
            int index = this.documentModel.findSelectedHotPoint(selectedObject, mousePoint);

            if (index != -1){
                this.selectedHotPointIndex = index;
                this.selectedHotPointObject = selectedObject;
                return;
            }
        }

        if (!ctrlDown) {
            // odselektiraj sve trenutno selektirane elemente
            for (GraphicalObject selectedObject : new ArrayList<>(documentModel.getSelectedObjects())){
                selectedObject.setSelected(false);
            }
        }
        go.setSelected(true);

    }

    @Override
    public void mouseUp(Point mousePoint, boolean shiftDown, boolean ctrlDown) {
        // resetiraj stanje
        this.selectedHotPointIndex = -1;
        this.selectedHotPointObject = null;
    }

    @Override
    public void mouseDragged(Point mousePoint) {
        if (this.selectedHotPointObject != null && this.selectedHotPointIndex != -1) {
            this.selectedHotPointObject.setHotPoint(selectedHotPointIndex, mousePoint);
        }
    }

    @Override
    public void keyPressed(int keyCode) {
        int dx = 0, dy = 0;

        switch (keyCode) {
            case KeyEvent.VK_LEFT: dx = -1; break;
            case KeyEvent.VK_RIGHT: dx = 1; break;
            case KeyEvent.VK_UP: dy = -1; break;
            case KeyEvent.VK_DOWN: dy = 1; break;
            case KeyEvent.VK_ADD:
                for(GraphicalObject go : documentModel.getSelectedObjects()){
                    this.documentModel.increaseZ(go);
                }
                return;
            case KeyEvent.VK_MINUS:
            case KeyEvent.VK_SUBTRACT:
                for(GraphicalObject go : documentModel.getSelectedObjects()){
                    this.documentModel.decreaseZ(go);
                }
                return;
            case KeyEvent.VK_G:
                // briše iz modela sve selektirane objekte,
                // stvara novi kompozit čija to postaju djeca i samo taj kompozit ubacuje u model

                // dohvat selektiranih objekata
                List<GraphicalObject> selectedObjects = new ArrayList<>(documentModel.getSelectedObjects());

                if(selectedObjects.size() == 1) return; // ako je veličine jedan ne želimo stvarati kompozit iz njega

                // brisanje selektiranih objekata iz modela
                for(GraphicalObject go : selectedObjects){
                    this.documentModel.removeGraphicalObject(go);
                    go.setSelected(false);
                }

                CompositeShape compositeShape = new CompositeShape(selectedObjects);
                compositeShape.setSelected(true);
                this.documentModel.addGraphicalObject(compositeShape);
                return;
            case KeyEvent.VK_U:
                // (u slučaju da postoji jedan selektirani objekt i da je on kompozit)
                // briše se taj kompozit iz modela i ponovno dodaje u model njegovu djecu (i odmah ih ostavlja
                // selektiranima tako da bi pritisak na G ponovno sve natrag zapakirao).
                List<GraphicalObject> selectedObjects_u = documentModel.getSelectedObjects();

                if(selectedObjects_u.size() == 1 &&
                        selectedObjects_u.getFirst() instanceof CompositeShape){

                    CompositeShape compositeShape_u = (CompositeShape) selectedObjects_u.getFirst();
                    this.documentModel.removeGraphicalObject(compositeShape_u);
                    for(GraphicalObject go : compositeShape_u.getChildren()){
                        go.setSelected(true);
                        this.documentModel.addGraphicalObject(go);
                    }
                }
                return;
        }

        for (GraphicalObject go : documentModel.getSelectedObjects()){
            go.translate(new Point(dx, dy));
        }
    }

    @Override
    public void afterDraw(Renderer r, GraphicalObject go) {}

    @Override
    public void afterDraw(Renderer r) {
        List<GraphicalObject> selectedObjects = this.documentModel.getSelectedObjects();
        for(GraphicalObject selectedObject : selectedObjects){
            Rectangle boundingBox = selectedObject.getBoundingBox();

            // nađi 4 točke bounding boxa
            Point p1 = new Point(boundingBox.getX(), boundingBox.getY());   // gornji lijevi kut
            Point p2 = new Point(boundingBox.getX() + boundingBox.getWidth(), boundingBox.getY()); // gornji desni kut
            Point p3 = new Point(boundingBox.getX(), boundingBox.getY() + boundingBox.getHeight()); // donji lijevi
            Point p4 = new Point(boundingBox.getX() + boundingBox.getWidth(), boundingBox.getY() + boundingBox.getHeight()); // donji desni

            // iscrtaj bounding box
            r.drawLine(p1, p2);
            r.drawLine(p2, p4);
            r.drawLine(p3, p4);
            r.drawLine(p3, p1);
        }

        if(selectedObjects.size() == 1){
            // nacrtaj i male box-iće oko hot-pointova
            GraphicalObject go = selectedObjects.getFirst();
            int hotPointsNumber = go.getNumberOfHotPoints();
            for(int i=0; i<hotPointsNumber; i++){
                Point centarPoint = go.getHotPoint(i);

                Point p1 = new Point(centarPoint.getX() - 3, centarPoint.getY() - 3);   // gornji lijevi
                Point p2 = new Point(centarPoint.getX() + 3, centarPoint.getY() - 3);   // gornji desni
                Point p3 = new Point(centarPoint.getX() - 3, centarPoint.getY() + 3);   // donji lijevi
                Point p4 = new Point(centarPoint.getX() + 3, centarPoint.getY() + 3);   // donji desni

                r.drawLine(p1,p2);
                r.drawLine(p2,p4);
                r.drawLine(p4,p3);
                r.drawLine(p3,p1);
            }
        }
    }

    @Override
    public void onLeaving() {
        for(GraphicalObject go : new ArrayList<>(documentModel.getSelectedObjects())){
            go.setSelected(false);
        }
    }
}

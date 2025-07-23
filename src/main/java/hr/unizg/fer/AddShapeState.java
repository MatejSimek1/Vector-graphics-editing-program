package hr.unizg.fer;

public class AddShapeState implements State {

    private GraphicalObject prototype;
    private DocumentModel model;

    public AddShapeState(DocumentModel model, GraphicalObject prototype) {
        this.model = model;
        this.prototype = prototype;
    }

    @Override
    public void mouseDown(Point mousePoint, boolean shiftDown, boolean ctrlDown) {
        // dupliciraj zapamćeni prototip, pomakni ga na poziciju miša i dodaj u model
        GraphicalObject duplicatedObj = this.prototype.duplicate();
        Point currentPoint = duplicatedObj.getHotPoint(0);  // trenutni hotpoint objekta
        Point delta = mousePoint.difference(currentPoint);        // trenutni položaj miša
        duplicatedObj.translate(delta);                           // razlika tog dvoje je pomak

        this.model.addGraphicalObject(duplicatedObj);
    }

    @Override
    public void mouseUp(Point mousePoint, boolean shiftDown, boolean ctrlDown) {}

    @Override
    public void mouseDragged(Point mousePoint) {}

    @Override
    public void keyPressed(int keyCode) {}

    @Override
    public void afterDraw(Renderer r, GraphicalObject go) {}

    @Override
    public void afterDraw(Renderer r) {}

    @Override
    public void onLeaving() {}

}

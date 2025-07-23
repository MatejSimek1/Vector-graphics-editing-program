package hr.unizg.fer;

import java.util.ArrayList;
import java.util.List;

public class EraserState implements State{

    public final static int PIXEL_TOLERANCE = 3;

    private DocumentModel model;
    private List<Point> pathPoints = new ArrayList<Point>();

    public EraserState(DocumentModel model) {
        this.model = model;
    }

    @Override
    public void mouseDown(Point mousePoint, boolean shiftDown, boolean ctrlDown) {
        pathPoints.clear();
        pathPoints.add(mousePoint);
    }

    @Override
    public void mouseUp(Point mousePoint, boolean shiftDown, boolean ctrlDown) {
        // obriši sve segmente koji se sjeku sa krivuljom miša
        // gledam za svaku točku u pathPoints i svaki objekt na platnu dali je udaljenost od neke točke do objekta = 0
        // ako da znači da se sjeku -> obriši ga
        for(GraphicalObject go : new ArrayList<>(model.list())) {
            for (Point point : pathPoints){
                if(go.selectionDistance(point) <= PIXEL_TOLERANCE){
                    // sijeku se -> ukloni objekt
                    this.model.removeGraphicalObject(go);
                }
            }
        }
        pathPoints.clear();
    }

    @Override
    public void mouseDragged(Point mousePoint) {
        pathPoints.add(mousePoint);
        model.notifyListeners();
    }

    @Override
    public void keyPressed(int keyCode) {

    }

    @Override
    public void afterDraw(Renderer r, GraphicalObject go) {

    }

    @Override
    public void afterDraw(Renderer r) {
        // iscrtaj liniju miša
        for (int i = 0; i < pathPoints.size() - 1; i++){
            r.drawLine(pathPoints.get(i), pathPoints.get(i+1));
        }
    }

    @Override
    public void onLeaving() {

    }
}

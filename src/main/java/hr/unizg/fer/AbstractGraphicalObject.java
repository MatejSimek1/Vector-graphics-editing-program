package hr.unizg.fer;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGraphicalObject implements GraphicalObject {
    private Point[] hotPoints;
    private boolean[] hotPointSelected;
    private boolean selected;
    List<GraphicalObjectListener> listeners = new ArrayList<>();

    protected AbstractGraphicalObject(Point[] hotPoints) {
        this.hotPoints = hotPoints;
    }

    public Point getHotPoint(int index) {
        return this.hotPoints[index];
    }

    public void setHotPoint(int index, Point hotPoint) {
        this.hotPoints[index] = hotPoint;
        notifyListeners();
    }

    public int getNumberOfHotPoints() {
        return this.hotPoints.length;
    }

    public double getHotPointDistance(int index, Point point) {
        return GeometryUtil.distanceFromPoint(point, this.hotPoints[index]);
    }

    public boolean isHotPointSelected(int index) {
        return this.hotPointSelected[index];
    }

    public void setHotPointSelected(int index, boolean isSelected) {
        this.hotPointSelected[index] = isSelected;
        notifyListeners();
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        notifySelectionListeners();
    }

    public void translate(Point point) {
        for(int i = 0; i < this.hotPoints.length; i++) {
            this.hotPoints[i] = this.hotPoints[i].translate(point);
        }

        notifyListeners();
    }

    public void addGraphicalObjectListener(GraphicalObjectListener listener) {
        if (!this.listeners.contains(listener)) this.listeners.add(listener);   // dodajemo samo ako već nije dodan
    }

    public void removeGraphicalObjectListener(GraphicalObjectListener listener) {
        this.listeners.remove(listener);
    }

    protected void notifyListeners() {
        for(GraphicalObjectListener listener : this.listeners){
            listener.graphicalObjectChanged(this);
        }
    }

    protected void notifySelectionListeners(){
        for(GraphicalObjectListener listener : this.listeners){
            listener.graphicalObjectSelectionChanged(this);
        }
    }
}

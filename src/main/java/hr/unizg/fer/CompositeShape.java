package hr.unizg.fer;

import java.util.ArrayList;
import java.util.List;

public class CompositeShape implements GraphicalObject{
    private List<GraphicalObject> children;
    private boolean selected;
    private List<GraphicalObjectListener> listeners = new ArrayList<>();

    public CompositeShape(List<GraphicalObject> children) {
        this.children = children;
    }

    @Override
    public boolean isSelected() {
        return this.selected;
    }

    @Override
    public void setSelected(boolean selected) {
//        for(GraphicalObject child : this.children) {
//            child.setSelected(selected);
//        }
        this.selected = selected;
        notifySelectionListeners();
    }

    @Override
    public int getNumberOfHotPoints() {
        // nema hotpoint-ova
        return 0;
    }

    @Override
    public Point getHotPoint(int index) {
        // nema hotpoint-ova
        return null;
    }

    @Override
    public void setHotPoint(int index, Point point) {
        // nema hotpoint-ova
    }

    @Override
    public boolean isHotPointSelected(int index) {
        // nema hotpoint-ova
        return false;
    }

    @Override
    public void setHotPointSelected(int index, boolean selected) {
        // nema hotpoint-ova
    }

    @Override
    public double getHotPointDistance(int index, Point mousePoint) {
        // nema hotpoint-ova
        return -1;
    }

    @Override
    public void translate(Point delta) {
        // delegira poziv funkcije djeci
        for (GraphicalObject child : children) {
            child.translate(delta);
        }
    }

    @Override
    public Rectangle getBoundingBox() {
        // unija bounding boxova djece -> tj. radim bounding box oko svega skupa (minimalni (x,y) od svih bounding
        // boxova djece te height i width do najdesnijega odnosno najnižega)
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (GraphicalObject child : children) {
            Rectangle childBoundingBox = child.getBoundingBox();
            int childMinX = childBoundingBox.getX();
            int childMinY = childBoundingBox.getY();
            int childMaxX = childMinX + childBoundingBox.getWidth();
            int childMaxY = childMinY + childBoundingBox.getHeight();

            if(childMinX < minX) minX = childMinX;
            if(childMinY < minY) minY = childMinY;
            if(childMaxX > maxX) maxX = childMaxX;
            if(childMaxY > maxY) maxY = childMaxY;

        }
        return new Rectangle(minX, minY, maxX-minX, maxY-minY);
    }

    @Override
    public double selectionDistance(Point mousePoint) {
        // uzima najmanju udaljenost do dijeteta unutar box-a
        double minimalDistance = Double.MAX_VALUE;

        for (GraphicalObject child : children) {
            double childDistance = child.selectionDistance(mousePoint);
            if(childDistance < minimalDistance) minimalDistance = childDistance;
        }

        return minimalDistance;
    }

    @Override
    public void render(Renderer r) {
        for (GraphicalObject child : children) {
            child.render(r);
        }
    }


    @Override
    public void addGraphicalObjectListener(GraphicalObjectListener l) {
        for (GraphicalObject child : children) {
            child.addGraphicalObjectListener(l);
        }
        if (!listeners.contains(l)) listeners.add(l);
    }

    @Override
    public void removeGraphicalObjectListener(GraphicalObjectListener l) {
        for (GraphicalObject child : children) {
            child.removeGraphicalObjectListener(l);
        }
        listeners.remove(l);
    }

    @Override
    public String getShapeName() {
        return "Kompozit";
    }

    @Override
    public GraphicalObject duplicate() {
        List<GraphicalObject> newChildren = new ArrayList<GraphicalObject>();
        for (GraphicalObject child : children) {
            newChildren.add(child.duplicate());
        }
        return new CompositeShape(newChildren);
    }

    public List<GraphicalObject> getChildren() {
        return children;
    }

    public void notiyListeners(){
        for (GraphicalObjectListener listener : listeners) {
            listener.graphicalObjectChanged(this);
        }
    }

    public void notifySelectionListeners(){
        for (GraphicalObjectListener listener : listeners) {
            listener.graphicalObjectSelectionChanged(this);
        }
    }
}

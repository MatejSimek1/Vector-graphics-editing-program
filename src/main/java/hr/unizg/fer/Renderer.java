package hr.unizg.fer;

public interface Renderer {
    void drawLine(Point s, Point e);
    void fillPolygon(Point[] points);
}

package hr.unizg.fer;

public class LineSegment extends AbstractGraphicalObject{

    public LineSegment(){
        super(new Point[] {
                new Point(0,0),
                new Point(10,0)});
    }

    public LineSegment(Point startPoint, Point endPoint){
        super(new Point[] {
                startPoint,
                endPoint
        });
    }

    @Override
    public Rectangle getBoundingBox() {
        Point startPoint = this.getHotPoint(0);
        Point endPoint = this.getHotPoint(1);
        // (x,y) -> lijevi gornji kut
        int x = Math.min(startPoint.getX(), endPoint.getX());
        int y = Math.min(startPoint.getY(), endPoint.getY());
        int width = Math.abs(startPoint.getX() - endPoint.getX());
        int height = Math.abs(startPoint.getY() - endPoint.getY());

        return new Rectangle(x, y, width, height);
    }

    @Override
    public double selectionDistance(Point mousePoint) {
        return GeometryUtil.distanceFromLineSegment(this.getHotPoint(0), this.getHotPoint(1), mousePoint);
    }

    @Override
    public void render(Renderer r) {
        r.drawLine(this.getHotPoint(0), this.getHotPoint(1));
    }

    @Override
    public String getShapeName() {
        return "Linija";
    }

    @Override
    public GraphicalObject duplicate() {
        Point startPoint = this.getHotPoint(0);
        Point endPoint = this.getHotPoint(1);
        return new LineSegment(new Point(startPoint.getX(), startPoint.getY()), new Point(endPoint.getX(), endPoint.getY()));
    }
}

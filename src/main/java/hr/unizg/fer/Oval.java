package hr.unizg.fer;

public class Oval extends AbstractGraphicalObject{

    public Oval(){
        super(new Point[] {
                new Point(10, 0),   // desni hot point
                new Point(0, 10),   // donji hot point
        });
    }

    // provjerit dal ovdje ovako ili kreirati nove Point točke
    public Oval(Point right, Point bottom){
        super(new Point[] {
                right,
                bottom
        });
    }

    @Override
    public Rectangle getBoundingBox() {
        Point right = this.getHotPoint(0);
        Point bottom = this.getHotPoint(1);

        int x = bottom.getX() - Math.abs(right.getX() - bottom.getX());
        int y = right.getY() - Math.abs(right.getY() - bottom.getY());
        int width = 2 * Math.abs(right.getX() - bottom.getX());
        int height = 2 * Math.abs(right.getY() - bottom.getY());
        return new Rectangle(x, y, width, height);
    }

    @Override
    public double selectionDistance(Point mousePoint) {
        // udaljenost miša od ovala
        Point right = getHotPoint(0); // desni rub (cx + rx, cy)
        Point bottom = getHotPoint(1); // donji rub (cx, cy + ry)

        // centralna točka
        int cx = bottom.getX();
        int cy = right.getY();
        // polumjeri u smjeru x (= rx) i y (= ry)
        int rx = Math.abs(right.getX() - cx);
        int ry = Math.abs(bottom.getY() - cy);
        // normalizirana udaljenost točke mousePoint od centra elipse po komponentama x i y
        double dx = (mousePoint.getX() - cx) / (double)rx;
        double dy = (mousePoint.getY() - cy) / (double)ry;
        double norm = Math.sqrt(dx*dx + dy*dy);

        if (norm <= 1.0) return 0; // točka unutar elipse
        else {
            // točka izvan elipse
            // radi projekciju na rub jedinične kružnice
            double projectedX = dx / norm;
            double projectedY = dy / norm;

            // denormalizacija u stvarne koordinate
            double nearestX = cx + rx * projectedX;
            double nearestY = cy + ry * projectedY;
            // udaljenost te točke od mouse pointa
            return GeometryUtil.distanceFromPoint(nearestX, nearestY, mousePoint.getX(), mousePoint.getY());
        }
    }

    @Override
    public void render(Renderer r) {
        Point right = getHotPoint(0);
        Point bottom = getHotPoint(1);

        // centralna točka
        int cx = bottom.getX();
        int cy = right.getY();
        // poluosi u smjeru x (= a) i y (= b)
        int a = Math.abs(right.getX() - cx);
        int b = Math.abs(bottom.getY() - cy);

        int N = 40;
        Point[] ellipsePoints = new Point[N];
        for (int i = 0; i < N; i++) {
            double theta = 2 * Math.PI * i / N;
            int x = (int)(cx + a * Math.cos(theta));
            int y = (int)(cy + b * Math.sin(theta));
            ellipsePoints[i] = new Point(x, y);
        }

        r.fillPolygon(ellipsePoints);
    }

    @Override
    public String getShapeName() {
        return "Oval";
    }

    @Override
    public GraphicalObject duplicate() {
        Point right = this.getHotPoint(0);
        Point bottom = this.getHotPoint(1);

        return new Oval(new Point(right.getX(), right.getY()), new Point(bottom.getX(), bottom.getY()));
    }
}

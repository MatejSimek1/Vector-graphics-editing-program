package hr.unizg.fer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class G2DRendererImpl implements Renderer {

    private Graphics2D g2d;

    public G2DRendererImpl(Graphics2D g2d) {
        this.g2d = g2d;
    }

    @Override
    public void drawLine(Point s, Point e) {
        // Postavi boju na plavu
        // Nacrtaj linijski segment od S do E
        // (sve to uporabom g2d dobivenog u konstruktoru)
        g2d.setColor(Color.blue);
        g2d.drawLine(s.getX(), s.getY(), e.getX(), e.getY());
    }

    @Override
    public void fillPolygon(Point[] points) {
        // Postavi boju na plavu
        // Popuni poligon definiran danim točkama
        // Postavi boju na crvenu
        // Nacrtaj rub poligona definiranog danim točkama
        // (sve to uporabom g2d dobivenog u konstruktoru)
        int nPoints = points.length;
        int[] xPoints = new int[nPoints];
        int[] yPoints = new int[nPoints];
        for(int i = 0; i < nPoints; i++) {
            xPoints[i] = points[i].getX();
            yPoints[i] = points[i].getY();
        }
        g2d.setColor(Color.blue);
        Polygon polygon = new Polygon(xPoints, yPoints, nPoints);
        g2d.fillPolygon(polygon);

        g2d.setColor(Color.red);
        g2d.drawPolygon(polygon);
    }

}

package hr.unizg.fer;

public class GeometryUtil {

    public static double distanceFromPoint(Point point1, Point point2) {
        // izračunaj euklidsku udaljenost između dvije točke -> sqrt((x1-x2)^2 + (y1-y2)^2)
        return Math.sqrt(Math.pow(point1.getX() - point2.getX(), 2) + Math.pow(point1.getY() - point2.getY(), 2));
    }

    public static double distanceFromPoint(double x1, double y1, double x2, double y2) {
        // izračunaj euklidsku udaljenost između dvije točke -> sqrt((x1-x2)^2 + (y1-y2)^2)
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static double distanceFromLineSegment(Point s, Point e, Point p) {
        // Izračunaj koliko je točka P udaljena od linijskog segmenta određenog
        // početnom točkom S i završnom točkom E. Uočite: ako je točka P iznad/ispod
        // tog segmenta, ova udaljenost je udaljenost okomice spuštene iz P na S-E.
        // Ako je točka P "prije" točke S ili "iza" točke E, udaljenost odgovara
        // udaljenosti od P do početne/konačne točke segmenta.

        // implementacija korištenjem vektora i projekcije:
        // 1) nađi vektore SE i SP
        double SE_vect[] = {e.getX()-s.getX(), e.getY()-s.getY()};  // index = 0 -> x koord; index = 1 -> y koord
        double SP_vect[] = {p.getX()-s.getX(), p.getY()-s.getY()};

        // 2) nađi projekciju SP_vect na SE_vect
        double t = (SP_vect[0]*SE_vect[0] + SP_vect[1]*SE_vect[1]) / Math.pow(distanceFromPoint(s,e),2);

        // 3) provjeri t
        if(t < 0){
            // nadi udaljenost od p do s
            return distanceFromPoint(s, p);
        } else if (t > 1) {
            // nadi udaljenost od p do e
            return distanceFromPoint(e, p);
        } else {
            // 0 <= t <= 1 -> projekcija je unutar segmenta -> računamo duljinu okomice
            // računamo projekcijsku točku -> (projektiraniX, projektiraniY)
            double projektiraniX = s.getX() + t * SE_vect[0];
            double projektiraniY = s.getY() + t * SE_vect[1];

            // nemogu koristiti gornju funkciju jer ona prima Point objekta, a oni imaju koordinate tipa int
            // dok je ovdje double
            return Math.sqrt(Math.pow(p.getX() - projektiraniX, 2) + Math.pow(p.getY() - projektiraniY, 2));
        }
    }
}

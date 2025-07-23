package hr.unizg.fer;

public interface GraphicalObjectListener {

    // Poziva se kad se nad objektom promjeni bilo što...
    void graphicalObjectChanged(GraphicalObject go);

    // Poziva se isključivo ako je nad objektom promjenjen status selektiranosti
    // (baš objekta, ne njegovih hot-point-a).
    void graphicalObjectSelectionChanged(GraphicalObject go);

}

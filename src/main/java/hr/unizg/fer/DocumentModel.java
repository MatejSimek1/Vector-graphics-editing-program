package hr.unizg.fer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DocumentModel {

    public final static double SELECTION_PROXIMITY = 10;

    // Kolekcija svih grafičkih objekata:
    private List<GraphicalObject> objects = new ArrayList<>();
    // Read-Only proxy oko kolekcije grafičkih objekata:
    private List<GraphicalObject> roObjects = Collections.unmodifiableList(objects);
    // Kolekcija prijavljenih promatrača:
    private List<DocumentModelListener> listeners = new ArrayList<>();
    // Kolekcija selektiranih objekata:
    private List<GraphicalObject> selectedObjects = new ArrayList<>();
    // Read-Only proxy oko kolekcije selektiranih objekata:
    private List<GraphicalObject> roSelectedObjects = Collections.unmodifiableList(selectedObjects);

    // Promatrač koji će biti registriran nad svim objektima crteža...
    private final GraphicalObjectListener goListener = new GraphicalObjectListener() {

        @Override
        public void graphicalObjectChanged(GraphicalObject go) {
            // obavijesti listenere da je došlo do promjene na platnu -> DocumentModel ne sadrži interne promjene
            // od svakog objekta pa ne treba ništa updateati već samo obavijestiti o promjeni
            notifyListeners();
        }

        @Override
        public void graphicalObjectSelectionChanged(GraphicalObject go) {
            // DocumentModel sadrži info o selektiranosti objekata pa treba ažurirati selektiranost objekta go
            // i tek onda obavijestiti listenere
            if(go.isSelected()){
                if(!selectedObjects.contains(go)){
                    selectedObjects.add(go);
                }
            } else{
                selectedObjects.remove(go);
            }
            // obavijesti listenere da je došlo do promjene selektiranosti
            notifyListeners();
        }
    };

    // Konstruktor...
    public DocumentModel(List<GraphicalObject> objects) {
        // objects.add - zadržava adresu this.objects u memoriji na istom mjestu te samo dodaje elemente u tu listu
        // To je nužno za dobro funkcioniranje roObjects jer on je wrapper od početnog this.objects
        // da smo napravili this.objects = objects -> to mjenja početni this.objects (pokazuje na novi objekt)
        // te roObjects više nije wrapper oko točnog objekta
        for(GraphicalObject go : objects){
            if(!this.objects.contains(go)) this.objects.add(go);    // kako se nebi dodavali duplikati
        }

        for (GraphicalObject go : objects) {
            go.addGraphicalObjectListener(this.goListener);
            if (go.isSelected()) {
                this.selectedObjects.add(go);
            }
        }

        notifyListeners();
    }

    // Brisanje svih objekata iz modela (pazite da se sve potrebno odregistrira)
    // i potom obavijeste sve promatrače modela
    public void clear() {
        for (GraphicalObject go : this.objects) {
            go.removeGraphicalObjectListener(this.goListener);
        }
        this.objects.clear();
        this.selectedObjects.clear();
        notifyListeners();
    }

    // Dodavanje objekta u dokument (pazite je li već selektiran; registrirajte model kao promatrača)
    public void addGraphicalObject(GraphicalObject obj) {
        if(this.objects.contains(obj)) return;  // objekt je već dodan -> ignoriraj zahtjev

        this.objects.add(obj);
        if(obj.isSelected()){
            this.selectedObjects.add(obj);
        }
        obj.addGraphicalObjectListener(this.goListener);
        notifyListeners();
    }

    // Uklanjanje objekta iz dokumenta (pazite je li već selektiran; odregistrirajte model kao promatrača)
    public void removeGraphicalObject(GraphicalObject obj) {
        if (!this.objects.contains(obj)) return; // objekt se ne nalazi u listi -> zanemari zahtjev

        obj.removeGraphicalObjectListener(this.goListener);
        this.objects.remove(obj);
        this.selectedObjects.remove(obj);

        notifyListeners();
    }

    // Vrati nepromjenjivu listu postojećih objekata (izmjene smiju ići samo kroz metode modela)
    public List<GraphicalObject> list() {
        // ovo je puno bolje nego svaki put vraćati novu kopiju liste
        // (koja također osigurava da netko ne promjeni orginalnu listu, ali nije optimalno)
        // ovo vraća nepromjenjivu listu (koja je zapravo proxy oko obične liste objekata)
        return this.roObjects;
    }

    // Prijava...
    public void addDocumentModelListener(DocumentModelListener l) {
        if(!this.listeners.contains(l)) this.listeners.add(l);  // dodaj samo ako već nije dodan kao listener
    }

    // Odjava...
    public void removeDocumentModelListener(DocumentModelListener l) {
        this.listeners.remove(l);
    }

    // Obavještavanje...
    public void notifyListeners() {
        for (DocumentModelListener l : this.listeners) {
            l.documentChange();
        }
    }

    // Vrati nepromjenjivu listu selektiranih objekata
    public List<GraphicalObject> getSelectedObjects() {
        return this.roSelectedObjects;
    }

    // Pomakni predani objekt u listi objekata na jedno mjesto kasnije...
    // Time će se on iscrtati kasnije (pa će time možda veći dio biti vidljiv)
    public void increaseZ(GraphicalObject go) {
        int index = this.objects.indexOf(go);

        // ako element nije u listi ili je na zadnjem mjestu -> ne mjenjaj ništa
        if (index == -1 || index == this.objects.size()-1) return;

        Collections.swap(this.objects, index, index+1);
        notifyListeners();
    }

    // Pomakni predani objekt u listi objekata na jedno mjesto ranije...
    public void decreaseZ(GraphicalObject go) {
        int index = this.objects.indexOf(go);

        // ako element nije u listi ili je na prvom mjestu -> ne mjenjaj ništa
        if (index == -1 || index == 0) return;

        Collections.swap(this.objects, index, index-1);
        notifyListeners();
    }

    // Pronađi postoji li u modelu neki objekt koji klik na točku koja je
    // predana kao argument selektira i vrati ga ili vrati null. Točka selektira
    // objekt kojemu je najbliža uz uvjet da ta udaljenost nije veća od
    // SELECTION_PROXIMITY. Status selektiranosti objekta ova metoda NE dira.
    public GraphicalObject findSelectedGraphicalObject(Point mousePoint) {
        GraphicalObject tempObject = null;
        double tempDistance = Double.MAX_VALUE;
        double currentDistance;

        for (GraphicalObject go : this.objects) {
            currentDistance = go.selectionDistance(mousePoint);
            if(currentDistance < tempDistance && currentDistance <= SELECTION_PROXIMITY){
                tempObject = go;
                tempDistance = currentDistance;
            }
        }
//        if (tempObject != null){
//            tempObject.setSelected(true);
//        }
        return tempObject;
    }

    // Pronađi da li u predanom objektu predana točka miša selektira neki hot-point.
    // Točka miša selektira onaj hot-point objekta kojemu je najbliža uz uvjet da ta
    // udaljenost nije veća od SELECTION_PROXIMITY. Vraća se indeks hot-pointa
    // kojeg bi predana točka selektirala ili -1 ako takve nema. Status selekcije
    // se pri tome NE dira.
    public int findSelectedHotPoint(GraphicalObject object, Point mousePoint) {
        int selectedIndex = -1;
        double tempDistance = Double.MAX_VALUE;
        double currentDistance;

        for (int i = 0; i < object.getNumberOfHotPoints(); i++){
            currentDistance = GeometryUtil.distanceFromPoint(object.getHotPoint(i), mousePoint);

            if(currentDistance < tempDistance && currentDistance <= SELECTION_PROXIMITY){
                tempDistance = currentDistance;
                selectedIndex = i;
            }
        }
        return selectedIndex;
    }

}

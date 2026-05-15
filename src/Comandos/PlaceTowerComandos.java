package Comandos;

import characters.Torreta;
import javafx.scene.layout.Pane;

public class PlaceTowerComandos implements Comandos {
    private Torreta torre;
    private Pane panel;
    private double x, y;
    private boolean colocada;

    public PlaceTowerComandos(Torreta torre, Pane panel, double x, double y) {
        this.torre = torre;
        this.panel = panel;
        this.x = x;
        this.y = y;
        this.colocada = false;
    }

    @Override
    public void ejecutar() {
        if (!colocada) {
            torre.getVistaImagen().setX(x);
            torre.getVistaImagen().setY(y);
            torre.getVistaImagenBomba().setX(x);
            torre.getVistaImagenBomba().setY(y);
            panel.getChildren().addAll(torre.getVistaImagen(), torre.getVistaImagenBomba());
            torre.setActiva(true);
            colocada = true; // CORREGIDO: Marcar como colocada después de ejecutar
            System.out.println("Torre colocada en (" + x + ", " + y + ")");
        }
    }

    @Override
    public void deshacer() {
        if (colocada) {
            panel.getChildren().removeAll(torre.getVistaImagen(), torre.getVistaImagenBomba());
            torre.setActiva(false);
            colocada = false;
            System.out.println("Torre retirada de (" + x + ", " + y + ")");
        }
    }

    public Torreta getTorre() {
        return torre;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public boolean estaColocada() { return colocada; }
}

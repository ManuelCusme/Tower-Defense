package characters;

import characters.interfaces.Health;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import java.util.Random;

public class Cozy extends VBox implements Health {
    private int hp;
    private int maxHp;
    private double x, y;
    private boolean vivo;
    private ImageView sprite;
    private ProgressBar barraDeVida;
    private double velocidad;
    private int numeroDeOleada;
    private int daño;
    private String tipo;

    public Cozy(int numeroDeOleada) {
        this.numeroDeOleada = numeroDeOleada;
        // Aumentar HP y daño base según la ronda
        this.maxHp = 50 + (numeroDeOleada * 25);
        this.hp = maxHp;
        this.velocidad = 1.0 + (numeroDeOleada * 0.3);
        this.daño = 50 + (numeroDeOleada * 10); // Daño aumentado
        this.vivo = true;
        this.x = 0;
        this.y = 0;

        determinarTipoPorOleada();
        inicializarSpritePorTipo();
        inicializarBarraDeVida();

        this.setSpacing(2);
        this.getChildren().addAll(barraDeVida, sprite);
        this.setPrefSize(40, 50);
    }

    private void determinarTipoPorOleada() {
        switch (numeroDeOleada) {
            case 1: tipo = "Scout"; break;
            case 2: tipo = "Warrior"; break;
            case 3: tipo = "Hunter"; break;
            case 4: tipo = "Berserker"; break;
            case 5: tipo = "Guardian"; break;
            case 6: tipo = "Assassin"; break;
            case 7: tipo = "Ranger"; break;
            case 8: tipo = "Juggernaut"; break;
            case 9: tipo = "Phantom"; break;
            case 10: tipo = "Destroyer"; break;
            case 11: tipo = "Overlord"; break;
            case 12: tipo = "Ultimate"; break;
            default: tipo = "Scout"; break;
        }
        ajustarEstadisticasPorTipo();
    }

    private void ajustarEstadisticasPorTipo() {
        switch (tipo) {
            case "Scout":
                velocidad *= 1.2;
                break;
            case "Warrior":
                maxHp = (int)(maxHp * 1.2);
                hp = maxHp;
                daño = (int)(daño * 1.2);
                break;
            case "Hunter":
                velocidad *= 1.5;
                daño = (int)(daño * 1.3);
                break;
            case "Berserker":
                daño *= 1.5;
                velocidad *= 1.2;
                break;
            case "Guardian":
                maxHp = (int)(maxHp * 1.8);
                hp = maxHp;
                velocidad *= 0.7;
                daño *= 1.4;
                break;
            case "Assassin":
                velocidad *= 2.0;
                daño *= 1.3;
                maxHp = (int)(maxHp * 0.8);
                hp = maxHp;
                break;
            case "Ranger":
                velocidad *= 1.3;
                daño *= 1.2;
                break;
            case "Juggernaut":
                maxHp = (int)(maxHp * 2.5);
                hp = maxHp;
                velocidad *= 0.5;
                daño *= 2.0;
                break;
            case "Phantom":
                velocidad *= 2.5;
                daño *= 1.5;
                maxHp = (int)(maxHp * 0.7);
                hp = maxHp;
                break;
            case "Destroyer":
                maxHp = (int)(maxHp * 2.0);
                hp = maxHp;
                daño *= 2.5;
                velocidad *= 0.8;
                break;
            case "Overlord":
                maxHp = (int)(maxHp * 3.0);
                hp = maxHp;
                daño *= 3.0;
                velocidad *= 0.8;
                break;
            case "Ultimate":
                maxHp = (int)(maxHp * 4.0);
                hp = maxHp;
                daño *= 4.0;
                velocidad *= 1.2;
                break;
        }
    }

    private void inicializarSpritePorTipo() {
        int numeroDeSprite;
        switch (tipo) {
            case "Scout": numeroDeSprite = 1; break;
            case "Warrior": numeroDeSprite = 2; break;
            case "Hunter": numeroDeSprite = 3; break;
            case "Berserker": numeroDeSprite = 4; break;
            case "Guardian": numeroDeSprite = 5; break;
            case "Assassin": numeroDeSprite = 6; break;
            case "Ranger": numeroDeSprite = 7; break;
            case "Juggernaut": numeroDeSprite = 8; break;
            case "Phantom": numeroDeSprite = 9; break;
            case "Destroyer": numeroDeSprite = 10; break;
            case "Overlord": numeroDeSprite = 11; break;
            case "Ultimate": numeroDeSprite = 12; break;
            default: numeroDeSprite = 1; break;
        }

        String rutaImagen = "./res/Pixel-Alien-" + numeroDeSprite + ".png";
        try {
            Image imagen = new Image(rutaImagen);
            sprite = new ImageView(imagen);
            sprite.setFitWidth(32);
            sprite.setFitHeight(32);
            sprite.setPreserveRatio(true);
        } catch (Exception e) {
            sprite = new ImageView();
            sprite.setFitWidth(32);
            sprite.setFitHeight(32);
        }
    }

    private void inicializarBarraDeVida() {
        barraDeVida = new ProgressBar(1.0);
        barraDeVida.setPrefWidth(32);
        barraDeVida.setPrefHeight(4);
        barraDeVida.setStyle("-fx-accent: red;");
    }

    public void recibirDaño(int daño) {
        hp -= daño;
        if (hp <= 0) {
            hp = 0;
            vivo = false;
            barraDeVida.setProgress(0);
            this.setOpacity(0.5);
        } else {
            barraDeVida.setProgress((double) hp / maxHp);
        }
    }

    public void actualizarPosicion(double x, double y) {
        this.x = x;
        this.y = y;
        this.setLayoutX(x);
        this.setLayoutY(y);
    }

    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public double getX() { return x; }
    public double getY() { return y; }
    public boolean estaVivo() { return vivo; }
    public double getVelocidad() { return velocidad; }
    public int getDaño() { return daño; }
    public String getTipo() { return tipo; }
    public int getNumeroDeOleada() { return numeroDeOleada; }
    public ImageView getSprite() { return sprite; }

    public void setHp(int nuevoHp) {
        this.hp = Math.max(0, nuevoHp);
        this.vivo = this.hp > 0;
        if (barraDeVida != null) {
            barraDeVida.setProgress((double) hp / maxHp);
        }
    }

    @Override
    public void aumentarSalud() {
        // Implementación del método de la interfaz Health
        // Similar a la implementación de Alien pero adaptado a Cozy
        Random rand = new Random();
        int temp = rand.nextInt(11);
        if (temp > 9) {
            // Aumentar la salud en un 20% de la salud máxima
            int cantidadCuracion = (int)(maxHp * 0.2);
            hp = Math.min(hp + cantidadCuracion, maxHp);
            barraDeVida.setProgress((double) hp / maxHp);
            System.out.println(tipo + " recuperó " + cantidadCuracion + " de salud!");
        }
    }
}

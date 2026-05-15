package characters;

import javafx.animation.PathTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

public class Torreta {
    private int ataque;
    private int costo;
    private int nivel;
    private int alcance;
    private double velocidadDisparo;
    private String tipoTorre;
    private String nombreArchivo;
    private Image imagen;
    private ImageView vistaImagen;
    private String nombreArchivoBomba;
    private Image imagenBomba;
    private ImageView vistaImagenBomba;
    private long tiempoUltimoDisparo; // Para el sistema de cooldown
    private boolean activa = true; // NUEVO: indica si la torre está activa en el mapa

    // Constructor por defecto (torre básica)
    public Torreta() {
        this("Basic");
    }

    // Constructor con tipo de torre
    public Torreta(String tipo) {
        this.tipoTorre = tipo;
        this.nivel = 1;
        this.tiempoUltimoDisparo = 0;
        inicializarEstadisticasTorre();
        inicializarImagenes();
    }

    private void inicializarEstadisticasTorre() {
        switch (tipoTorre) {
            case "Basic":
                this.ataque = 50;
                this.costo = 40;
                this.alcance = 150;
                this.velocidadDisparo = 1.0;
                this.nombreArchivo = "./res/launcher.png";
                break;
            case "Rapid":
                this.ataque = 25;
                this.costo = 60;
                this.alcance = 120;
                this.velocidadDisparo = 0.5; // Dispara más rápido
                this.nombreArchivo = "./res/launcher.png";
                break;
            case "Heavy":
                this.ataque = 100;
                this.costo = 80;
                this.alcance = 130;
                this.velocidadDisparo = 2.0;
                this.nombreArchivo = "./res/launcher.png";
                break;
            case "Sniper":
                this.ataque = 150;
                this.costo = 120;
                this.alcance = 250;
                this.velocidadDisparo = 3.0;
                this.nombreArchivo = "./res/launcher.png";
                break;
        }
        this.nombreArchivoBomba = "./res/bomb.gif";
    }

    private void inicializarImagenes() {
        this.imagen = new Image(nombreArchivo);
        this.vistaImagen = new ImageView(imagen);
        this.vistaImagen.setFitWidth(32);
        this.vistaImagen.setFitHeight(32);

        this.imagenBomba = new Image(nombreArchivoBomba);
        this.vistaImagenBomba = new ImageView(imagenBomba);
        this.vistaImagenBomba.setFitWidth(16);
        this.vistaImagenBomba.setFitHeight(16);
    }

    // Sistema de mejoras
    public void mejorar() {
        nivel++;
        ataque += (nivel * 10);
        alcance += 20;
        velocidadDisparo *= 0.9; // Reduce el tiempo entre disparos
    }

    public boolean puedeMetorar() {
        return nivel < 3;
    }

    public int getCostoMejora() {
        return costo * nivel;
    }

    // Getters y setters existentes mejorados
    public int getAtaque() { return ataque * nivel; }
    public int getCosto() { return costo; }
    public int getNivel() { return nivel; }
    public int getAlcance() { return alcance + (nivel * 10); }
    public double getVelocidadDisparo() { return velocidadDisparo; }
    public String getTipoTorre() { return tipoTorre; }
    public String getNombreArchivo() { return nombreArchivo; }
    public Image getImagen() { return imagen; }
    public ImageView getVistaImagen() { return vistaImagen; }
    public String getNombreArchivoBomba() { return nombreArchivoBomba; }
    public Image getImagenBomba() { return imagenBomba; }
    public ImageView getVistaImagenBomba() { return vistaImagenBomba; }

    public void setNivel(int nivel) {
        this.nivel = nivel;
        // Recalcular estadísticas basadas en el nivel
        inicializarEstadisticasTorre();
        for (int i = 1; i < nivel; i++) {
            ataque = (int)(ataque * 1.5);
            alcance = (int)(alcance * 1.1);
            velocidadDisparo *= 1.2;
        }
    }

    // Método de ataque mejorado
    public void atacar(Cozy cozy) {
        cozy.recibirDaño(ataque);
    }

    // Verificar si un enemigo está en rango
    public boolean estaEnRango(Cozy cozy) {
        // Obtener la posición real del enemigo (incluye el desplazamiento por la animación)
        double cozyX = cozy.getLayoutX() + cozy.getTranslateX();
        double cozyY = cozy.getLayoutY() + cozy.getTranslateY();

        // Obtener el centro de la torre
        double torreX = vistaImagen.getX() + (vistaImagen.getFitWidth() / 2);
        double torreY = vistaImagen.getY() + (vistaImagen.getFitHeight() / 2);

        // Calcular la distancia entre la torre y el enemigo
        double distancia = Math.sqrt(
            Math.pow(torreX - cozyX, 2) +
            Math.pow(torreY - cozyY, 2)
        );
        return distancia <= alcance;
    }

    // Métodos de PathTransition existentes
    public Path crearRutaBombas(double x, double y) {
        Path ruta = new Path();
        MoveTo aparicion = new MoveTo(x, y);
        LineTo linea1 = new LineTo(0, 0);
        ruta.getElements().addAll(aparicion, linea1);
        return ruta;
    }

    public PathTransition transicionRutaBomba() {
        Path ruta = crearRutaBombas(getVistaImagen().getX(), getVistaImagen().getY());
        PathTransition pt2 = new PathTransition(Duration.millis(5000), ruta, getVistaImagenBomba());
        return pt2;
    }

    // Información de la torre para UI
    public String getInfo() {
        return String.format("Torre %s Nv.%d\nDaño: %d\nAlcance: %d\nVel. Disparo: %.1f",
                           tipoTorre, nivel, ataque, alcance, velocidadDisparo);
    }

    // Métodos para el sistema de cooldown
    public boolean puedeDisparar() {
        long tiempoActual = System.currentTimeMillis();
        // Convertir velocidadDisparo a milisegundos (velocidadDisparo está en segundos)
        long enfriamiento = (long)(velocidadDisparo * 1000);
        return activa && (tiempoActual - tiempoUltimoDisparo >= enfriamiento);
    }

    public void setTiempoUltimoDisparo(long tiempo) {
        this.tiempoUltimoDisparo = tiempo;
    }

    public long getTiempoUltimoDisparo() {
        return tiempoUltimoDisparo;
    }

    // NUEVO: activar/desactivar torre
    public boolean estaActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
        if (vistaImagen != null) {
            vistaImagen.setOpacity(activa ? 1.0 : 0.5);
        }
    }
}

package Juego;

public class GameManager {
    private int vidaJugador;
    private int vidaMaxima;
    private int puntuacion;
    private int dinero;
    private int oleadaActual;
    private int oleadasMaximas = 12; // Límite de 12 rondas
    private boolean juegoTerminado;
    private boolean juegoPausado;

    public GameManager() {
        this.vidaMaxima = 1000;
        this.vidaJugador = vidaMaxima;
        this.puntuacion = 0;
        this.dinero = 200; // Reducido de 500 a 200 para aumentar dificultad
        this.oleadaActual = 1;
        this.juegoTerminado = false;
        this.juegoPausado = false;
    }

    public void takeDamage(int daño) {
        this.vidaJugador = Math.max(0, this.vidaJugador - daño);
        System.out.println("Jugador recibió " + daño + " de daño. Vida restante: " + this.vidaJugador);
        if (this.vidaJugador <= 0) {
            this.juegoTerminado = true;
            System.out.println("JUEGO TERMINADO - ¡El jugador se quedó sin vida!");
        }
    }

    public void healPlayer(int curacion) {
        vidaJugador = Math.min(vidaMaxima, vidaJugador + curacion);
    }

    public void addScore(int puntos) {
        puntuacion += puntos;
    }

    public void addMoney(int cantidad) {
        dinero += cantidad;
    }

    public boolean spendMoney(int cantidad) {
        if (dinero >= cantidad) {
            dinero -= cantidad;
            return true;
        }
        return false;
    }

    public void nextWave() {
        if (oleadaActual < oleadasMaximas) {
            oleadaActual++;
            // Bonus de dinero por sobrevivir la oleada
            addMoney(50 + (oleadaActual * 25));
        } else {
            // Juego completado exitosamente
            juegoTerminado = true;
        }
    }

    public void enemyKilled(String tipoEnemigo, int numeroOleada) {
        int puntoBase = 10;
        int bonusOleada = numeroOleada * 2;
        int bonusTipo = getBonusPorTipo(tipoEnemigo);

        int puntosTotales = puntoBase + bonusOleada + bonusTipo;
        addScore(puntosTotales);
        addMoney(puntosTotales / 2); // Dinero = mitad de los puntos
    }

    private int getBonusPorTipo(String tipoEnemigo) {
        switch (tipoEnemigo) {
            case "Basic": return 5;
            case "Fast": return 8;
            case "Tank": return 15;
            case "Boss": return 30;
            default: return 5;
        }
    }

    /**
     * Reinicia el GameManager a sus valores iniciales
     * para permitir comenzar una nueva partida
     */
    public void reset() {
        this.vidaJugador = vidaMaxima;
        this.puntuacion = 0;
        this.dinero = 200;
        this.oleadaActual = 1;
        this.juegoTerminado = false;
        this.juegoPausado = false;
        System.out.println("GameManager reiniciado: vida, dinero y puntuación restablecidos");
    }

    // Getters
    public int getPlayerHealth() { return vidaJugador; }
    public int getMaxHealth() { return vidaMaxima; }
    public int getScore() { return puntuacion; }
    public int getMoney() { return dinero; }
    public int getCurrentWave() { return oleadaActual; }
    public boolean isGameOver() { return juegoTerminado; }
    public boolean isGamePaused() { return juegoPausado; }
    public int getMaxWaves() { return oleadasMaximas; }

    // Setters
    public void setGamePaused(boolean pausado) { this.juegoPausado = pausado; }
    public void setGameOver() { this.juegoTerminado = true; }

    public boolean isLastWave() {
        return oleadaActual >= oleadasMaximas;
    }

    public String getGameStats() {
        return String.format("Vida: %d/%d | Puntuación: %d | Dinero: $%d | Oleada: %d",
                           vidaJugador, vidaMaxima, puntuacion, dinero, oleadaActual);
    }
}

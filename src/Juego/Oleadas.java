package Juego;

import characters.Cozy;
import Estructuras.CircularQueue;

public class Oleadas {
    private CircularQueue colaCozy;
    private int numeroOleada;
    private int enemigosTotal;
    private int enemigosEliminados;
    private int enemigosProcesados;
    private boolean completada;
    private int recompensaBase;

    public Oleadas(int numeroOleada, int numEnemigos) {
        this.numeroOleada = numeroOleada;
        this.enemigosTotal = Math.max(numEnemigos, 8 + (numeroOleada * 3)); // Garantizar mínimo de enemigos
        this.enemigosEliminados = 0;
        this.enemigosProcesados = 0;
        this.completada = false;
        this.recompensaBase = 100;

        int capacidad = Math.max(enemigosTotal + 5, 30);
        this.colaCozy = new CircularQueue(capacidad);
        generarEnemigos(enemigosTotal);
    }

    private void generarEnemigos(int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            Cozy cozy = new Cozy(numeroOleada);
            colaCozy.encolar(cozy);
        }
        System.out.println("Generados " + cantidad + " enemigos para la oleada " + numeroOleada);
    }

    public void enemyKilled() {
        enemigosEliminados++;
        enemigosProcesados++;
        verificarCompletadoOleada();
    }

    public void enemyEscaped() {
        enemigosProcesados++;
        verificarCompletadoOleada();
    }

    private void verificarCompletadoOleada() {
        if (enemigosProcesados >= enemigosTotal) {
            completada = true;
            System.out.println("Oleada " + numeroOleada + " completada - Eliminados: " +
                             enemigosEliminados + "/" + enemigosTotal);
        }
    }

    public boolean isCompleted() {
        return completada || (enemigosProcesados >= enemigosTotal);
    }

    public double getProgress() {
        return (double) enemigosProcesados / enemigosTotal;
    }

    public int getCompletionReward() {
        int bonusOleada = numeroOleada * 50;
        int bonusEfectividad = (int) ((double) enemigosEliminados / enemigosTotal * 100);
        return recompensaBase + bonusOleada + bonusEfectividad;
    }

    public CircularQueue getCozyQueue() {
        return colaCozy;
    }

    public int getWaveNumber() {
        return numeroOleada;
    }

    public int getTotalEnemies() {
        return enemigosTotal;
    }

    public int getEnemiesKilled() {
        return enemigosEliminados;
    }

    public int getEnemiesProcessed() {
        return enemigosProcesados;
    }
}

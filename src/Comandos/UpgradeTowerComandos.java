package Comandos;

import characters.Torreta;

public class UpgradeTowerComandos implements Comandos {
    private Torreta torre;
    private int nivelAnterior;
    private int ataqueAnterior;
    private int alcanceAnterior;
    private double velocidadDisparoAnterior;

    public UpgradeTowerComandos(Torreta torre) {
        this.torre = torre;

        this.nivelAnterior = torre.getNivel();
        this.ataqueAnterior = torre.getAtaque();
        this.alcanceAnterior = torre.getAlcance();
        this.velocidadDisparoAnterior = torre.getVelocidadDisparo();
    }

    @Override
    public void ejecutar() {
        if (torre.puedeMetorar()) {
            torre.mejorar();
            System.out.println("Torre mejorada al nivel " + torre.getNivel());
        }
    }

    @Override
    public void deshacer() {
        torre.setNivel(nivelAnterior);
        // Asegurar que la torre sigue marcada como activa después de deshacer
        torre.setActiva(true);
        System.out.println("Mejora de torre deshecha, vuelve al nivel " + nivelAnterior);
    }

    public Torreta getTorre() {
        return torre;
    }

    public int getCostoMejora() {
        return torre.getCostoMejora();
    }
}

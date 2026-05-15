package Estructuras;

import characters.Cozy;

public class CircularQueue {
    private Cozy[] cola;
    private int frente;
    private int final_;
    private int tamaño;
    private int capacidad;

    public CircularQueue(int capacidad) {
        this.capacidad = capacidad;
        this.cola = new Cozy[capacidad];
        this.frente = 0;
        this.final_ = -1;
        this.tamaño = 0;
    }

    public boolean estaVacia() {
        return tamaño == 0;
    }

    public boolean estaLlena() {
        return tamaño == capacidad;
    }


    public boolean encolar(Cozy cozy) {
        if (estaLlena()) {
            System.out.println("Error: La cola está llena, no se puede agregar más enemigos");
            return false;
        }
        final_ = (final_ + 1) % capacidad;
        cola[final_] = cozy;
        tamaño++;
        return true;
    }

    public Cozy desencolar() {
        if (estaVacia())
            throw new IllegalStateException("La cola está vacía");
        Cozy cozy = cola[frente];
        cola[frente] = null;
        frente = (frente + 1) % capacidad;
        tamaño--;
        return cozy;
    }

    public Cozy ver() {
        if (estaVacia())
            throw new IllegalStateException("La cola está vacía");
        return cola[frente];
    }

    public int obtenerTamaño() {
        return tamaño;
    }

    public int obtenerCapacidad() {
        return capacidad;
    }
}

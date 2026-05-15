package Estructuras;

public class Stack<T> {
    private Object[] pila;
    private int tope;
    private int capacidad;

    public Stack(int capacidad) {
        this.capacidad = capacidad;
        this.pila = new Object[capacidad];
        this.tope = -1;
    }

    public boolean estaVacia() {
        return tope == -1;
    }

    public boolean estaLlena() {
        return tope == capacidad - 1;
    }

    public void apilar(T elemento) {
        if (estaLlena()) throw new IllegalStateException("La pila está llena");
        pila[++tope] = elemento;
    }

    @SuppressWarnings("unchecked")
    public T desapilar() {
        if (estaVacia()) throw new IllegalStateException("La pila está vacía");
        return (T) pila[tope--];
    }

    @SuppressWarnings("unchecked")
    public T ver() {
        if (estaVacia()) throw new IllegalStateException("La pila está vacía");
        return (T) pila[tope];
    }

    public int tamaño() {
        return tope + 1;
    }
}

package Comandos;

import Estructuras.Stack;

public class CommandManager {
    private Stack<Comandos> pilaDeshacer;
    private Stack<Comandos> pilaRehacer;
    private int tamañoMaximoHistorial;

    public CommandManager() {
        this(50); // Historial de 50 comandos por defecto
    }

    public CommandManager(int tamañoMaximoHistorial) {
        this.tamañoMaximoHistorial = tamañoMaximoHistorial;
        this.pilaDeshacer = new Stack<>(tamañoMaximoHistorial);
        this.pilaRehacer = new Stack<>(tamañoMaximoHistorial);
    }

    public void ejecutarComando(Comandos comando) {
        comando.ejecutar();
        pilaDeshacer.apilar(comando);


        pilaRehacer = new Stack<>(tamañoMaximoHistorial);

        System.out.println("Comando ejecutado: " + comando.getClass().getSimpleName());
    }

    public boolean puedeDeshacerse() {
        return !pilaDeshacer.estaVacia();
    }

    public boolean puedeRehacerse() {
        return !pilaRehacer.estaVacia();
    }

    public void deshacer() {
        if (puedeDeshacerse()) {
            Comandos comando = pilaDeshacer.desapilar();
            comando.deshacer();
            pilaRehacer.apilar(comando);
            System.out.println("Comando deshecho: " + comando.getClass().getSimpleName());
        }
    }

    public void rehacer() {
        if (puedeRehacerse()) {
            Comandos comando = pilaRehacer.desapilar();
            comando.ejecutar();
            pilaDeshacer.apilar(comando);
            System.out.println("Comando rehecho: " + comando.getClass().getSimpleName());
        }
    }

    public void limpiar() {
        pilaDeshacer = new Stack<>(tamañoMaximoHistorial);
        pilaRehacer = new Stack<>(tamañoMaximoHistorial);
    }

    public int obtenerContadorDeshacer() {
        return pilaDeshacer.tamaño();
    }

    public int obtenerContadorRehacer() {
        return pilaRehacer.tamaño();
    }
}

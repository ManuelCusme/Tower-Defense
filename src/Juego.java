import Comandos.UpgradeTowerComandos;
import characters.Torreta;
import characters.Cozy;
import Estructuras.CircularQueue;
import Juego.Oleadas;
import Juego.GameManager;
import Comandos.CommandManager;
import Comandos.PlaceTowerComandos;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.image.PixelReader;
import javafx.scene.shape.Path;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;


public class Juego extends Application {
    private final CommandManager gestorComandos;
    private final GameManager gestorJuego;
    private final List<Torreta> torres;
    private final List<CozyView> enemigosActivos;
    private final List<ImageView> proyectilesActivos;
    private Pane panelJuego;
    private Pane panelUI;
    private Text textoEstadisticas;
    private Text textoMensajeOleada;
    private Timeline generadorEnemigos;
    private Timeline bucleJuego;
    private int enemigosGenerados = 0;
    private boolean oleadaEnProgreso = false;
    private boolean juegoTerminadoManejado = false;
    private ProgressBar barraProgresoOleada;
    private String tipoTorreSeleccionada = "Basic";
    private Oleadas oleadaActual;
    private Button btnDeshacer;
    private Button btnRehacer;

    private static class CozyView {
        final Cozy cozy;
        final PathTransition transicion;

        CozyView(Cozy cozy, PathTransition transicion) {
            this.cozy = cozy;
            this.transicion = transicion;
        }
    }

    public Juego() {
        gestorComandos = new CommandManager();
        gestorJuego = new GameManager();
        torres = new ArrayList<>();
        enemigosActivos = new ArrayList<>();
        proyectilesActivos = new ArrayList<>();
    }

    @Override
    public void start(Stage escenario) {
        configurarJuego();
        configurarUI(escenario);
        iniciarPrimeraOleada();
    }

    private void configurarJuego() {
        juegoTerminadoManejado = false;
        System.out.println("=== TOWER DEFENSE INICIADO ===");
        System.out.println("Caso de estudio: Cozy enemies, CircularQueue, CommandStack");
    }

    private void configurarUI(Stage escenario) {
        Pane raiz = new Pane();
        panelJuego = new Pane();
        panelUI = new Pane();

        // Fondo del juego
        Image imagenMapa = new Image("./res/MapCropped.png");
        ImageView fondoMapa = new ImageView(imagenMapa);

        Image areas = new Image("./res/placeMapCroppedMinified.png", 1376, 736, true, true);
        Image cubierta = new Image("./res/MapCroppedForeground.png");
        ImageView cubierta2 = new ImageView(cubierta);

        // Textos de información
        textoEstadisticas = new Text();
        textoEstadisticas.setFont(Font.loadFont("file:./res/RetroComputer.ttf", 20));
        textoEstadisticas.setFill(Color.WHITE);
        textoEstadisticas.relocate(20, 20);

        textoMensajeOleada = new Text();
        textoMensajeOleada.setFont(Font.loadFont("file:./res/RetroComputer.ttf", 40));
        textoMensajeOleada.setFill(Color.YELLOW);
        textoMensajeOleada.relocate(500, 300);
        textoMensajeOleada.setVisible(false);

        // Barra de progreso de oleada
        barraProgresoOleada = new ProgressBar(0);
        barraProgresoOleada.setPrefWidth(200);
        barraProgresoOleada.relocate(20, 100);

        // Panel de torres
        configurarPanelTorres();

        // Panel de comandos
        configurarPanelComandos();

        // Layout principal
        panelJuego.setMinSize(1376, 736);
        raiz.getChildren().addAll(fondoMapa, panelJuego, cubierta2, panelUI);
        panelUI.getChildren().addAll(textoEstadisticas, textoMensajeOleada, barraProgresoOleada);

        // Eventos de mouse
        configurarEventosMouse(panelJuego, areas);

        Scene escena = new Scene(raiz);
        escenario.setScene(escena);
        escenario.setTitle("Tower Defense - Caso de Estudio Completo");
        escenario.setResizable(false);
        escenario.show();

        // Loop principal del juego
        configurarBucleJuego();
    }

    private void configurarPanelTorres() {
        VBox panelTorres = new VBox(10);
        panelTorres.relocate(1200, 20);

        Text tituloTorres = new Text("Torres Disponibles");
        tituloTorres.setFill(Color.WHITE);
        tituloTorres.setFont(Font.loadFont("file:./res/RetroComputer.ttf", 16));

        Button btnTorreBasica = new Button("Basic ($40)");
        Button btnTorreRapida = new Button("Rapid ($60)");
        Button btnTorrePesada = new Button("Heavy ($80)");
        Button btnTorreFrancotirador = new Button("Sniper ($120)");

        btnTorreBasica.setOnAction(event -> tipoTorreSeleccionada = "Basic");
        btnTorreRapida.setOnAction(event -> tipoTorreSeleccionada = "Rapid");
        btnTorrePesada.setOnAction(event -> tipoTorreSeleccionada = "Heavy");
        btnTorreFrancotirador.setOnAction(event -> tipoTorreSeleccionada = "Sniper");

        panelTorres.getChildren().addAll(tituloTorres, btnTorreBasica, btnTorreRapida, btnTorrePesada, btnTorreFrancotirador);
        panelUI.getChildren().add(panelTorres);
    }

    private void configurarPanelComandos() {
        HBox panelComandos = new HBox(10);
        panelComandos.relocate(20, 150);

        btnDeshacer = new Button("Deshacer");
        btnRehacer = new Button("Rehacer");
        Button btnMejorar = new Button("Mejorar Última");

        btnDeshacer.setOnAction(event -> {
            gestorComandos.deshacer();
            actualizarBotonesComandos(btnDeshacer, btnRehacer);
            actualizarUI();
        });

        btnRehacer.setOnAction(event -> {
            gestorComandos.rehacer();
            actualizarBotonesComandos(btnDeshacer, btnRehacer);
            actualizarUI();
        });

        btnMejorar.setOnAction(event -> {
            // Buscar la última torre activa colocada para mejorar
            Torreta objetivo = null;
            for (int i = torres.size() - 1; i >= 0; i--) {
                Torreta t = torres.get(i);
                if (t.estaActiva()) { objetivo = t; break; }
            }
            if (objetivo == null) {
                System.out.println("No hay torres para mejorar");
                return;
            }
            if (!objetivo.puedeMetorar()) {
                System.out.println("La torre ya está al máximo nivel");
                return;
            }
            int costo = objetivo.getCostoMejora();
            if (gestorJuego.spendMoney(costo)) {
                UpgradeTowerComandos cmd = new UpgradeTowerComandos(objetivo);
                gestorComandos.ejecutarComando(cmd);
                actualizarBotonesComandos(btnDeshacer, btnRehacer);
                actualizarUI();
                System.out.println("Torre mejorada. Dinero restante: $" + gestorJuego.getMoney());
            } else {
                System.out.println("Dinero insuficiente para mejorar la torre (costo $" + costo + ")");
            }
        });

        panelComandos.getChildren().addAll(btnDeshacer, btnRehacer, btnMejorar);
        panelUI.getChildren().add(panelComandos);
        actualizarBotonesComandos(btnDeshacer, btnRehacer);
    }

    private void actualizarBotonesComandos(Button btnDeshacer, Button btnRehacer) {
        btnDeshacer.setDisable(!gestorComandos.puedeDeshacerse());
        btnRehacer.setDisable(!gestorComandos.puedeRehacerse());
    }

    private void configurarEventosMouse(Pane panelJuego, Image areas) {
        panelJuego.setOnMouseClicked(event -> {
            if (gestorJuego.isGameOver()) return;

            double mouseX = event.getX();
            double mouseY = event.getY();

            if (puedeColocarTorreEn(mouseX, mouseY, areas)) {
                Torreta torre = new Torreta(tipoTorreSeleccionada);

                if (gestorJuego.spendMoney(torre.getCosto())) {
                    PlaceTowerComandos cmd = new PlaceTowerComandos(torre, panelJuego, mouseX, mouseY);
                    gestorComandos.ejecutarComando(cmd);
                    torres.add(torre);
                    actualizarBotonesComandos(btnDeshacer, btnRehacer);
                    System.out.println("Torre " + tipoTorreSeleccionada + " colocada. Dinero restante: $" + gestorJuego.getMoney());
                } else {
                    System.out.println("Dinero insuficiente para torre " + tipoTorreSeleccionada);
                }
            }
        });
    }

    private boolean puedeColocarTorreEn(double x, double y, Image areas) {
        try {
            PixelReader lector = areas.getPixelReader();
            Color colorPixel = lector.getColor((int)x, (int)y);

            // Permitir colocar torres en áreas que NO son caminos (negro)
            // Simplificar la lógica para permitir colocar torres en más áreas
            boolean esNegro = colorPixel.equals(Color.BLACK) ||
                    (colorPixel.getRed() < 0.1 && colorPixel.getGreen() < 0.1 && colorPixel.getBlue() < 0.1);

            return !esNegro; // Permitir colocar en cualquier lugar que no sea el camino
        } catch (Exception e) {
            System.out.println("Error al verificar posición para torre: " + e.getMessage());
            return false;
        }
    }

    private void iniciarPrimeraOleada() {
        mostrarMensajeOleada();
    }

    private void mostrarMensajeOleada() {
        int numOleada = gestorJuego.getCurrentWave();
        int cantidadEnemigos = calcularEnemigosParaOleada(numOleada);
        int enemigoHP = calcularHPParaOleada(numOleada);

        textoMensajeOleada.setText("Oleada " + numOleada + " ¡Inminente!\n" +
                "Enemigos: " + cantidadEnemigos + "\n" +
                "Vida Enemigo: " + enemigoHP + "\n" +
                "¡Prepárate!");
        textoMensajeOleada.setVisible(true);

        Timeline ocultarMensaje = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            textoMensajeOleada.setVisible(false);
            iniciarNuevaOleada();
        }));
        ocultarMensaje.play();
    }

    private void iniciarNuevaOleada() {
        int numOleada = gestorJuego.getCurrentWave();
        int cantidadEnemigos = calcularEnemigosParaOleada(numOleada);

        // Reiniciar correctamente todos los contadores y listas
        System.out.println("\n=== INICIANDO NUEVA OLEADA " + numOleada + " ===");

        // Limpiar enemigos residuales de la oleada anterior
        enemigosActivos.clear();
        proyectilesActivos.clear();

        // Reiniciar contadores
        enemigosGenerados = 0;
        oleadaEnProgreso = true;
        barraProgresoOleada.setProgress(0);

        // Resetear cooldown de torres para que disparen de inmediato en nueva oleada
        for (Torreta t : torres) {
            t.setTiempoUltimoDisparo(0L);
        }

        // Crear nueva oleada
        oleadaActual = new Oleadas(numOleada, cantidadEnemigos);

        System.out.println("Enemigos totales: " + cantidadEnemigos);
        System.out.println("HP base: " + calcularHPParaOleada(numOleada));
        System.out.println("Velocidad: " + calcularVelocidadParaOleada(numOleada));
        System.out.println("Enemigos en cola: " + oleadaActual.getCozyQueue().obtenerTamaño());

        // Iniciar spawning de enemigos usando CircularQueue
        iniciarGeneracionEnemigos();
    }

    // Cálculo escalado de enemigos por oleada (limitado a 12 rondas)
    private int calcularEnemigosParaOleada(int oleada) {
        // Aumentar dificultad: base 8 y +3 por cada oleada
        int enemigosBase = 8 + (3 * oleada);
        System.out.println("Calculando enemigos para oleada " + oleada + ": " + enemigosBase + " enemigos");
        return enemigosBase;
    }

    // Cálculo escalado de HP por oleada (cada tipo tiene sus propias estadísticas)
    private int calcularHPParaOleada(int oleada) {
        return 50 + (oleada * 25); // Base HP que se modifica en Cozy según el tipo
    }

    // Cálculo escalado de velocidad por oleada
    private double calcularVelocidadParaOleada(int oleada) {
        return Math.max(8000, 20000 - (oleada * 1000)); // Más rápido cada oleada
    }

    private void configurarBucleJuego() {
        bucleJuego = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            if (!gestorJuego.isGameOver() && !gestorJuego.isGamePaused()) {
                actualizarAtaquesTorres();
                actualizarUI();
                verificarCompletadoOleada();
            }
        }));
        bucleJuego.setCycleCount(Timeline.INDEFINITE);
        bucleJuego.play();
    }

    private void actualizarJuego() {
        // Actualizar estadísticas del juego
        textoEstadisticas.setText("Dinero: $" + gestorJuego.getMoney() + " | " +
                "Vida: " + gestorJuego.getPlayerHealth() + " | " +
                "Oleada: " + gestorJuego.getCurrentWave() + "/12");

        // Verificar si el juego ha terminado (victoria o derrota)
        if (gestorJuego.isGameOver()) {
            if (gestorJuego.getCurrentWave() >= gestorJuego.getMaxWaves()) {
                terminarJuego(true); // Victoria - completó las 12 rondas
            } else {
                terminarJuego(false); // Derrota - perdió antes de completar
            }
        }
    }

    private void terminarJuego(boolean victoria) {
        oleadaEnProgreso = false;
        String mensaje = victoria ? "¡Has defendido con éxito todas las oleadas!" : "Fin del juego. Intenta de nuevo.";
        System.out.println(mensaje);

        Text textoFinJuego = new Text(mensaje);
        textoFinJuego.setFont(Font.loadFont("file:./res/RetroComputer.ttf", 30));
        textoFinJuego.setFill(victoria ? Color.GREEN : Color.RED);
        textoFinJuego.relocate(400, 300);

        panelJuego.getChildren().add(textoFinJuego);

        // Detener todo el juego
        gestorComandos.limpiar();
        gestorJuego.setGameOver();
        if (generadorEnemigos != null) {
            generadorEnemigos.stop();
        }
        if (bucleJuego != null) {
            bucleJuego.stop();
        }

        // Añadir botón de reinicio cuando el jugador pierde o gana
        // Esto garantiza que siempre aparezca el botón al finalizar la partida
        agregarBotonReiniciar();
    }

    private void iniciarGeneracionEnemigos() {
        // Cancelar cualquier temporizador pendiente antes de comenzar
        if (generadorEnemigos != null) {
            generadorEnemigos.stop();
            System.out.println("Timer de spawn anterior detenido");
        }

        int maxEnemigos = oleadaActual.getTotalEnemies();
        System.out.println("=== Iniciando spawning de " + maxEnemigos + " enemigos para la oleada " + gestorJuego.getCurrentWave() + " ===");

        // Reiniciar contador de enemigos spawneados
        enemigosGenerados = 0;

        // Crear un nuevo timeline con menor intervalo para spawns más frecuentes
        generadorEnemigos = new Timeline(new KeyFrame(Duration.millis(800), e -> {
            CircularQueue cola = oleadaActual.getCozyQueue();

            // Verificar si hay enemigos en la cola y si no hemos alcanzado el máximo
            if (!cola.estaVacia() && enemigosGenerados < maxEnemigos && !gestorJuego.isGameOver()) {
                System.out.println(">>> Spawning enemigo " + (enemigosGenerados + 1) + "/" + maxEnemigos);
                generarEnemigoDesdeCola();
                enemigosGenerados++;
            } else if (enemigosGenerados >= maxEnemigos || cola.estaVacia()) {
                // Marcar spawn como finalizado
                System.out.println("=== Spawning finalizado: " + enemigosGenerados + "/" + maxEnemigos + " enemigos spawneados ===");
                generadorEnemigos.stop();
            }
        }));

        // Configurar el Timeline para que se repita indefinidamente hasta que se detenga manualmente
        generadorEnemigos.setCycleCount(Timeline.INDEFINITE);
        generadorEnemigos.play();
    }

    private void generarEnemigoDesdeCola() {
        System.out.println("Intentando sacar un enemigo de la cola...");
        CircularQueue cola = oleadaActual.getCozyQueue();

        if (cola == null) {
            System.out.println("ERROR CRÍTICO: La cola de enemigos es NULL");
            return;
        }

        System.out.println("Estado de la cola - Tamaño: " + cola.obtenerTamaño() + ", Vacía: " + cola.estaVacia());

        if (!cola.estaVacia()) {
            try {
                Cozy cozy = cola.desencolar();
                System.out.println("Enemigo extraído de la cola. Tipo: " + cozy.getTipo() + ", HP: " + cozy.getHp());

                // Crear animación por el camino con velocidad escalada
                Path camino = crearCaminoCompleto();
                double velocidadOleada = calcularVelocidadParaOleada(gestorJuego.getCurrentWave());

                PathTransition transicion = new PathTransition(
                        Duration.millis((long)velocidadOleada), camino, cozy
                );
                transicion.setCycleCount(1);

                transicion.setOnFinished(e -> {
                    // Si este enemigo ya fue removido (p.ej., porque murió), no aplicar daño
                    boolean sigueActivo = false;
                    for (CozyView cv : enemigosActivos) {
                        if (cv.cozy == cozy) { sigueActivo = true; break; }
                    }
                    if (!sigueActivo) {
                        return;
                    }

                    // Siempre aplicar daño al llegar al final
                    System.out.println("Cozy " + cozy.getTipo() + " llegó al final! Causando " + cozy.getDaño() + " de daño");
                    gestorJuego.takeDamage(cozy.getDaño());
                    oleadaActual.enemyEscaped();
                    // Marcarlo como no-vivo para que proyectiles tardíos no cuenten kill
                    try { cozy.setHp(0); } catch (Exception ignore) {}

                    // Eliminar completamente de todas las estructuras
                    panelJuego.getChildren().remove(cozy);
                    enemigosActivos.removeIf(cv -> cv.cozy == cozy);

                    System.out.println("Enemigos activos restantes: " + enemigosActivos.size());
                    // Comprobar Game Over inmediatamente tras el daño
                    manejarFinDeJuegoSiNecesario();
                    verificarCompletadoOleada();
                });

                panelJuego.getChildren().add(cozy);
                enemigosActivos.add(new CozyView(cozy, transicion));
                transicion.play();

                System.out.println("Cozy añadido al mapa y animación iniciada: " + cozy.getTipo() + " (HP: " + cozy.getHp() + ")");
            } catch (Exception e) {
                System.out.println("ERROR al procesar enemigo: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("ADVERTENCIA: Se intentó sacar un enemigo de una cola vacía.");
        }
    }

    // NUEVA animación de proyectiles corregida
    private void crearYDispararProyectil(Torreta torre, Cozy objetivo) {
        // Crear proyectil visual más grande y visible
        ImageView proyectil = new ImageView(new Image("./res/bomb.gif"));
        proyectil.setFitWidth(25);
        proyectil.setFitHeight(25);

        // Posición inicial del proyectil (centro de la torre)
        double torreX = torre.getVistaImagen().getX() + 16;
        double torreY = torre.getVistaImagen().getY() + 16;

        // Posición del enemigo objetivo
        double objetivoX = objetivo.getLayoutX() + objetivo.getTranslateX();
        double objetivoY = objetivo.getLayoutY() + objetivo.getTranslateY();

        // Posicionar proyectil en el centro de la torre
        proyectil.setLayoutX(torreX);
        proyectil.setLayoutY(torreY);

        // Agregar proyectil al juego
        panelJuego.getChildren().add(proyectil);
        proyectilesActivos.add(proyectil);

        // Animación del proyectil with TranslateTransition CORREGIDA
        TranslateTransition animacionProyectil = new TranslateTransition(Duration.millis(600), proyectil);

        // Calcular la diferencia de posición para el movimiento
        double deltaX = objetivoX - torreX;
        double deltaY = objetivoY - torreY;

        animacionProyectil.setByX(deltaX);
        animacionProyectil.setByY(deltaY);

        animacionProyectil.setOnFinished(e -> {
            // El proyectil impacta - CAUSAR DAÑO REAL
            if (objetivo.estaVivo()) {
                int daño = torre.getAtaque();
                objetivo.recibirDaño(daño);
                System.out.println("IMPACTO! Torre " + torre.getTipoTorre() +
                        " causó " + daño + " de daño. HP restante: " + objetivo.getHp());

                // Si el Cozy muere, eliminarlo completamente y detener su transición para evitar doble conteo
                if (!objetivo.estaVivo() || objetivo.getHp() <= 0) {
                    System.out.println("Cozy " + objetivo.getTipo() + " ELIMINADO!");

                    oleadaActual.enemyKilled();
                    gestorJuego.enemyKilled(objetivo.getTipo(), objetivo.getNumeroDeOleada());

                    // Detener transición del objetivo si sigue activa
                    for (int i = 0; i < enemigosActivos.size(); i++) {
                        CozyView cv = enemigosActivos.get(i);
                        if (cv.cozy == objetivo) {
                            try {
                                cv.transicion.stop();
                            } catch (Exception ignore) {}
                            break;
                        }
                    }

                    // Remover de pantalla y de todas las listas
                    panelJuego.getChildren().remove(objetivo);
                    enemigosActivos.removeIf(cv -> cv.cozy == objetivo);

                    System.out.println("Puntuación: " + gestorJuego.getScore() + ", Enemigos activos: " + enemigosActivos.size());
                }
            }

            // ELIMINAR EL PROYECTIL SIEMPRE
            panelJuego.getChildren().remove(proyectil);
            proyectilesActivos.remove(proyectil);
        });

        animacionProyectil.play();
    }

    private Path crearCaminoCompleto() {
        // Crear la ruta utilizando las coordenadas originales proporcionadas
        Path camino = new Path();

        // Punto de spawn original
        MoveTo inicio = new MoveTo(6.0, 430.0);

        // Líneas de la ruta original
        LineTo linea1 = new LineTo(85.0, 430.0);    // Primer movimiento horizontal
        LineTo linea2 = new LineTo(85.0, 80.0);     // Subida
        LineTo linea3 = new LineTo(265.0, 80.0);    // Movimiento horizontal superior
        LineTo linea4 = new LineTo(265.0, 682.5);   // Bajada larga
        LineTo linea5 = new LineTo(350.0, 682.5);   // Movimiento horizontal inferior
        LineTo linea6 = new LineTo(350.0, 600.0);   // Pequeña subida
        LineTo linea7 = new LineTo(800.0, 600.0);   // Movimiento horizontal largo
        LineTo linea8 = new LineTo(800.0, 425.5);   // Subida
        LineTo linea9 = new LineTo(1121.0, 425.5);  // Movimiento horizontal
        LineTo linea10 = new LineTo(1121.0, 300.0); // Movimiento final

        // Añadir todos los elementos de la ruta de una vez
        camino.getElements().addAll(inicio, linea1, linea2, linea3, linea4, linea5,
                linea6, linea7, linea8, linea9, linea10);

        return camino;
    }

    private void actualizarAtaquesTorres() {
        for (Torreta torre : torres) {
            // Ignorar torres retiradas/inactivas
            if (!torre.estaActiva()) {
                continue;
            }
            // Verificar si la torre puede disparar (cooldown)
            if (torre.puedeDisparar()) {
                Cozy objetivo = encontrarCozyMasCercano(torre);
                if (objetivo != null) {
                    // Crear proyectil visual que viaje hacia el enemigo
                    crearYDispararProyectil(torre, objetivo);

                    // Actualizar tiempo de último disparo para cooldown
                    torre.setTiempoUltimoDisparo(System.currentTimeMillis());
                }
            }
        }

        // Log periódico para diagnosticar
        if (System.currentTimeMillis() % 5000 < 100) {
            System.out.println("Torres activas: " + torres.size() + " | Enemigos activos: " + enemigosActivos.size());
        }
    }

    private Cozy encontrarCozyMasCercano(Torreta torre) {
        // Encuentra el enemigo más cercano dentro del rango
        Cozy masCercano = null;
        double distanciaMinima = Double.MAX_VALUE;

        for (CozyView cozyView : enemigosActivos) {
            Cozy cozy = cozyView.cozy;
            // Solo considerar enemigos vivos
            if (cozy.estaVivo()) {
                // Comprobar si está en rango
                if (torre.estaEnRango(cozy)) {
                    // Calcular distancia real
                    double distancia = Math.sqrt(
                            Math.pow(torre.getVistaImagen().getX() - cozy.getX(), 2) +
                                    Math.pow(torre.getVistaImagen().getY() - cozy.getY(), 2)
                    );

                    // Actualizar si es más cercano
                    if (distancia < distanciaMinima) {
                        distanciaMinima = distancia;
                        masCercano = cozy;
                    }
                }
            }
        }

        return masCercano;
    }

    private void verificarCompletadoOleada() {
        // Verificar estado actual
        boolean noHayEnemigosActivos = enemigosActivos.isEmpty();
        boolean colaVacia = oleadaActual.getCozyQueue().estaVacia();
        boolean jugadorVivo = gestorJuego.getPlayerHealth() > 0;

        // LOGS DETALLADOS para diagnosticar
        System.out.println("\n=== VERIFICAR COMPLETADO DE OLEADA ===");
        System.out.println("Oleada en progreso: " + oleadaEnProgreso);
        System.out.println("Enemigos activos en mapa: " + enemigosActivos.size() + " -> vacío? " + noHayEnemigosActivos);
        System.out.println("Cola de spawn: " + oleadaActual.getCozyQueue().obtenerTamaño() + " -> vacía? " + colaVacia);
        System.out.println("Jugador vivo: " + gestorJuego.getPlayerHealth() + "HP -> " + jugadorVivo);
        System.out.println("Game Over: " + gestorJuego.isGameOver());

        // La oleada se completa cuando no quedan enemigos en la cola NI en el mapa
        if (oleadaEnProgreso && noHayEnemigosActivos && colaVacia) {
            oleadaEnProgreso = false;
            System.out.println("\n*** OLEADA " + gestorJuego.getCurrentWave() + " COMPLETADA ***");

            // Verificar si el jugador sigue vivo para continuar
            if (jugadorVivo && !gestorJuego.isGameOver()) {
                int recompensa = oleadaActual.getCompletionReward();
                System.out.println("Recompensa: $" + recompensa);
                gestorJuego.addMoney(recompensa);

                if (gestorJuego.getCurrentWave() >= 12) {
                    System.out.println("¡JUEGO COMPLETADO CON ÉXITO!");
                    textoMensajeOleada.setText("¡FELICIDADES!\nHas completado las 12 rondas!\nPuntuación final: " + gestorJuego.getScore());
                    textoMensajeOleada.setVisible(true);
                    gestorJuego.setGameOver();
                    if (bucleJuego != null) bucleJuego.stop();
                    if (generadorEnemigos != null) generadorEnemigos.stop();
                    agregarBotonReiniciar();
                } else {
                    System.out.println("\n>>> PREPARANDO SIGUIENTE OLEADA EN 3 SEGUNDOS <<<");
                    gestorJuego.nextWave();
                    int siguienteOleada = gestorJuego.getCurrentWave();

                    textoMensajeOleada.setText("Oleada " + (siguienteOleada - 1) + " Completada!\nSiguiente oleada en 3 segundos...");
                    textoMensajeOleada.setVisible(true);

                    Timeline retrasoSiguienteOleada = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
                        System.out.println("\n*** INICIANDO TRANSICIÓN A OLEADA " + siguienteOleada + " ***");
                        if (gestorJuego.getPlayerHealth() > 0 && !gestorJuego.isGameOver()) {
                            textoMensajeOleada.setVisible(false);
                            mostrarMensajeOleada();
                        } else {
                            System.out.println("!!! ALERTA: Jugador sin vida, no se puede iniciar nueva oleada !!!");
                        }
                    }));
                    retrasoSiguienteOleada.setCycleCount(1);
                    retrasoSiguienteOleada.play();
                }
            } else {
                System.out.println("!!! Oleada completada pero jugador sin vida - Game Over !!!");
                manejarFinDeJuegoSiNecesario();
            }
        } else {
            System.out.println(">>> Oleada AÚN NO completada (esperando...)");
        }
        System.out.println("=============================\n");
    }

    // Centraliza el manejo de Game Over y evita duplicación
    private void manejarFinDeJuegoSiNecesario() {
        if (juegoTerminadoManejado) return;
        if (gestorJuego.getPlayerHealth() <= 0 || gestorJuego.isGameOver()) {
            juegoTerminadoManejado = true;
            System.out.println("\n!!! GAME OVER - JUGADOR SIN VIDA !!!");
            gestorJuego.setGameOver();

            // Detener todas las animaciones
            if (bucleJuego != null) bucleJuego.stop();
            if (generadorEnemigos != null) generadorEnemigos.stop();

            try {
                for (CozyView cv : new ArrayList<>(enemigosActivos)) {
                    cv.transicion.stop();
                    panelJuego.getChildren().remove(cv.cozy);
                }
                enemigosActivos.clear();
                proyectilesActivos.forEach(proj -> panelJuego.getChildren().remove(proj));
                proyectilesActivos.clear();
            } catch (Exception ignored) {
                // Ignorar errores de limpieza
            }

            // Mostrar mensaje de game over más visible
            Text textoGameOver = new Text("¡JUEGO TERMINADO!");
            textoGameOver.setFont(Font.loadFont("file:./res/RetroComputer.ttf", 50));
            textoGameOver.setFill(Color.RED);
            textoGameOver.relocate(500, 300);
            panelJuego.getChildren().add(textoGameOver);

            Text textoPuntuacion = new Text("Puntuación Final: " + gestorJuego.getScore());
            textoPuntuacion.setFont(Font.loadFont("file:./res/RetroComputer.ttf", 30));
            textoPuntuacion.setFill(Color.WHITE);
            textoPuntuacion.relocate(540, 350);
            panelJuego.getChildren().add(textoPuntuacion);

            // Crear un botón grande y colorido para reiniciar
            Button botonReiniciar = new Button("REINICIAR PARTIDA");
            botonReiniciar.setPrefSize(300, 60);
            botonReiniciar.setFont(Font.loadFont("file:./res/RetroComputer.ttf", 24));
            botonReiniciar.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
            botonReiniciar.relocate(530, 420);

            botonReiniciar.setOnAction(e -> reiniciarJuego());

            // Añadir directamente al gamePane para asegurar que sea visible
            panelJuego.getChildren().add(botonReiniciar);

            // Efecto visual para hacer que el botón destaque
            Timeline efectoParpadeo = new Timeline(
                new KeyFrame(Duration.seconds(0.5), evt -> botonReiniciar.setOpacity(0.7)),
                new KeyFrame(Duration.seconds(1.0), evt -> botonReiniciar.setOpacity(1.0))
            );
            efectoParpadeo.setCycleCount(Timeline.INDEFINITE);
            efectoParpadeo.play();

            System.out.println("Botón de reinicio añadido al panelJuego");
        }
    }

    // Muestra un botón para reiniciar la partida manualmente
    private void agregarBotonReiniciar() {
        Button botonReiniciar = new Button("Jugar de Nuevo");
        botonReiniciar.setFont(Font.loadFont("file:./res/RetroComputer.ttf", 20));
        botonReiniciar.setPrefSize(200, 50);
        botonReiniciar.relocate(588, 400); // Centrado aproximado

        botonReiniciar.setOnAction(e -> {
            System.out.println("Reiniciando juego por botón...");

            // Limpiar elementos del juego anterior
            try {
                if (generadorEnemigos != null) generadorEnemigos.stop();
                if (bucleJuego != null) bucleJuego.stop();
            } catch (Exception ignore) {}

            panelJuego.getChildren().clear();
            panelUI.getChildren().clear();
            torres.clear();
            enemigosActivos.clear();
            proyectilesActivos.clear();

            // Reiniciar el juego desde ronda 1
            Stage escenario = (Stage) panelJuego.getScene().getWindow();
            configurarJuego();
            configurarUI(escenario);
            iniciarPrimeraOleada();
        });

        // Evitar añadir múltiples botones de reinicio si ya existe uno
        if (!panelUI.getChildren().contains(botonReiniciar)) {
            panelUI.getChildren().add(botonReiniciar);
        }
    }

    /**
     * Reinicia la partida después de perder
     */
    private void reiniciarJuego() {
        System.out.println("Reiniciando juego...");

        // Detener todas las animaciones y timelines activos
        if (generadorEnemigos != null) {
            generadorEnemigos.stop();
        }
        if (bucleJuego != null) {
            bucleJuego.stop();
        }

        // Limpiar elementos visuales
        panelJuego.getChildren().clear();

        // Limpiar colecciones de datos
        torres.clear();
        enemigosActivos.clear();
        proyectilesActivos.clear();

        // Resetear variables de control
        enemigosGenerados = 0;
        oleadaEnProgreso = false;
        juegoTerminadoManejado = false;
        tipoTorreSeleccionada = "Basic";

        // Reiniciar los gestores
        gestorComandos.limpiar();
        gestorJuego.reset();

        // Reiniciar la interfaz de usuario
        Stage escenario = (Stage) panelJuego.getScene().getWindow();
        configurarJuego();
        configurarUI(escenario);
        iniciarPrimeraOleada();

        System.out.println("Juego reiniciado con éxito!");
    }

    private void actualizarUI() {
        // Actualizar texto de estadísticas con progreso de rondas
        textoEstadisticas.setText("Dinero: $" + gestorJuego.getMoney() + " | " +
                "Vida: " + gestorJuego.getPlayerHealth() + " | " +
                "Oleada: " + gestorJuego.getCurrentWave() + "/12" +
                "\nDeshacer: " + gestorComandos.obtenerContadorDeshacer() +
                " | Rehacer: " + gestorComandos.obtenerContadorRehacer() +
                "\nProyectiles: " + proyectilesActivos.size());

        // Actualizar barra de progreso
        if (oleadaActual != null) {
            barraProgresoOleada.setProgress(oleadaActual.getProgress());
        }

        // Verificar Game Over centralizado
        manejarFinDeJuegoSiNecesario();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

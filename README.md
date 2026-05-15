# Tower Defense

Este proyecto es un juego tipo **Tower Defense** desarrollado en Java. El objetivo del juego es defenderse de oleadas de enemigos colocando y mejorando torres estratégicamente sobre el mapa. El código sigue una arquitectura modular y contiene recursos gráficos propios (sprites, mapas, tipografía).

## Estructura general del proyecto

- **src/**
  - **Comandos/**: Contiene clases relacionadas con la gestión de comandos del juego, por ejemplo:
    - `CommandManager.java`: Gestor principal de comandos.
    - `Comandos.java`, `PlaceTowerComandos.java`, `UpgradeTowerComandos.java`: Definición y gestión de órdenes como colocar torres, mejorarlas, etc.
  - **Estructuras/**: Estructuras de datos auxiliares.
    - `CircularQueue.java`, `Stack.java`: Implementaciones de estructuras como colas o pilas, probablemente usadas para manejar oleadas o historia de acciones.
  - **Juego/**: Núcleo de la lógica del juego.
    - `GameManager.java`: Manejo de la lógica principal del estado y avance del juego.
    - `Oleadas.java`: Manejo de las oleadas/enemigos.
    - `Juego.java`: Entrada principal, quizás la ventana gráfica y control del flujo de ejecución.
  - **characters/**: Clases relacionadas con entidades del juego.
    - `Cozy.java`, `Torreta.java`: Personajes y torres del juego, con sus atributos y comportamientos.
    - `interfaces/Health.java`: Interfaces para características compartidas como la salud.
  - **res/**: Recursos (sprites, mapas, fuentes tipográficas, etc).
    - Archivos `.png`, `.gif`, `.ttf` para gráficos, fondo, aliens, torretas y decoraciones visuales del juego.
- **build.xml**: Script de construcción/automatización, compatible con Ant.
- **.gitignore**, **TowerDefenseGrupo#1.iml**: Archivos de configuración típicos para control de versiones e IDE.

## Principales características

- **Juego en Java:** Fácil de compilar y correr en cualquier sistema compatible con Java.
- **Arquitectura modular:** Separación clara entre lógica, comandos, estructuras y personajes.
- **Soporte visual:** Mapa, sprites y recursos gráficos incluidos, personalizables en `src/res/`.
- **Sistema de comandos:** El control de acciones se basa en comandos, facilitando la extensión y el mantenimiento del código.
- **Gestión de oleadas:** Enemigos llegan en oleadas y el jugador debe responder mejorando o colocando torres.
- **Extensible:** La organización del código permite agregar fácilmente nuevos tipos de torres, enemigos o mecánicas.

## Requisitos

- Java 8 o superior
- (Opcional) IDE recomendado: IntelliJ IDEA o similar

## Cómo ejecutar

1. Clona este repositorio:
   ```sh
   git clone https://github.com/ManuelCusme/Tower-Defense.git
   ```
2. Abre el proyecto en tu IDE Java favorito.
3. Asegúrate de tener los recursos en `src/res/` y de que tu IDE lo incluya en el classpath.
4. Ejecuta la clase principal `Juego.java` o usa el archivo `build.xml` con Ant para compilar y ejecutar.

## Créditos

Desarrollado por ManuelCusme y colaboradores.

---

¡Siéntete libre de contribuir, reportar bugs o proponer nuevas mecánicas!

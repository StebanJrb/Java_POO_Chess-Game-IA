# ♚ Chess — Juego de Ajedrez en Java

Un juego de ajedrez completo desarrollado en **Java** con **Swing**, construido sobre los principios de la **Programación Orientada a Objetos (POO)**.

## 🎯 Propósito del Proyecto

Este proyecto nace como una aplicación práctica de los conceptos fundamentales de la POO:

- **Abstracción:** La clase `Piece` define el comportamiento común de todas las piezas, mientras que cada pieza concreta (`Pawn`, `Rook`, `Knight`, `Bishop`, `Queen`, `King`) implementa su propia lógica de movimiento.
- **Encapsulamiento:** Cada clase gestiona su propio estado interno (`Board`, `GameManager`, `Position`), exponiendo solo lo necesario.
- **Herencia:** Todas las piezas heredan de la clase abstracta `Piece`, reutilizando funcionalidad compartida como el cálculo de movimientos deslizantes.
- **Polimorfismo:** El tablero trabaja con referencias a `Piece` sin conocer el tipo específico, delegando el cálculo de movimientos a cada subclase.

## 🎮 Características

- ✅ Menú principal con selección de modo de juego
- ✅ Movimientos legales resaltados al seleccionar una pieza
- ✅ Captura de piezas con panel de piezas capturadas
- ✅ Captura al paso (en passant)
- ✅ Enroque corto y largo
- ✅ Promoción de peón (elección entre Dama, Torre, Alfil o Caballo)
- ✅ Detección de jaque, jaque mate y tablas por ahogado
- ✅ Indicador de último movimiento realizado

## 🧱 Estructura del Proyecto

```
src/
├── main/java/org/Chess/
│   ├── Main.java                  # Punto de entrada
│   ├── model/
│   │   ├── PieceColor.java        # Enum de colores
│   │   ├── PieceType.java         # Enum de tipos de pieza
│   │   ├── Position.java          # Posición en el tablero
│   │   ├── Move.java              # Registro de un movimiento
│   │   ├── Piece.java             # Clase abstracta base
│   │   ├── Board.java             # Tablero 8x8
│   │   ├── GameManager.java       # Lógica del juego
│   │   └── pieces/
│   │       ├── Pawn.java
│   │       ├── Rook.java
│   │       ├── Knight.java
│   │       ├── Bishop.java
│   │       ├── Queen.java
│   │       └── King.java
│   └── gui/
│       ├── ChessApp.java          # Ventana principal
│       ├── MainMenuPanel.java     # Menú inicial
│       ├── BoardPanel.java        # Renderizado del tablero
│       ├── GameWindow.java        # Integración de componentes
│       ├── CapturedPiecesPanel.java
│       ├── PromotionDialog.java
│       └── PieceImageLoader.java  # Carga de imágenes locales
└── pieces/                        # Imágenes PNG de las piezas
```

## 🚀 Cómo Ejecutar

1. Abre el proyecto en **IntelliJ IDEA**.
2. Ejecuta la clase `org.Chess.Main`.

## 🔮 Próximos Pasos

El siguiente objetivo de este proyecto es **entrenar una Inteligencia Artificial** capaz de jugar partidas contra el jugador humano. La arquitectura actual ya contempla el modo "Jugador vs IA" en el menú principal, por lo que la infraestructura está preparada para integrar el motor de decisiones.

Algunas ideas para la implementación de la IA:

- **Algoritmo Minimax** con poda alfa-beta para evaluación de movimientos.
- **Función de evaluación** basada en valor material, posición de las piezas y control del centro.
- Posible integración de **redes neuronales** para aprendizaje por refuerzo.

---

Desarrollado por **Steban** 🧑‍💻

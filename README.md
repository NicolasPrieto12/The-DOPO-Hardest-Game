# 🎮 The DOPO Hardest Game

> Inspirado en *The World's Hardest Game*, desarrollado en Java con Swing.

**Autores:**
- 👤 Nicolás Prieto Ramos
- 👤 Sebastián Peña Sánchez

---

## Descripción

The DOPO Hardest Game es un juego de habilidad y reflejos en 2D donde el jugador controla un cuadrado rojo y debe recoger todas las monedas del nivel para luego llegar a la zona final, evitando a los enemigos. El juego cuenta con 3 modos de juego, 4 niveles de dificultad progresiva y múltiples elementos especiales.

---

## Modos de Juego

El juego ofrece **3 modos**:

| Modo | Descripción | Controles |
|------|-------------|-----------|
| **Un Jugador** | El jugador clásico: recoge monedas y llega a la meta antes de que se acabe el tiempo. | Flechas del teclado |
| **Jugador vs Jugador (PvP)** | Dos jugadores compiten en el mismo tablero. Cada uno inicia en un extremo opuesto y debe llegar al extremo contrario. Gana quien llegue primero. | J1: Flechas · J2: WASD |
| **Jugador vs Máquina (PvM)** | El jugador compite contra una IA con dos perfiles de dificultad: **RANDOM** (movimiento aleatorio) y **EXPERT** (busca monedas y evita paredes). | Flechas del teclado |

---

## Niveles

El juego tiene **4 niveles** de dificultad progresiva, disponibles en los 3 modos:

### Nivel 1 — Fácil
- Tablero con obstáculos en forma de L
- 4 monedas amarillas
- 2 enemigos básicos (horizontal y vertical)

### Nivel 2 — Intermedio
- Laberinto más complejo con pasillos estrechos
- 8 monedas (6 normales + 2 SkinCoins en PvP/PvM)
- 1 enemigo básico + 3 enemigos patrulleros
- 1 zona de checkpoint central

### Nivel 3 — Difícil
- Laberinto en zigzag con pasillos muy angostos
- 8 monedas amarillas + 1 GreenCoin
- 5 SliderEnemies + 5 AcceleratedEnemies
- 6 bombas estáticas

### Nivel 4 — Muy Difícil
- Diseño en forma de cruz con corredores en zigzag
- 10 monedas + 1 SkinCoin + 1 GreenCoin
- 8 AcceleratedEnemies
- 4 bombas + 2 fuentes de vida (LifeSource)

---

## Tipos de Monedas

| Moneda | Color | Efecto |
|--------|-------|--------|
| **Coin** (normal) | 🟡 Amarilla | Debe recogerse para poder completar el nivel |
| **SkinCoin** | 🔵 Azul | Transforma al jugador en **Inky** (azul): velocidad y tamaño ×1.5 |
| **GreenCoin** | 🟢 Verde | Transforma al jugador en **Clyde** (verde): resistente, absorbe el primer golpe sin morir |

---

## Tipos de Enemigos

| Enemigo | Color | Comportamiento | Dificultad |
|---------|-------|----------------|------------|
| **Enemy** (básico) | 🔵 Azul | Se mueve en línea recta (horizontal o vertical), rebota en paredes | Baja |
| **PatrolEnemy** | 🔵 Azul oscuro | Sigue una ruta circular predefinida (waypoints) | Media |
| **SliderEnemy** | 🔵 Azul | Se desplaza exclusivamente en vertical, rebota arriba y abajo | Baja |
| **AcceleratedEnemy** | 🔵 Azul | Se mueve al doble de velocidad (6 px/tick), rebota en paredes | Alta |

---

## Elementos Especiales

| Elemento | Descripción |
|----------|-------------|
| **CheckpointZone** 🟩 | Zona verde intermedia. Al pisarla, el jugador reaparece aquí al morir (conservando monedas ya recogidas) |
| **Bomb** 💣 | Bomba estática negra. Mata al instante al tocarla. No desaparece |
| **LifeSource** ❤️ | Corazón rojo. Otorga un escudo: el primer golpe de enemigo no mata, solo reposiciona |
| **StartZone** 🟦 | Zona azul de inicio. Zona segura donde reaparece el jugador |
| **EndZone** 🟦 | Zona azul de llegada. Completar el nivel requiere estar aquí con todas las monedas |

---

## Tipos de Jugador

| Tipo | Color | Velocidad | Tamaño | Descripción |
|------|-------|-----------|--------|-------------|
| **RED** | 🔴 Rojo | 3 px/tick | 20×20 | Estado base del jugador |
| **BLUE (Inky)** | 🔵 Azul | 4 px/tick | 30×30 | Obtenido con SkinCoin. Más rápido y grande |
| **GREEN (Clyde)** | 🟢 Verde | 3 px/tick | 20×20 | Obtenido con GreenCoin. Absorbe un golpe |

---

## Reglas Generales

- Cada nivel tiene un límite de **3 minutos** (180 segundos). Si se agota → `TIMEOUT`
- Al morir sin checkpoint: el jugador vuelve al inicio y las monedas se reinician
- Al morir con checkpoint activo: el jugador reaparece en el checkpoint conservando monedas
- En PvP/PvM: la muerte de un jugador **no afecta** las monedas del otro
- El juego se puede **reiniciar** en cualquier momento
- Se puede **guardar y cargar** la partida (modos Single y PvP)

---

## Pruebas

### Cobertura de Pruebas

El proyecto cuenta con **8 clases de prueba** con más de **40 casos de prueba** que cubren:

- Estado inicial del juego
- Lógica de muerte y respawn
- Sistema de checkpoints
- Recolección de monedas
- Condición de victoria
- Reinicio
- Progresión de niveles
- Tiempo agotado (TIMEOUT)
- Independencia de monedas en PvP/PvM
- Comportamiento de la máquina (RANDOM y EXPERT)
- Patrón Singleton

### Resultado de Coverage (IntelliJ)

> Ejecutar con: `Run > Run with Coverage` sobre el directorio `src/test/java/domain/`

> ⚠️ **IMPORTANTE:** Tomar el screenshot del reporte de coverage en IntelliJ y guardarlo como `docs/coverage.png` para que aparezca aqui.

![Coverage Report](docs/coverage.png)

Clases cubiertas por las pruebas:

| Clase | Tipo de prueba |
|-------|----------------|
| `Game` | Unitaria + Aceptacion |
| `Player` | Unitaria |
| `Coin` | Unitaria |
| `Enemy` | Unitaria |
| `Level` | Unitaria |
| `GamePvP` | Aceptacion |
| `GamePvM` | Aceptacion |
| `LevelPvP` | Aceptacion (indirecta) |

### Análisis Estático

El código fue analizado con las herramientas integradas de IntelliJ IDEA:

- Sin variables no utilizadas
- Sin imports innecesarios
- Javadoc completo en todas las clases pública
- Separación estricta entre capa de dominio y GUI (dominio sin imports de `java.awt` excepto `Rectangle` y `Graphics`)
- Uso de `final` en campos inmutables
- Uso de `List.of()` para listas inmutables donde aplica
- Nombres de variables y métodos descriptivos en español
- Constantes nombradas con `UPPER_SNAKE_CASE`
- Clases de prueba con nombres `should/shouldNot` para describir comportamiento esperado

### Análisis Dinámico

Pruebas de comportamiento en tiempo de ejecución verificadas:

| Escenario | Resultado |
|-----------|-----------|
| Jugador colisiona con enemigo → muere y reaparece | ✅ |
| Jugador recoge moneda → desaparece del tablero | ✅ |
| Jugador llega a EndZone sin monedas → no gana | ✅ |
| Jugador llega a EndZone con monedas → gana | ✅ |
| Timer llega a 0 → estado TIMEOUT | ✅ |
| Muerte de J1 en PvP → monedas de J2 intactas | ✅ |
| Máquina EXPERT se mueve hacia monedas | ✅ |
| Checkpoint activo → respawn en checkpoint | ✅ |
| LifeSource → absorbe primer golpe | ✅ |
| Reinicio → muertes y tiempo a cero | ✅ |
| Pausa → estado PAUSED, reanuda → PLAYING | ✅ |
| Singleton Game → misma instancia siempre | ✅ |
| Moneda ya recogida → no genera colisión | ✅ |
| Enemigo llega a borde → invierte dirección | ✅ |
| Enemigo llega a zona prohibida → invierte dirección | ✅ |

---

## Notas de Diseño

### Independencia de monedas en PvP/PvM
En los modos PvP y PvM, las monedas son **compartidas** en el tablero pero la muerte de un jugador **no reinicia** las monedas del otro. Esto se logra mediante `LevelPvP.resetPlayer()` que solo reposiciona al jugador sin tocar las monedas.

### Patrón Singleton en Game
`Game` usa Singleton para garantizar que solo exista una instancia del juego en modo single player. `Game.resetInstance()` permite destruir la instancia en los tests para garantizar aislamiento entre pruebas.

### IA de la Máquina
La máquina tiene dos perfiles:
- **RANDOM**: cambia de dirección cada 25 ticks, con 65% de probabilidad de ir hacia el objetivo
- **EXPERT**: siempre elige la dirección libre más cercana al objetivo, evitando paredes

Ambos perfiles detectan cuando están atascados (sin moverse por 8 ticks) y recalculan la dirección.

### Sistema de Escudos
Hay dos tipos de escudo:
1. **Clyde (GreenCoin)**: al absorber un golpe, la velocidad baja a 1 px/tick
2. **LifeSource**: al absorber un golpe, la velocidad NO cambia

Ambos otorgan 90 ticks de invencibilidad tras absorber el golpe.

---

## Cómo Ejecutar

### Requisitos
- Java 17 o superior
- Maven 3.8+
- Git

### 1. Clonar el repositorio
```bash
git clone <URL_DEL_REPOSITORIO>
cd ProyectoFinal
```

### 2. Compilar y ejecutar directamente con Maven
```bash
mvn compile
mvn exec:java
```

### 3. Generar JAR ejecutable y correrlo
```bash
mvn package -DskipTests
java -jar target/dopo-hardest-game.jar
```

### 4. Ejecutar pruebas
```bash
mvn test
```

### 5. Ejecutar con cobertura (IntelliJ)
1. Click derecho sobre `src/test/java/domain/`
2. Seleccionar **"Run 'All Tests' with Coverage"**
3. Ver el reporte en el panel **Coverage**

---

## Controles

| Acción | Un Jugador / J1 PvP | J2 PvP |
|--------|---------------------|--------|
| Mover | ↑ ↓ ← → | W A S D |
| Reiniciar | R (en menú) | — |

---

## Dependencias

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
```

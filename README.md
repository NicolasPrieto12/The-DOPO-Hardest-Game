# The DOPO Hardest Game

> Inspirado en *The World's Hardest Game*, desarrollado en Java con Swing.

**Autores:**
- Nicolás Prieto Ramos
- Sebastián Peña Sánchez

---

## Carta de Presentación

The DOPO Hardest Game es un videojuego de habilidad y reflejos en 2D desarrollado en Java como proyecto final de la asignatura. El jugador controla un cuadrado rojo que debe recoger todas las monedas del nivel y llegar a la zona final, evitando enemigos, bombas y otros obstáculos.

El proyecto fue construido aplicando principios de diseño orientado a objetos, patrones de diseño clásicos (Singleton, Factory Method, Strategy) y una separación estricta entre la capa de dominio y la interfaz gráfica. Cuenta con 3 modos de juego, 4 niveles de dificultad progresiva, un sistema de IA para el modo contra la máquina, sistema de checkpoints, escudos, guardado de partidas y más de 40 pruebas unitarias y de aceptación.

El objetivo del proyecto fue aplicar en la práctica los conceptos de ingeniería de software vistos en clase: diseño de clases, pruebas, análisis estático y dinámico, control de versiones y documentación.

---

## Descripción del Juego

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
| **Coin** (normal) | Amarilla | Debe recogerse para poder completar el nivel |
| **SkinCoin** | Azul | Transforma al jugador en Inky (azul): velocidad y tamaño x1.5 |
| **GreenCoin** | Verde | Transforma al jugador en Clyde (verde): resistente, absorbe el primer golpe sin morir |

---

## Tipos de Enemigos

| Enemigo | Comportamiento | Dificultad |
|---------|----------------|------------|
| **Enemy** (básico) | Se mueve en línea recta (horizontal o vertical), rebota en paredes | Baja |
| **PatrolEnemy** | Sigue una ruta circular predefinida (waypoints) | Media |
| **SliderEnemy** | Se desplaza exclusivamente en vertical, rebota arriba y abajo | Baja |
| **AcceleratedEnemy** | Se mueve al doble de velocidad (6 px/tick), rebota en paredes | Alta |

---

## Elementos Especiales

| Elemento | Descripción |
|----------|-------------|
| **CheckpointZone** | Zona verde intermedia. Al pisarla, el jugador reaparece aquí al morir conservando monedas |
| **Bomb** | Bomba estática negra. Mata al instante al tocarla. No desaparece |
| **LifeSource** | Corazón rojo. Otorga un escudo: el primer golpe de enemigo no mata, solo reposiciona |
| **StartZone** | Zona de inicio. Zona segura donde reaparece el jugador |
| **EndZone** | Zona de llegada. Completar el nivel requiere estar aquí con todas las monedas |

---

## Tipos de Jugador

| Tipo | Velocidad | Tamaño | Descripción |
|------|-----------|--------|-------------|
| **RED** | 3 px/tick | 20x20 | Estado base del jugador |
| **BLUE (Inky)** | 4 px/tick | 30x30 | Obtenido con SkinCoin. Más rápido y grande |
| **GREEN (Clyde)** | 3 px/tick | 20x20 | Obtenido con GreenCoin. Absorbe un golpe |

---

## Reglas Generales

- Cada nivel tiene un límite de **3 minutos** (180 segundos). Si se agota el tiempo el estado pasa a `TIMEOUT`
- Al morir sin checkpoint: el jugador vuelve al inicio y las monedas se reinician
- Al morir con checkpoint activo: el jugador reaparece en el checkpoint conservando monedas
- En PvP/PvM: la muerte de un jugador **no afecta** las monedas del otro
- El juego se puede **pausar** y **reiniciar** en cualquier momento
- Se puede **guardar y cargar** la partida (modos Single y PvP)

---

## Controles

| Acción | Un Jugador / J1 PvP | J2 PvP |
|--------|---------------------|--------|
| Mover | Flechas del teclado | W A S D |
| Pausar | P | P |
| Reiniciar | R (en menú) | — |

---

## Guía de Comandos

### Requisitos previos
- Java 17 o superior
- Maven 3.8+
- Git

### 1. Clonar el repositorio
```bash
git clone https://github.com/NicolasPrieto12/The-DOPO-Hardest-Game.git
cd The-DOPO-Hardest-Game
```

### 2. Compilar el proyecto
```bash
mvn compile
```

### 3. Ejecutar el juego
```bash
mvn exec:java -Dexec.mainClass="gui.GameGUI"
```

### 4. Ejecutar los tests unitarios
```bash
mvn test
```

### 5. Ejecutar los tests con reporte de cobertura
```bash
mvn test jacoco:report
```

### 6. Generar JAR ejecutable
```bash
mvn package -DskipTests
java -jar target/dopo-hardest-game.jar
```

### 7. Ejecutar análisis estático con PMD
```bash
mvn pmd:check
```

### 8. Abrir en IntelliJ IDEA
1. `File → Open` → seleccionar la carpeta del proyecto
2. IntelliJ detecta el `pom.xml` automáticamente
3. Click derecho en `GameGUI.java` → **Run**

### 9. Ejecutar tests con cobertura en IntelliJ
1. Click derecho sobre `src/test/java/domain/`
2. Seleccionar **Run 'All Tests' with Coverage**
3. Ver el reporte en el panel **Coverage**

---

## Diagrama de Clases y Patrones de Diseño

### Patrones de Diseño Aplicados

**Singleton — clase `Game`**
La clase `Game` garantiza que solo exista una instancia del juego en modo un jugador. El método `Game.resetInstance()` permite destruir la instancia en los tests para garantizar aislamiento entre pruebas.

```
Game.getInstance(player, levels)  →  crea la instancia si no existe
Game.getInstance()                →  retorna la instancia existente
Game.resetInstance()              →  destruye la instancia (usado en tests)
```

**Factory Method — clase `LevelFactory`**
La clase `LevelFactory` centraliza la construcción de todos los niveles del juego. Cada modo (Single, PvP, PvM) tiene sus propios métodos de construcción que encapsulan la configuración de paredes, enemigos, monedas y zonas.

```
LevelFactory.buildSingleLevel1(player)
LevelFactory.buildPvPLevel2(p1, p2)
LevelFactory.buildPvMLevel3(player, machine)
```

**Strategy — enum `MachineProfile`**
La IA de la máquina en modo PvM soporta dos perfiles intercambiables en tiempo de ejecución:
- `RANDOM`: cambia de dirección cada 25 ticks, 65% de probabilidad hacia el objetivo
- `EXPERT`: siempre elige la dirección libre más cercana al objetivo, evitando paredes

**Interfaces como contratos (Template Method)**
Las interfaces `IMovable`, `ICollidable` e `IRenderable` definen contratos que todas las entidades del juego implementan, permitiendo que el motor de juego trabaje con cualquier objeto sin conocer su tipo concreto.

---

## Reporte de Test Coverage

El proyecto cuenta con **11 clases de prueba** con más de **100 casos de prueba**.

### Resultado de Coverage

> Ejecutar con: `Run > Run with Coverage` sobre el directorio `src/test/java/domain/`

![Coverage Report](docs/coverage.png)

### Clases cubiertas

| Clase | Tipo de prueba |
|-------|----------------|
| `Game` | Unitaria + Aceptacion |
| `Player` | Unitaria |
| `Coin` | Unitaria |
| `Enemy` | Unitaria |
| `Level` | Unitaria |
| `GamePvP` | Unitaria + Aceptacion |
| `GamePvM` | Unitaria + Aceptacion |
| `LevelPvP` | Unitaria + Aceptacion |
| `SkinCoin`, `GreenCoin`, `Bomb`, `LifeSource`, `CheckpointZone` | Unitaria |
| `PatrolEnemy`, `SliderEnemy`, `AcceleratedEnemy`, `MachinePlayer` | Unitaria |
| `BoardPvP`, `StartZone`, `EndZone`, `PlayerType`, `GameState` | Unitaria |

### Escenarios cubiertos

- Estado inicial del juego (PLAYING, muertes=0, tiempo=180)
- Lógica de muerte y respawn con y sin checkpoint
- Sistema de checkpoints (activación, conservación de monedas)
- Recolección de monedas (Coin, SkinCoin, GreenCoin)
- Condición de victoria (monedas + EndZone)
- Pausa y reinicio completo
- Progresión de niveles (nextLevel, skipLevel)
- Tiempo agotado (TIMEOUT a los 180 segundos)
- Independencia de monedas en PvP/PvM al morir
- Comportamiento de la IA (RANDOM y EXPERT)
- Patrón Singleton (misma instancia siempre)
- Escudo de Clyde y LifeSource (absorbHit)
- Rebote de enemigos en bordes y paredes
- Zonas prohibidas para enemigos

---

## Reporte de Análisis Estático (PMD)

El código fue analizado con las herramientas integradas de IntelliJ IDEA y PMD:

### Resultados

- Sin variables no utilizadas
- Sin imports innecesarios
- Javadoc completo en todas las clases públicas y sus métodos
- Separación estricta entre capa de dominio y GUI — el paquete `domain` no importa clases de `javax.swing` ni `java.awt` excepto `Rectangle` y `Graphics`
- Uso de `final` en todos los campos inmutables
- Uso de `List.of()` para listas inmutables donde aplica
- Nombres de variables y métodos descriptivos en español
- Constantes nombradas con `UPPER_SNAKE_CASE`
- Clases de prueba con nombres `should/shouldNot` para describir comportamiento esperado
- Sin métodos con más de 30 líneas en la capa de dominio
- Sin clases con más de 200 líneas en la capa de dominio (excepto `LevelFactory` que es una fábrica)

### Reglas PMD aplicadas

| Regla | Estado |
|-------|--------|
| UnusedLocalVariable | Sin hallazgos |
| UnusedImports | Sin hallazgos |
| UnnecessaryLocalBeforeReturn | Sin hallazgos |
| MethodNamingConventions | Cumple |
| ClassNamingConventions | Cumple |
| FieldNamingConventions | Cumple |
| MissingJavadoc | Cumple |

---

## Notas de Diseño

### Independencia de monedas en PvP/PvM
En los modos PvP y PvM, las monedas son compartidas en el tablero pero la muerte de un jugador no reinicia las monedas del otro. Esto se logra mediante `LevelPvP.resetPlayer()` que solo reposiciona al jugador sin tocar las monedas.

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

## Temas y Lecciones Aprendidas

### Lecciones Técnicas

**Separación de capas (dominio vs GUI)**
Aprendimos la importancia de mantener la lógica del juego completamente independiente de la interfaz gráfica. El paquete `domain` no conoce nada de Swing, lo que permitió escribir pruebas unitarias sin necesidad de levantar una ventana.

**Patrones de diseño en la práctica**
Aplicar Singleton, Factory Method y Strategy no fue solo teoría — resolvió problemas reales: el Singleton evitó instancias duplicadas del juego, la Factory centralizó la construcción de niveles complejos, y Strategy permitió cambiar el comportamiento de la IA sin modificar el código del juego.

**Pruebas unitarias como red de seguridad**
Cada vez que corregimos un bug (como el reset de monedas en PvP o el contador de muertes en restart), los tests fallaron primero y nos guiaron exactamente a dónde estaba el problema. Sin pruebas, esos bugs habrían pasado desapercibidos.

**Cobertura de branches vs líneas**
Aprendimos que la cobertura de líneas no es suficiente — la cobertura de branches obliga a probar todos los caminos posibles de cada condición `if/else`, lo que reveló casos borde que no habíamos considerado.

**Control de versiones con Git**
Mantener un historial de commits descriptivos y organizados por tipo (`fix:`, `feat:`, `docs:`) facilitó entender qué cambió en cada momento y revertir errores cuando fue necesario.

### Lecciones de Proceso

**Retrospectiva**

Lo que funcionó bien:
- Dividir el trabajo por capas (uno en dominio, otro en GUI) redujo conflictos de merge
- Escribir los tests antes de corregir bugs (TDD reactivo) aceleró la corrección
- Documentar con Javadoc desde el inicio evitó tener que hacerlo todo al final

Lo que haríamos diferente:
- Escribir los tests desde el inicio del proyecto, no al final
- Definir los constructores de las clases antes de empezar a usarlos en los tests para evitar errores de compilación
- Usar ramas de Git por funcionalidad en lugar de trabajar todo en `main`

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

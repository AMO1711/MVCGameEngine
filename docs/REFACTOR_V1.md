# MVCGameEngine — REFACTOR\_V1

## Informe unificado: situación base, problemas de fronteras y soluciones

> **Estado:** REFACTOR\_V1 · Documento de arquitectura consolidado listo para GitHub

---

## ÍNDICE

1. [ABSTRACT](#abstract)
2. [INFORME DE SITUACIÓN BASE](#2-informe-de-situación-base)
   1. [Filosofía general del engine](#21-filosofía-general-del-engine)
   2. [Módulos base existentes y su rol actual](#22-módulos-base-existentes-y-su-rol-actual)
   3. [Estado del Main](#23-estado-del-main)
3. [PROBLEMAS DE FRONTERAS IDENTIFICADOS](#3-problemas-de-fronteras-identificados)
4. [PROPUESTAS DE SOLUCIÓN (CONSOLIDADAS)](#4-propuestas-de-solución-consolidadas)
   1. [World\*: definición clara + variación explícita](#41-world-definición-clara--variación-explícita)
      1. [ItemDTO vs PrototypeItemDTO](#411-separar-itemdto-y-prototypeitemdto)
      2. [Rangos como contrato](#412-rangos-como-contrato-no-como-valor-final)
      3. [Densidad en assets](#413-densidad-definida-en-assets)
   2. [LevelGenerator: escena estática y progresión](#42-levelgenerator-escena-estática-y-progresión)
   3. [IAGenerator: dinámica pura](#43-iagenerator-dinámica-pura)
   4. [ActionsGenerator: reglas, no infraestructura](#44-actionsgenerator-reglas-no-infraestructura)
   5. [Core: commit obligatorio y explícito](#45-core-commit-obligatorio-y-explícito)
   6. [Simplificación radical del Main](#46-simplificación-radical-del-main)
   7. [Ejemplo completo: colisión en Asteroids](#47-ejemplo-completo-ciclo-de-vida-de-una-colisión-en-asteroids)
5. [GRID DE CROSS-REFERENCE](#5-grid-de-cross-reference)
6. [CONCLUSIÓN](#6-conclusión)

---

## ABSTRACT

### ¿Qué es MVCGameEngine?

**MVCGameEngine** es un motor de juegos **educativo, modular y orientado a arcades**, cuyo objetivo principal es permitir crear juegos muy distintos **sin modificar el core**.

Su idea central es separar de forma estricta:

- **Infraestructura** (tiempo, física, eventos, ejecución),
- de **decisiones de diseño** (qué aparece, cuándo, qué pasa cuando algo colisiona).

El motor adopta una arquitectura **MVC sin game loop global**:

- Cada entidad dinámica (*DynamicBody*) ejecuta su propio tick.
- El core garantiza coherencia temporal, física y de eventos.
- El gameplay emerge de la combinación de módulos intercambiables.

MVCGameEngine no es:

- un engine cerrado para un género concreto,
- ni un framework de alto nivel con reglas predefinidas.

Es, deliberadamente, un **motor de infraestructura** sobre el que se pueden construir arcades clásicos, experimentales o híbridos, siempre respetando contratos claros entre piezas.

---



### Acerca de los ejemplos incluidos en este documento (Asteroids como referencia)

A lo largo de este documento se utiliza el arcade clásico **Asteroids** como ejemplo de referencia para ilustrar, de forma práctica y narrada, el rol y las fronteras de cada módulo del engine.

Asteroids se elige porque:

- es conceptualmente simple,
- separa muy bien escena, dinámica y reglas,
- y expone de forma clara problemas clásicos (spawn, colisiones, fragmentación, ritmo).

Siempre que aparezcan ejemplos narrados ("el jugador dispara", "un asteroide se fragmenta", etc.), deben entenderse **como instancias concretas de uso del engine**, no como lógica hardcodeada del core.

El objetivo no es describir cómo implementar Asteroids, sino usarlo como **modelo mental compartido** para explicar:

- qué decide cada generador,
- qué hace el core,
- y, sobre todo, qué *no* debe hacer cada pieza.

---

### ¿Qué es Asteroids?

**Asteroids** es un arcade clásico (Atari, 1979) de pantalla única y espacio abierto.

La premisa es simple:

- El jugador controla una nave con inercia.
- En el espacio aparecen asteroides que se desplazan y rotan.
- El jugador puede disparar proyectiles para destruirlos.

Las reglas básicas del juego son:

- Al impactar un proyectil contra un asteroide:
  - el proyectil desaparece,
  - el asteroide se fragmenta en otros más pequeños (o desaparece si ya es pequeño).
- Los asteroides no persiguen al jugador: solo generan presión espacial.
- El reto surge del **ritmo de aparición**, la **fragmentación** y la **gestión del espacio**, no de una IA compleja.

Asteroids es especialmente útil como ejemplo porque:

- distingue claramente entre **escena inicial** (nave + fondo),
- **dinámica del mundo** (spawn continuo de asteroides),
- y **reglas puras** (qué ocurre cuando algo colisiona).

Por eso encaja de forma natural como caso de estudio para explicar la arquitectura de MVCGameEngine.

---

### Alcance y objetivo de este informe

MVCGameEngine es un motor educativo y modular que permite crear arcades muy distintos sin tocar el core MVC.

El core proporciona **infraestructura** (tiempo, física, eventos, ejecución), mientras que una serie de módulos base configurables (World\*, LevelGenerator, IAGenerator, ActionsGenerator) permiten definir la experiencia de juego.

Este documento no redefine el motor, sino que **analiza su estado real**, identifica problemas de frontera y propone una reorganización conceptual para hacerlo más comprensible y usable.

El engine funciona correctamente, pero presenta problemas estructurales que dificultan:

- entender dónde modificar un parámetro para que tenga efecto,
- evitar overrides silenciosos entre módulos,
- y ofrecer una buena experiencia en las primeras horas de uso.

Este informe:

- fija el estado real de partida (qué es cada módulo hoy),
- identifica las roturas de frontera que causan confusión,
- y propone soluciones conceptuales que:
  - aclaran responsabilidades,
  - reducen parámetros dispersos,
  - mantienen el core como infraestructura,
  - y permiten una alta variación de arcades sin tocarlo.

---

## 2. INFORME DE SITUACIÓN BASE

### 2.1 Filosofía general del engine

- Arquitectura MVC, sin game loop global.
- Cada DynamicBody ejecuta su propio tick en su propio hilo.
- El core:
  - calcula física,
  - detecta eventos,
  - ejecuta acciones,
  - mantiene coherencia temporal (dt).
- Los módulos base están fuera del core y son intercambiables para crear juegos distintos.

### 2.2 Módulos base existentes y su rol actual

#### World\* (WorldDefinition + Providers + Assets)

Define:

- qué elementos existen,
- qué assets se usan,
- cómo se ven los objetos.

Incluye:

- definición de items,
- armas,
- emitters,
- fondos.

Actualmente mezcla:

- definición visual,
- parámetros físicos,
- y parte de la lógica de gameplay.

#### LevelGenerator

- Crea la escena inicial.
- Coloca elementos “estáticos”.
- Hoy aplica el nivel en el constructor.

Objetivo gamer explícito:

- gestionar escenas estáticas y cambios de nivel.

#### IAGenerator

- Genera la parte dinámica del mundo.
- Decide:
  - qué aparece,
  - cuándo aparece,
  - con qué ritmo.

Actualmente:

- pisa tamaños y masas,
- hace bootstrap del player,
- y se ve afectado indirectamente por la semántica de MOVE.

#### ActionsGenerator

- Recibe eventos ricos.
- Decide qué acciones se disparan.
- Es el ruleset del juego.

Actualmente sufre confusión porque:

- algunas acciones parecen necesarias para que el mundo avance.

#### Core (Model + Controller)

Infraestructura pura:

- física,
- tiempo,
- eventos,
- ejecución de acciones.

Implementa correctamente:

- tick por body,
- commit de física,
- NO\_MOVE con timestamp correcto.

Pero con semántica difícil de entender desde fuera.

### 2.3 Estado del Main

El Main actual:

- contiene muchos parámetros de diseño (tamaños, masas, delays),
- actúa como pseudo–archivo de configuración del juego,
- y expone al usuario demasiadas decisiones demasiado pronto.

Esto es un síntoma, no el problema raíz.

---

## 3. PROBLEMAS DE FRONTERAS IDENTIFICADOS

### 3.1 Doble fuente de verdad

- Tamaños y masas definidos en World\* y recalculados en IA.
- Resultado: cambiar un valor no siempre tiene efecto.

### 3.2 Variabilidad visual limitada

- Pocos assets → miles de instancias.
- Sin rangos bien definidos, el arcade se vuelve repetitivo.

### 3.3 Movimiento acoplado a acciones

- El commit de movimiento depende implícitamente de acciones (MOVE).
- Spawn o ticks sin eventos pueden congelar bodies.
- El comportamiento es correcto, pero opaco.

### 3.4 NO\_MOVE malinterpretado

- Conceptualmente podría confundirse con “pausar el tiempo”.
- En realidad congela posición pero actualiza timestamp (correcto).

### 3.5 IA y Rules lidiando con infraestructura

IA y ActionsGenerator no deberían:

- preocuparse de dt,
- ni de timestamps,
- ni de “mantener el mundo en marcha”.

### 3.6 Main sobrecargado

- Muchos parámetros acaban en el punto de entrada.
- El usuario no sabe qué es esencial y qué es detalle.

---

## 4. PROPUESTAS DE SOLUCIÓN (CONSOLIDADAS)

### 4.1 World\*: definición clara + variación explícita

#### 4.1.1 Separar ItemDTO y PrototypeItemDTO

**ItemDTO (determinista)**

- Objetos del nivel:
  - tamaño fijo,
  - posición fija,
  - apariencia fija.
- Consumidos por LevelGenerator.

**PrototypeItemDTO (generativo)**

- Objetos spawnables:
  - asteroides,
  - debris,
  - powerups dinámicos.
- Contienen:
  - rangos de tamaño, orientación, rotación,
  - referencia a asset,
  - política de masa.
- Consumidos por IAGenerator.

#### 4.1.2 Rangos como contrato, no como valor final

- World\*:
  - no fija tamaños finales,
  - fija dominios válidos.
- IA:
  - muestrea dentro de esos rangos.

Una única fuente de verdad.

#### 4.1.3 Densidad definida en assets

Cada asset puede definir su densidad (material).

Ejemplo: asteroides férreos más densos.

La masa se calcula siempre como:

```
mass = density * size^p * massScale
```

Beneficios:

- coherencia física,
- variedad real,
- ningún minMass/maxMass en IA ni en el Main.

### 4.2 LevelGenerator: escena estática y progresión

**Ejemplo narrado (Asteroids clásico)**

Al comenzar una partida de *Asteroids*:

- El jugador aparece en el centro de la pantalla.
- No hay asteroides todavía (o hay un número fijo inicial).
- El fondo es siempre el mismo y no tiene interacción.

Todo esto es responsabilidad del `LevelGenerator`.

Narrativa concreta:

> “Empieza la partida. Se crea la nave del jugador en (0,0), con orientación neutra. Se instala el fondo estrellado. No hay aún presión dinámica.”

El `LevelGenerator`:

- Instala un `ItemDTO` para la nave del jugador:
  - tamaño fijo,
  - asset fijo,
  - posición inicial conocida.
- Puede instalar `ItemDTO` decorativos (estrellas, HUD lógico, límites invisibles).
- **No decide** cuántos asteroides aparecerán después.

Cuando se pasa al siguiente nivel:

> “Nivel 2. Se limpia la escena dinámica, se mantiene el jugador, se reinicia la presión.”

El `LevelGenerator` gestiona ese cambio, sin tocar IA ni reglas.

- Instala ItemDTO.
- Gestiona cambios de nivel.

No:

- genera dinámicos,
- define ritmo,
- toca reglas.

### 4.3 IAGenerator: dinámica pura

**Ejemplo de referencia (Asteroids clásico)**

En un arcade tipo *Asteroids*, el IAGenerator no decide *qué pasa* cuando algo colisiona, sino **qué existe en el mundo y con qué ritmo aparece**.

Responsabilidades concretas:

- Spawnear asteroides usando `PrototypeItemDTO`:
  - tamaños dentro de un rango (grande / medio / pequeño),
  - orientación y rotación aleatoria,
  - masa derivada automáticamente de la densidad del asset.
- Decidir *cuándo* aparece un nuevo asteroide (spawn rate creciente por nivel).
- Nunca reaccionar a eventos de colisión.

Ejemplo:

- Nivel 1:
  - 4 asteroides grandes cada 10 segundos.
- Nivel 5:
  - 2 grandes + 4 medianos cada 6 segundos.

El IAGenerator **no sabe** si un asteroide ha sido destruido por un proyectil, ni cómo se fragmenta. Solo mantiene la presión dinámica del juego.

- Decide cuándo y cuántos.
- Instancia prototipos.
- No redefine apariencia ni física base.

IAConfig se reduce a:

- spawn rates,
- delays,
- patrones.

IA no lidia con timestamps ni movimiento base.

### 4.4 ActionsGenerator: reglas, no infraestructura

**Ejemplo de referencia (Asteroids clásico)**

En Asteroids, las reglas son claras:

- Si un proyectil colisiona con un asteroide:
  - el proyectil desaparece,
  - el asteroide se fragmenta (o desaparece si es pequeño).

Esto **no es responsabilidad del IAGenerator**, sino del `ActionsGenerator`.

#### Flujo conceptual del ejemplo

1. El core detecta una colisión física.
2. Se emite un `CollisionEvent` con información rica:
   - `entityA` (Projectile)
   - `entityB` (Asteroid)
   - punto de impacto, energía, etc.
3. El `ActionsGenerator` recibe el evento.
4. Aplica reglas puras y devuelve acciones.

#### Ejemplo lógico

```text
Evento:
  CollisionEvent(
    projectileId,
    asteroidId,
    asteroidSize = LARGE
  )

Acciones generadas:
  - DIE(projectileId)
  - SPAWN_FRAGMENT(asteroidId, size = MEDIUM, count = 2)
```

El ActionsGenerator:

- **no destruye nada directamente**,
- **no ejecuta física**,
- **no actualiza timestamps**,
- solo expresa consecuencias del evento.

El core se encarga de ejecutar esas acciones de forma coherente.

Este enfoque permite:

- cambiar reglas (ej. asteroides que explotan en 3 fragmentos),

- añadir variantes (power-ups que evitan fragmentación),

- sin tocar el core ni la IA.

- Las acciones expresan consecuencias:

  - spawn,
  - die,
  - overrides explícitos.

No son responsables de:

- hacer avanzar el mundo,
- mantener bodies activos.

### 4.5 Core: commit obligatorio y explícito

**Ejemplo narrado (un frame de Asteroids)**

Supongamos un frame cualquiera del juego:

> “La nave se mueve, un proyectil avanza, un asteroide gira lentamente.”

Lo que ocurre en el core, *siempre*, es lo siguiente:

1. Se calcula `dt` desde el último tick.
2. Cada body propone su nuevo estado físico:
   - posición,
   - velocidad,
   - rotación.
3. El core detecta eventos:
   - colisiones,
   - salidas de pantalla,
   - expiración de vida del proyectil.
4. Los eventos se envían al `ActionsGenerator`.
5. El core recibe un conjunto de acciones.
6. El core resuelve una `MovementDirective` y **commitea**.

Ejemplo concreto:

> “El proyectil no colisiona en este frame.”

- No hay acciones.
- Aun así:
  - el movimiento se commitea,
  - el timestamp se actualiza,
  - el mundo avanza.

Esto garantiza que:

- un body nunca se congela por falta de acciones,
- el tiempo nunca depende de reglas o IA.

El core no sabe nada de *Asteroids*. Solo mantiene el contrato temporal y físico.

#### 4.5.1 Contrato definitivo del tick por body

En cada tick:

1. Se calcula dt
2. Física propone nuevos valores
3. Se detectan eventos
4. Rules generan acciones
5. El core resuelve una **MovementDirective**:
   - DEFAULT\_COMMIT
   - FREEZE (NO\_MOVE)
   - OVERRIDE

- Siempre se commitea
- Siempre se actualiza el timestamp
- El tiempo nunca se congela

#### 4.5.2 NO\_MOVE correcto y centralizado

- NO\_MOVE:
  - congela posición/velocidad,
  - actualiza timestamp.

IA y rules no se ocupan de esto. El core es el único responsable.

#### 4.5.3 MOVE desaparece como acción pública

- MOVE deja de ser acción necesaria.
- El commit es infraestructura.

En el futuro:

- el “movimiento” se modela como evento o métrica (energía, desgaste).

### 4.6 Simplificación radical del Main

Gracias a:

- prototipos,
- densidad en assets,
- masa derivada,
- contratos claros,

el Main:

- deja de contener diseño,
- solo orquesta módulos.

Esto mejora enormemente el onboarding.

---

### 4.7 Ejemplo completo: ciclo de vida de una colisión en Asteroids

**Narrativa paso a paso**

> “El jugador dispara. El proyectil impacta contra un asteroide grande.”

1. **Core (física)**

   - Detecta una colisión entre `Projectile` y `Asteroid`.

2. **Evento**

   - Se emite un `CollisionEvent` con:
     - ids de ambos bodies,
     - tipos,
     - energía del impacto.

3. **ActionsGenerator (reglas)**

   - Evalúa el evento.
   - Decide consecuencias:
     - destruir el proyectil,
     - fragmentar el asteroide.

4. **Acciones devueltas**

```text
- DIE(projectileId)
- SPAWN_FRAGMENT(asteroidId, size = MEDIUM, count = 2)
```

5. **Core (ejecución)**

   - Elimina el proyectil.
   - Instancia dos nuevos asteroides medianos usando prototipos.
   - Commita el movimiento del resto de bodies.

6. **IAGenerator**

   - No interviene.
   - Seguirá spawneando nuevos asteroides según su ritmo.

Este ejemplo muestra claramente:

- quién detecta,
- quién decide,
- quién ejecuta,
- y quién **no participa**.

---

## 5. GRID DE CROSS-REFERENCE

| Problema                      | Solución                             |
| ----------------------------- | ------------------------------------ |
| Tamaños pisados entre módulos | PrototypeItemDTO con rangos          |
| Juego repetitivo              | Rangos + variación continua          |
| Masas arbitrarias             | Densidad en assets + masa derivada   |
| Main sobrecargado             | Diseño movido a World                |
| Bodies se congelan sin MOVE   | Commit obligatorio                   |
| Spawn congela emisores        | Movimiento independiente de acciones |
| Rebotes inconsistentes        | MovementDirective                    |
| NO\_MOVE confuso              | Semántica clara + core               |
| IA tocando infraestructura    | Commit y timestamp solo en core      |
| Reglas difíciles de razonar   | Acciones = consecuencias             |

---

## 6. CONCLUSIÓN

MVCGameEngine no necesitaba “más features”, sino fronteras claras.

Las soluciones propuestas:

- respetan el diseño original,
- mantienen el core como infraestructura,
- permiten una gran variación de arcades,
- y convierten un comportamiento correcto pero opaco en un sistema correcto y explicable.

El resultado práctico es clave:

> Cuando un desarrollador quiere cambiar algo, sabe exactamente dónde hacerlo.

Eso es lo que transforma un motor funcional en un motor realmente usable.


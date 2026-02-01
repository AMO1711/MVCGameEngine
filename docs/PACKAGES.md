# Informe jerárquico de paquetes para unknown/MVCGameEngine — rama: develop (no disponible localmente; análisis sobre branch work) — generado el 2026-02-01

> Nota de acceso: el repositorio local no tiene remoto Git configurado ni existe la rama `develop` en este entorno. No es posible “actualizar” ni contrastar cambios respecto a `develop`. Para resolverlo, configure un remoto (`git remote add origin ...`) y haga `git fetch`/`git checkout develop`. Mientras tanto, el informe refleja el árbol `src/` actual de la rama local `work`.
>
> Nota sobre enlaces: se usa un owner placeholder (`unknown`). Sustitúyelo por el owner real de GitHub para que los enlaces funcionen.

## Micro-resumen arquitectural

El engine implementa un MVC clásico con un `Controller` que coordina la simulación (`Model`) y la presentación (`View`). El `Model` gestiona entidades, físicas, eventos y snapshots de datos, mientras que la `View` se encarga del render loop, assets e input, consumiendo DTOs para no acoplarse al estado mutable. Este flujo puede observarse en el controlador y las clases principales del modelo y la vista ([src/controller/implementations/Controller.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/implementations/Controller.java), [src/model/implementations/Model.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/implementations/Model.java), [src/view/core/View.java](https://github.com/unknown/MVCGameEngine/blob/work/src/view/core/View.java)).

Se usan patrones MVC, Factory, Strategy y Template Method para separar responsabilidades y permitir sustitución de reglas, motores de físicas y definiciones de mundo sin tocar otras capas. El desacoplamiento entre `controller`, `model` y `view` se logra mediante interfaces concretas en `controller.ports`, `model.ports` y DTOs de render, lo que limita dependencias directas y facilita la evolución independiente de cada capa ([src/controller/ports/ActionsGenerator.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/ports/ActionsGenerator.java), [src/model/ports/DomainEventProcessor.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/ports/DomainEventProcessor.java), [src/view/renderables/ports/RenderDTO.java](https://github.com/unknown/MVCGameEngine/blob/work/src/view/renderables/ports/RenderDTO.java)).

### Grid de paquetes de primer nivel

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `controller` (`src/controller`) | Orquestación MVC y puente entre Model y View. | Inyección de dependencias, mapeo DTOs, reglas de juego, ciclo de vida. |
| `game` (`src/game`) | Configuración del juego y reglas específicas. | Main, IA, generación de niveles y reglas de colisión/acciones. |
| `model` (`src/model`) | Núcleo de simulación y físicas. | Entidades, colisiones, eventos, armas, emisores, físicas. |
| `resources` (`src/resources`) | Assets gráficos del juego. | Imágenes, sprites, fondos y efectos. |
| `utils` (`src/utils`) | Utilidades compartidas. | Assets, eventos, acciones, spatial grid, imágenes, helpers. |
| `view` (`src/view`) | Presentación, render y HUD. | Render loop, UI, input, DTOs de render. |
| `world` (`src/world`) | Definición de mundos y catálogos de ítems. | Proveedores de world definitions, DTOs de definición. |

---

## Paquete `controller` (`src/controller`)

Capa de orquestación entre simulación y presentación que implementa el coordinador MVC y encapsula las reglas de juego mediante puertos. ([src/controller/implementations/Controller.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/implementations/Controller.java))

### Grid resumen de subpaquetes

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `controller.implementations` | Implementación concreta del controlador. | Ciclo de vida del engine, orquestación MVC, traducción de eventos. |
| `controller.mappers` | Mapeos DTO entre Model y View. | Conversión de Body/Player DTOs a Render DTOs. |
| `controller.ports` | Contratos de integración. | Interfaces para reglas, estado del engine y evolución del mundo. |

### Análisis detallado

- **Propósito:** centralizar la coordinación entre simulación y renderizado, inyectando reglas de juego y transformando datos de dominio en DTOs de render. ([src/controller/implementations/Controller.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/implementations/Controller.java))
- **Responsabilidades principales:**
  - Activación del engine y configuración de dimensiones; entrega de snapshots y notificaciones a la vista. ([src/controller/implementations/Controller.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/implementations/Controller.java))
  - Traducción de eventos de dominio a acciones mediante `ActionsGenerator`. ([src/controller/ports/ActionsGenerator.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/ports/ActionsGenerator.java))
  - Uso de mappers para construir DTOs de render. ([src/controller/mappers/DynamicRenderableMapper.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/mappers/DynamicRenderableMapper.java), [src/controller/mappers/PlayerRenderableMapper.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/mappers/PlayerRenderableMapper.java))
- **Interacción con otras capas/paquetes:**
  - Consume `model` y `world` para crear entidades y ejecutar reglas. ([src/controller/implementations/Controller.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/implementations/Controller.java))
  - Publica DTOs a `view` y recibe input desde `view` mediante inyección directa del controlador en la vista. ([src/view/core/View.java](https://github.com/unknown/MVCGameEngine/blob/work/src/view/core/View.java))
  - Se desacopla mediante interfaces en `controller.ports` (`ActionsGenerator`, `WorldInitializer`, `WorldEvolver`). ([src/controller/ports/ActionsGenerator.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/ports/ActionsGenerator.java), [src/controller/ports/WorldInitializer.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/ports/WorldInitializer.java), [src/controller/ports/WorldEvolver.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/ports/WorldEvolver.java))
- **Concurrencia / threading:** el controlador es mayormente un façade con estado mínimo; los accesos de snapshots se realizan en el render loop y se apoyan en DTOs inmutables para evitar compartir estado mutable. ([src/controller/implementations/Controller.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/implementations/Controller.java))
- **Principales clases/interfaces/DTOs y su rol:**
  - `Controller`: coordinador MVC y puente Model/View. ([src/controller/implementations/Controller.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/implementations/Controller.java))
  - `ActionsGenerator`: contrato de reglas de juego. ([src/controller/ports/ActionsGenerator.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/ports/ActionsGenerator.java))
  - `WorldInitializer`/`WorldEvolver`: contratos para bootstrap y evolución. ([src/controller/ports/WorldInitializer.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/ports/WorldInitializer.java), [src/controller/ports/WorldEvolver.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/ports/WorldEvolver.java))
- **Patrones de diseño relevantes:** MVC, DTO + Mapper, Dependency Injection por puertos.
- **Puntos de atención:**
  - Probar integración de mappers para asegurar que cambios en DTOs no rompan el render.
  - Métodos de notificación (alta/baja de entidades) son críticos para consistencia entre Model y View.

### Subpaquete `controller.implementations`

**Descripción:** implementación principal del controlador MVC con responsabilidades de activación, reglas y bridge entre capas. ([src/controller/implementations/Controller.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/implementations/Controller.java))

**Grid resumen**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `controller.implementations` | Implementación concreta del controlador. | Activación del engine, coordinación MVC, reglas de juego. |

**Análisis detallado**

- **Propósito:** ejecutar la lógica de orquestación y enlazar Model/View. ([src/controller/implementations/Controller.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/implementations/Controller.java))
- **Responsabilidades principales:** bootstrap de mundo, creación de entidades y rutas de comandos del jugador. ([src/controller/implementations/Controller.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/implementations/Controller.java))
- **Interacción:** consume `model` y `view` directamente; usa `world` para configuraciones de armas y emisores. ([src/controller/implementations/Controller.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/implementations/Controller.java))
- **Concurrencia:** usa estado `EngineState` volatile; los snapshots de render son DTOs nuevos por llamada. ([src/controller/implementations/Controller.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/implementations/Controller.java))
- **Clases clave:** `Controller`. ([src/controller/implementations/Controller.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/implementations/Controller.java))
- **Patrones:** Fachada + Coordinador MVC.

### Subpaquete `controller.mappers`

**Descripción:** conjunto de mapeadores de dominio a render para aislar dependencias de la vista. ([src/controller/mappers/DynamicRenderableMapper.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/mappers/DynamicRenderableMapper.java))

**Grid resumen**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `controller.mappers` | Translación DTO de Model a View. | Mapear cuerpos, jugadores, armas y estadísticas. |

**Análisis detallado**

- **Propósito:** encapsular lógica de conversión entre DTOs del `model` y DTOs del `view`. ([src/controller/mappers/RenderableMapper.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/mappers/RenderableMapper.java))
- **Responsabilidades principales:** convertir `BodyDTO` en `RenderDTO`, `PlayerDTO` en `PlayerRenderDTO`, etc. ([src/controller/mappers/PlayerRenderableMapper.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/mappers/PlayerRenderableMapper.java), [src/controller/mappers/DynamicRenderableMapper.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/mappers/DynamicRenderableMapper.java))
- **Interacción:** consumen DTOs del `model` y entregan DTOs para `view`. ([src/controller/mappers/DynamicRenderableMapper.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/mappers/DynamicRenderableMapper.java))
- **Patrones:** Mapper/Assembler.
- **Puntos de atención:** mantener sincronización de campos cuando se agreguen nuevos atributos a DTOs.

### Subpaquete `controller.ports`

**Descripción:** contratos para reglas de juego, estados del engine y construcción/evolución del mundo. ([src/controller/ports/ActionsGenerator.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/ports/ActionsGenerator.java))

**Grid resumen**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `controller.ports` | Interfaces de integración. | Reglas, evolución del mundo y estados del engine. |

**Análisis detallado**

- **Propósito:** permitir desacoplamiento entre core del engine y reglas/implementaciones concretas. ([src/controller/ports/ActionsGenerator.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/ports/ActionsGenerator.java))
- **Responsabilidades principales:**
  - `ActionsGenerator` define el contrato de reglas de juego. ([src/controller/ports/ActionsGenerator.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/ports/ActionsGenerator.java))
  - `WorldInitializer` y `WorldEvolver` exponen operaciones de bootstrap y runtime. ([src/controller/ports/WorldInitializer.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/ports/WorldInitializer.java), [src/controller/ports/WorldEvolver.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/ports/WorldEvolver.java))
  - `EngineState` define estados del ciclo de vida. ([src/controller/ports/EngineState.java](https://github.com/unknown/MVCGameEngine/blob/work/src/controller/ports/EngineState.java))
- **Patrones:** Ports & Adapters.

---

## Paquete `game` (`src/game`)

Contiene la configuración de la experiencia de juego, desde el `Main` hasta reglas, IA y generadores de niveles. ([src/game/Main.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/Main.java))

### Grid resumen de subpaquetes

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `game.actions` | Reglas de colisión y límites. | Traducción de eventos a acciones. |
| `game.ai` | Generadores de IA. | Spawners de entidades dinámicas. |
| `game.core` | Infraestructura de generadores. | Template Methods para niveles/IA. |
| `game.level` | Definición de niveles. | Nivel básico y configuración de escenas. |
| `game.worlddef` | Proveedores de mundos. | World definitions concretas. |

### Análisis detallado

- **Propósito:** orquestar un juego concreto encima del engine genérico. ([src/game/Main.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/Main.java))
- **Responsabilidades principales:**
  - Arranque del motor con `Controller`, `Model` y `View`. ([src/game/Main.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/Main.java))
  - Aplicación de reglas de juego mediante `ActionsGenerator`. ([src/game/actions/ActionsReboundCollisionPlayerImmunity.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/actions/ActionsReboundCollisionPlayerImmunity.java))
  - Spawning de entidades dinámicas y jugadores. ([src/game/ai/AIBasicSpawner.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/ai/AIBasicSpawner.java))
- **Interacción con otras capas/paquetes:**
  - Se apoya en `controller.ports` para evolucionar el mundo. ([src/game/ai/AIBasicSpawner.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/ai/AIBasicSpawner.java))
  - Consume definiciones de mundo (`world`) y assets (`utils.assets`). ([src/game/Main.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/Main.java))
- **Concurrencia / threading:** los generadores de IA suelen ejecutar su propio ciclo (ver generador abstracto). ([src/game/core/AbstractIAGenerator.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/core/AbstractIAGenerator.java))
- **Principales clases/interfaces/DTOs y su rol:**
  - `Main`: arranque del engine. ([src/game/Main.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/Main.java))
  - `ActionsReboundCollisionPlayerImmunity`: reglas base. ([src/game/actions/ActionsReboundCollisionPlayerImmunity.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/actions/ActionsReboundCollisionPlayerImmunity.java))
  - `AIBasicSpawner`: spawner básico. ([src/game/ai/AIBasicSpawner.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/ai/AIBasicSpawner.java))
- **Patrones de diseño relevantes:** Template Method en generadores, Strategy para reglas (implementaciones de `ActionsGenerator`).
- **Puntos de atención:** validar límites de spawners y evitar que reglas de juego ignoren eventos críticos (colisiones, límites).

### Subpaquete `game.actions`

**Descripción:** implementaciones de reglas que traducen eventos de dominio a acciones del engine. ([src/game/actions/ActionsReboundCollisionPlayerImmunity.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/actions/ActionsReboundCollisionPlayerImmunity.java))

**Grid resumen**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `game.actions` | Reglas de acción. | Resolver colisiones, límites, vida y emisiones. |

**Análisis detallado**

- **Propósito:** aplicar reglas específicas de colisión/limitaciones. ([src/game/actions/ActionsReboundCollisionPlayerImmunity.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/actions/ActionsReboundCollisionPlayerImmunity.java))
- **Responsabilidades principales:** mapear `DomainEvent` a `ActionDTO`. ([src/game/actions/ActionsReboundCollisionPlayerImmunity.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/actions/ActionsReboundCollisionPlayerImmunity.java))
- **Interacción:** consume eventos del `model` vía `controller.ports.ActionsGenerator`. ([src/game/actions/ActionsReboundCollisionPlayerImmunity.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/actions/ActionsReboundCollisionPlayerImmunity.java))
- **Patrones:** Strategy (por implementación de `ActionsGenerator`).
- **Puntos de atención:** cobertura de casos de inmunidad y colisiones múltiples.

### Subpaquete `game.ai`

**Descripción:** lógica de generación de entidades dinámicas y players. ([src/game/ai/AIBasicSpawner.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/ai/AIBasicSpawner.java))

**Grid resumen**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `game.ai` | Spawners/IA. | Creación periódica de asteroides y jugadores. |

**Análisis detallado**

- **Propósito:** spawnear entidades basado en definiciones de mundo. ([src/game/ai/AIBasicSpawner.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/ai/AIBasicSpawner.java))
- **Responsabilidades principales:** seleccionar definiciones y crear dinámicos/jugadores. ([src/game/ai/AIBasicSpawner.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/ai/AIBasicSpawner.java))
- **Interacción:** usa `WorldEvolver` para insertar entidades y `WorldDefinition` como fuente. ([src/game/ai/AIBasicSpawner.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/ai/AIBasicSpawner.java))
- **Concurrencia:** hereda ciclo de vida de `AbstractIAGenerator` (thread propio). ([src/game/core/AbstractIAGenerator.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/core/AbstractIAGenerator.java))
- **Patrones:** Template Method (abstract generator).

### Subpaquete `game.core`

**Descripción:** infraestructura base para generadores de IA y niveles. ([src/game/core/AbstractIAGenerator.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/core/AbstractIAGenerator.java))

**Grid resumen**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `game.core` | Infraestructura base. | Ciclo de vida y utilidades para generadores. |

**Análisis detallado**

- **Propósito:** proveer un esqueleto para generadores con hooks (`onActivate`, `tickAlive`). ([src/game/core/AbstractIAGenerator.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/core/AbstractIAGenerator.java))
- **Responsabilidades principales:** ciclo de vida y acceso a `WorldEvolver`. ([src/game/core/AbstractIAGenerator.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/core/AbstractIAGenerator.java))
- **Patrones:** Template Method.

### Subpaquete `game.level`

**Descripción:** niveles y composición de escenas. ([src/game/level/LevelBasic.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/level/LevelBasic.java))

**Grid resumen**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `game.level` | Niveles. | Configurar decoradores, estáticos, jugadores. |

**Análisis detallado**

- **Propósito:** encapsular reglas de composición de un nivel. ([src/game/level/LevelBasic.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/level/LevelBasic.java))
- **Responsabilidades principales:** invocar `WorldInitializer` para inyectar entidades estáticas y decoradores. ([src/game/level/LevelBasic.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/level/LevelBasic.java))
- **Patrones:** Template Method (en generadores de nivel). ([src/game/core/AbstractLevelGenerator.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/core/AbstractLevelGenerator.java))

### Subpaquete `game.worlddef`

**Descripción:** proveedores concretos de `WorldDefinition`. ([src/game/worlddef/RandomWorldDefinitionProvider.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/worlddef/RandomWorldDefinitionProvider.java))

**Grid resumen**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `game.worlddef` | World providers concretos. | Variantes de definiciones de mundo. |

**Análisis detallado**

- **Propósito:** materializar definiciones de mundo para escenarios específicos. ([src/game/worlddef/RandomWorldDefinitionProvider.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/worlddef/RandomWorldDefinitionProvider.java))
- **Responsabilidades principales:** configurar assets y listas de entidades definidas. ([src/game/worlddef/RandomWorldDefinitionProvider.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/worlddef/RandomWorldDefinitionProvider.java))
- **Interacción:** extiende base `world.core.AbstractWorldDefinitionProvider`. ([src/game/worlddef/RandomWorldDefinitionProvider.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/worlddef/RandomWorldDefinitionProvider.java))
- **Patrones:** Template Method/Factory en la definición de mundos.

---

## Paquete `model` (`src/model`)

Capa de simulación que gestiona entidades, eventos, físicas, armas y emisores con soporte concurrente. ([src/model/implementations/Model.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/implementations/Model.java))

### Grid resumen de subpaquetes

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `model.bodies` | Entidades físicas del mundo. | Bodies dinámicos, estáticos, jugador y DTOs. |
| `model.emitter` | Emisores de partículas. | Emisión de cuerpos/efectos. |
| `model.implementations` | Núcleo del modelo. | Simulación, eventos, snapshots. |
| `model.physics` | Motores de físicas. | Integración de movimiento y límites. |
| `model.ports` | Interfaces del modelo. | Contratos de eventos y estado. |
| `model.weapons` | Sistema de armas. | Tipos, estados y disparos. |

### Análisis detallado

- **Propósito:** ejecutar la simulación, detectar eventos y coordinar acciones. ([src/model/implementations/Model.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/implementations/Model.java))
- **Responsabilidades principales:**
  - Gestión de entidades y snapshots para render. ([src/model/implementations/Model.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/implementations/Model.java))
  - Detección de colisiones con `SpatialGrid`. ([src/utils/spatial/core/SpatialGrid.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/spatial/core/SpatialGrid.java))
  - Procesamiento de eventos y ejecución de acciones. ([src/model/implementations/Model.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/implementations/Model.java))
- **Interacción con otras capas/paquetes:**
  - Envía eventos al `controller` mediante `DomainEventProcessor`. ([src/model/implementations/Model.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/implementations/Model.java))
  - Usa `utils.events` y `utils.actions` para el pipeline evento→acción. ([src/model/implementations/Model.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/implementations/Model.java))
- **Concurrencia / threading:** cada entidad dinámica corre en su propio thread y el modelo usa `ConcurrentHashMap` para colecciones compartidas. ([src/model/implementations/Model.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/implementations/Model.java))
- **Principales clases/interfaces/DTOs y su rol:** `Model`, `BodyFactory`, `WeaponFactory`, `PhysicsEngine`. ([src/model/implementations/Model.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/implementations/Model.java), [src/model/bodies/ports/BodyFactory.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/bodies/ports/BodyFactory.java), [src/model/weapons/ports/WeaponFactory.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/weapons/ports/WeaponFactory.java), [src/model/physics/ports/PhysicsEngine.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/physics/ports/PhysicsEngine.java))
- **Patrones de diseño relevantes:** Factory, Strategy, DTO, MVC.
- **Puntos de atención:**
  - Validar throughput del `SpatialGrid` bajo alta densidad.
  - Asegurar coherencia en snapshots frente a concurrencia.

### Subpaquete `model.bodies`

**Descripción:** entidades base del mundo (dinámicos, estáticos, jugador, proyectiles) y sus DTOs. ([src/model/bodies/core/AbstractBody.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/bodies/core/AbstractBody.java))

**Grid resumen de subpaquetes**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `model.bodies.core` | Clase base de bodies. | Ciclo de vida y lógica común. |
| `model.bodies.implementations` | Implementaciones concretas. | Player, dinámicos, proyectiles, estáticos. |
| `model.bodies.ports` | DTOs y contratos. | Interfaces, estados, factories. |

**Análisis detallado**

- **Propósito:** encapsular entidades físicas y su integración con el motor. ([src/model/bodies/core/AbstractBody.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/bodies/core/AbstractBody.java))
- **Responsabilidades principales:** actualizar físicas, emitir eventos, exponer DTOs. ([src/model/bodies/core/AbstractBody.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/bodies/core/AbstractBody.java))
- **Interacción:** usa `model.physics` y `utils.spatial`. ([src/model/bodies/ports/BodyFactory.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/bodies/ports/BodyFactory.java))
- **Concurrencia:** bodies dinámicos ejecutan threads propios. ([src/model/implementations/Model.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/implementations/Model.java))
- **Clases clave:** `AbstractBody`, `DynamicBody`, `PlayerBody`, `ProjectileBody`, `StaticBody`. ([src/model/bodies/core/AbstractBody.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/bodies/core/AbstractBody.java), [src/model/bodies/implementations/DynamicBody.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/bodies/implementations/DynamicBody.java), [src/model/bodies/implementations/PlayerBody.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/bodies/implementations/PlayerBody.java), [src/model/bodies/implementations/ProjectileBody.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/bodies/implementations/ProjectileBody.java), [src/model/bodies/implementations/StaticBody.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/bodies/implementations/StaticBody.java))
- **Patrones:** Factory (`BodyFactory`). ([src/model/bodies/ports/BodyFactory.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/bodies/ports/BodyFactory.java))
- **Detalles críticos:**
  - Hot path en actualización de físicas y colisiones.
  - Manejo de inmunidad de proyectiles es clave para reglas de colisión.
  - Uso de `BodyState` para evitar reentrancia concurrente. ([src/model/bodies/ports/BodyState.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/bodies/ports/BodyState.java))

### Subpaquete `model.emitter`

**Descripción:** emisores de partículas y proyectiles asociados a entidades. ([src/model/emitter/core/AbstractEmitter.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/emitter/core/AbstractEmitter.java))

**Grid resumen de subpaquetes**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `model.emitter.core` | Base de emisores. | Ciclo de vida y configuración. |
| `model.emitter.implementations` | Emisores concretos. | Emisión básica y en ráfaga. |
| `model.emitter.ports` | Contratos/DTOs. | Interfaces y configuración. |

**Análisis detallado**

- **Propósito:** generar cuerpos/efectos a partir de configuraciones. ([src/model/emitter/core/AbstractEmitter.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/emitter/core/AbstractEmitter.java))
- **Responsabilidades:** ejecutar lógica de spawn según configuración. ([src/model/emitter/implementations/BasicEmitter.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/emitter/implementations/BasicEmitter.java))
- **Interacción:** acoplado a `model` para creación de entidades. ([src/model/emitter/implementations/BasicEmitter.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/emitter/implementations/BasicEmitter.java))
- **Patrones:** Template Method/Factory de emisores.

### Subpaquete `model.implementations`

**Descripción:** implementación del modelo con pipeline de eventos y snapshots. ([src/model/implementations/Model.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/implementations/Model.java))

**Grid resumen**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `model.implementations` | Núcleo de simulación. | Eventos, colisiones, snapshots, lifecycle. |

**Análisis detallado**

- **Propósito:** orquestar simulación, colisiones y acciones. ([src/model/implementations/Model.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/implementations/Model.java))
- **Responsabilidades:** manejar mapas de entidades y generar DTOs para render. ([src/model/implementations/Model.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/implementations/Model.java))
- **Concurrencia:** colecciones concurrentes y threads por entidad. ([src/model/implementations/Model.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/implementations/Model.java))
- **Detalles críticos:**
  - Pipeline eventos→acciones es sensible a orden/prioridad.
  - `SpatialGrid` reduce complejidad de colisiones. ([src/utils/spatial/core/SpatialGrid.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/spatial/core/SpatialGrid.java))
  - Snapshot sin asignaciones excesivas (buffers scratch). ([src/model/implementations/Model.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/implementations/Model.java))

### Subpaquete `model.physics`

**Descripción:** motores de físicas intercambiables y DTOs de físicas. ([src/model/physics/ports/PhysicsEngine.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/physics/ports/PhysicsEngine.java))

**Grid resumen de subpaquetes**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `model.physics.core` | Base de motores. | Comportamiento común. |
| `model.physics.implementations` | Motores concretos. | Física básica y null. |
| `model.physics.ports` | Interfaces/DTOs. | Contratos de engine y valores. |

**Análisis detallado**

- **Propósito:** encapsular lógica de movimiento/colisiones por estrategia. ([src/model/physics/ports/PhysicsEngine.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/physics/ports/PhysicsEngine.java))
- **Responsabilidades:** cálculo de nuevas físicas y rebotes. ([src/model/physics/implementations/BasicPhysicsEngine.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/physics/implementations/BasicPhysicsEngine.java))
- **Interacción:** usado por bodies mediante `PhysicsEngine` (Strategy). ([src/model/bodies/ports/BodyFactory.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/bodies/ports/BodyFactory.java))
- **Patrones:** Strategy, Factory.
- **Detalles críticos:**
  - Cambios en física afectan a toda la simulación.
  - Reglas de rebote deben alinearse con límites del mundo.

### Subpaquete `model.ports`

**Descripción:** contratos para estado y procesamiento de eventos en el modelo. ([src/model/ports/DomainEventProcessor.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/ports/DomainEventProcessor.java))

**Grid resumen**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `model.ports` | Interfaces del modelo. | Estado del modelo y eventos. |

**Análisis detallado**

- **Propósito:** desacoplar el modelo de reglas específicas. ([src/model/ports/DomainEventProcessor.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/ports/DomainEventProcessor.java))
- **Responsabilidades:** exponer `ModelState` y contrato de procesamiento de eventos. ([src/model/ports/ModelState.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/ports/ModelState.java), [src/model/ports/DomainEventProcessor.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/ports/DomainEventProcessor.java))
- **Patrones:** Ports & Adapters.

### Subpaquete `model.weapons`

**Descripción:** sistema de armas, tipos, estados y factories. ([src/model/weapons/core/AbstractWeapon.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/weapons/core/AbstractWeapon.java))

**Grid resumen de subpaquetes**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `model.weapons.core` | Base de armas. | Lógica común de disparo. |
| `model.weapons.implementations` | Armas concretas. | Basic, burst, missile, mine. |
| `model.weapons.ports` | Contratos/DTOs. | Interfaces, factory y enums. |

**Análisis detallado**

- **Propósito:** encapsular comportamientos de disparo y configuración. ([src/model/weapons/core/AbstractWeapon.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/weapons/core/AbstractWeapon.java))
- **Responsabilidades principales:** crear armas desde DTOs y manejar estados. ([src/model/weapons/ports/WeaponFactory.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/weapons/ports/WeaponFactory.java), [src/model/weapons/ports/WeaponState.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/weapons/ports/WeaponState.java))
- **Interacción:** dependencias con `model.bodies` y `model.emitter` para proyectiles. ([src/model/weapons/ports/WeaponFactory.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/weapons/ports/WeaponFactory.java))
- **Patrones:** Factory, Strategy.
- **Detalles críticos:**
  - Configuración incorrecta puede afectar balance de juego y spam de entidades.
  - Sincronización con reglas de colisión (inmunidad).

---

## Paquete `resources` (`src/resources`)

Contiene los assets gráficos del juego (imágenes, fondos, sprites). No hay código Java aquí, pero el contenido es consumido por `utils.assets` y `view`. (Directorio `src/resources`)

### Grid resumen

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `resources` | Assets binarios. | Proveer sprites, fondos y efectos visuales. |

### Análisis detallado

- **Propósito:** almacenar recursos gráficos para el engine.
- **Interacción:** se referencian desde `AssetCatalog` y `WorldDefinitionProvider` para construir catálogos. ([src/utils/assets/core/AssetCatalog.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/assets/core/AssetCatalog.java), [src/world/core/AbstractWorldDefinitionProvider.java](https://github.com/unknown/MVCGameEngine/blob/work/src/world/core/AbstractWorldDefinitionProvider.java))
- **Puntos de atención:** verificar rutas y consistencia de IDs de assets.

---

## Paquete `utils` (`src/utils`)

Biblioteca de utilidades y módulos transversales (assets, eventos, spatial grid, imágenes, helpers). ([src/utils/assets/core/AssetCatalog.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/assets/core/AssetCatalog.java), [src/utils/spatial/core/SpatialGrid.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/spatial/core/SpatialGrid.java))

### Grid resumen de subpaquetes

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `utils.actions` | Acciones del engine. | Enum y DTO de acciones. |
| `utils.assets` | Catálogo de assets. | Registro y consulta de assets. |
| `utils.events` | Eventos de dominio. | Eventos, payloads y tipos. |
| `utils.fx` | Efectos visuales. | Tipos de FX y recursos asociados. |
| `utils.helpers` | Helpers genéricos. | Vectores, colecciones, random. |
| `utils.images` | Carga y cacheo de imágenes. | Cache de imágenes y DTOs. |
| `utils.spatial` | Particionado espacial. | Grid, estadísticas, celdas. |

### Análisis detallado

- **Propósito:** dar soporte transversal al engine sin acoplar a capas MVC. ([src/utils/assets/core/AssetCatalog.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/assets/core/AssetCatalog.java))
- **Responsabilidades principales:**
  - Definir acciones y eventos de dominio. ([src/utils/actions/Action.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/actions/Action.java), [src/utils/events/domain/core/AbstractDomainEvent.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/events/domain/core/AbstractDomainEvent.java))
  - Proveer assets e imágenes. ([src/utils/assets/core/AssetCatalog.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/assets/core/AssetCatalog.java), [src/utils/images/ImageCache.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/images/ImageCache.java))
  - Optimizar colisiones con `SpatialGrid`. ([src/utils/spatial/core/SpatialGrid.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/spatial/core/SpatialGrid.java))
- **Interacción:** consumido por `model`, `view`, `world` y `game`. ([src/model/implementations/Model.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/implementations/Model.java), [src/view/core/View.java](https://github.com/unknown/MVCGameEngine/blob/work/src/view/core/View.java))
- **Concurrencia:** estructuras concurrentes en `SpatialGrid`. ([src/utils/spatial/core/SpatialGrid.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/spatial/core/SpatialGrid.java))
- **Patrones:** DTO, Ports & Adapters, caching.

### Subpaquete `utils.actions`

**Descripción:** catálogo de acciones del motor y DTO asociado. ([src/utils/actions/Action.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/actions/Action.java))

**Grid resumen**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `utils.actions` | Definición de acciones. | Enum y DTO para acciones del engine. |

**Análisis detallado**

- **Propósito:** normalizar el conjunto de acciones ejecutables. ([src/utils/actions/Action.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/actions/Action.java))
- **Responsabilidades:** listar tipos de acción y su DTO. ([src/utils/actions/Action.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/actions/Action.java), [src/utils/actions/ActionDTO.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/actions/ActionDTO.java))
- **Interacción:** usado por `game.actions` y `model`. ([src/game/actions/ActionsReboundCollisionPlayerImmunity.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/actions/ActionsReboundCollisionPlayerImmunity.java))

### Subpaquete `utils.assets`

**Descripción:** catálogo y DTOs para assets de juego. ([src/utils/assets/core/AssetCatalog.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/assets/core/AssetCatalog.java))

**Grid resumen de subpaquetes**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `utils.assets.core` | Catálogo de assets. | Registro, lookup, randomización. |
| `utils.assets.implementations` | Catálogos concretos. | Assets del proyecto. |
| `utils.assets.ports` | DTOs/enums. | Tipos e intensidades de assets. |

**Análisis detallado**

- **Propósito:** gestionar IDs y metadata de assets. ([src/utils/assets/core/AssetCatalog.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/assets/core/AssetCatalog.java))
- **Responsabilidades:** registrar assets y exponer selecciones aleatorias. ([src/utils/assets/core/AssetCatalog.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/assets/core/AssetCatalog.java))
- **Interacción:** `world` y `view` consumen catálogos. ([src/world/core/AbstractWorldDefinitionProvider.java](https://github.com/unknown/MVCGameEngine/blob/work/src/world/core/AbstractWorldDefinitionProvider.java), [src/view/core/View.java](https://github.com/unknown/MVCGameEngine/blob/work/src/view/core/View.java))

### Subpaquete `utils.events`

**Descripción:** eventos de dominio y payloads que alimentan el pipeline de acciones. ([src/utils/events/domain/core/AbstractDomainEvent.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/events/domain/core/AbstractDomainEvent.java))

**Grid resumen de subpaquetes**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `utils.events.domain` | Eventos de dominio. | Eventos, tipos, payloads. |

#### Subpaquete `utils.events.domain`

**Grid resumen de subpaquetes**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `utils.events.domain.core` | Base de eventos. | Clase abstracta común. |
| `utils.events.domain.ports` | DTOs y tipos. | Tipos de evento y payloads. |

**Análisis detallado**

- **Propósito:** modelar eventos del dominio de juego. ([src/utils/events/domain/core/AbstractDomainEvent.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/events/domain/core/AbstractDomainEvent.java))
- **Responsabilidades:**
  - Definir clase base `AbstractDomainEvent`. ([src/utils/events/domain/core/AbstractDomainEvent.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/events/domain/core/AbstractDomainEvent.java))
  - Enumerar tipos y payloads. ([src/utils/events/domain/ports/DomainEventType.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/events/domain/ports/DomainEventType.java), [src/utils/events/domain/ports/payloads/CollisionPayload.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/events/domain/ports/payloads/CollisionPayload.java))
- **Interacción:** usado por `model` y `game.actions`. ([src/model/implementations/Model.java](https://github.com/unknown/MVCGameEngine/blob/work/src/model/implementations/Model.java), [src/game/actions/ActionsReboundCollisionPlayerImmunity.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/actions/ActionsReboundCollisionPlayerImmunity.java))

#### Subpaquete `utils.events.domain.ports`

**Grid resumen de subpaquetes**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `utils.events.domain.ports.eventtype` | Tipos concretos de evento. | CollisionEvent, EmitEvent, LimitEvent, etc. |
| `utils.events.domain.ports.payloads` | Payloads de eventos. | Datos asociados a colisiones/emisiones. |

**Análisis detallado**

- **Propósito:** representar eventos con tipos y payloads tipados. ([src/utils/events/domain/ports/eventtype/CollisionEvent.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/events/domain/ports/eventtype/CollisionEvent.java))
- **Responsabilidades:** clasificar eventos y transportar datos. ([src/utils/events/domain/ports/eventtype/CollisionEvent.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/events/domain/ports/eventtype/CollisionEvent.java), [src/utils/events/domain/ports/payloads/EmitPayloadDTO.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/events/domain/ports/payloads/EmitPayloadDTO.java))

### Subpaquete `utils.fx`

**Descripción:** tipos y estructuras para efectos visuales. ([src/utils/fx/Fx.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/fx/Fx.java))

**Grid resumen**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `utils.fx` | Efectos. | Tipos y config de efectos. |

**Análisis detallado**

- **Propósito:** describir efectos de partículas/sprites. ([src/utils/fx/Fx.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/fx/Fx.java))
- **Responsabilidades:** enumeración de tipos y DTOs asociados. ([src/utils/fx/Fx.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/fx/Fx.java), [src/utils/fx/FxType.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/fx/FxType.java))

### Subpaquete `utils.helpers`

**Descripción:** utilidades genéricas para colecciones y vectores. ([src/utils/helpers/RandomArrayList.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/helpers/RandomArrayList.java))

**Grid resumen**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `utils.helpers` | Helpers generales. | Listas aleatorias, vectores. |

**Análisis detallado**

- **Propósito:** simplificar operaciones comunes (vectores y random). ([src/utils/helpers/DoubleVector.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/helpers/DoubleVector.java))

### Subpaquete `utils.images`

**Descripción:** carga y cacheo de imágenes. ([src/utils/images/ImageCache.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/images/ImageCache.java))

**Grid resumen**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `utils.images` | Imágenes y caché. | Cache, DTOs, acceso por ID. |

**Análisis detallado**

- **Propósito:** evitar recarga de assets y mejorar performance en render. ([src/utils/images/ImageCache.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/images/ImageCache.java))
- **Responsabilidades:** cache y metadata de imágenes. ([src/utils/images/ImageCache.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/images/ImageCache.java), [src/utils/images/ImageDTO.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/images/ImageDTO.java))

### Subpaquete `utils.spatial`

**Descripción:** estructura de particionado espacial para colisiones. ([src/utils/spatial/core/SpatialGrid.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/spatial/core/SpatialGrid.java))

**Grid resumen de subpaquetes**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `utils.spatial.core` | Implementación de grid. | Inserción, query, stats. |
| `utils.spatial.ports` | DTOs de estadísticas. | Estadísticas del grid. |

**Análisis detallado**

- **Propósito:** reducir complejidad de colisiones. ([src/utils/spatial/core/SpatialGrid.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/spatial/core/SpatialGrid.java))
- **Responsabilidades:** registrar celdas y obtener candidatos de colisión. ([src/utils/spatial/core/SpatialGrid.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/spatial/core/SpatialGrid.java))
- **Concurrencia:** buckets concurrentes y acceso weakly consistent. ([src/utils/spatial/core/SpatialGrid.java](https://github.com/unknown/MVCGameEngine/blob/work/src/utils/spatial/core/SpatialGrid.java))
- **Detalles críticos:**
  - Ajuste de tamaño de celda impacta rendimiento.
  - La consulta de candidatos es parte del hot path del motor.

---

## Paquete `view` (`src/view`)

Capa de presentación que maneja render activo, HUD e input. ([src/view/core/View.java](https://github.com/unknown/MVCGameEngine/blob/work/src/view/core/View.java))

### Grid resumen de subpaquetes

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `view.core` | Infraestructura de vista. | JFrame, render loop, input. |
| `view.huds` | HUDs y UI. | Widgets y paneles de HUD. |
| `view.renderables` | Renderables y DTOs. | Render de entidades y DTOs de render. |

### Análisis detallado

- **Propósito:** mostrar el estado del juego y recibir input de usuario. ([src/view/core/View.java](https://github.com/unknown/MVCGameEngine/blob/work/src/view/core/View.java))
- **Responsabilidades principales:**
  - Render loop dedicado con `Renderer`. ([src/view/core/Renderer.java](https://github.com/unknown/MVCGameEngine/blob/work/src/view/core/Renderer.java))
  - Gestión de assets y cachés. ([src/view/core/View.java](https://github.com/unknown/MVCGameEngine/blob/work/src/view/core/View.java))
  - HUD y métricas de performance. ([src/view/huds/implementations/SystemHUD.java](https://github.com/unknown/MVCGameEngine/blob/work/src/view/huds/implementations/SystemHUD.java))
- **Interacción:** consume snapshots vía `controller`, no accede al `model` directamente. ([src/view/core/View.java](https://github.com/unknown/MVCGameEngine/blob/work/src/view/core/View.java))
- **Concurrencia:** render thread separado, estrategia copy-on-write para estáticos. ([src/view/core/Renderer.java](https://github.com/unknown/MVCGameEngine/blob/work/src/view/core/Renderer.java))
- **Patrones:** MVC, DTO, Active Rendering Loop.
- **Detalles críticos:**
  - Render loop es hot path; optimización de cache y buffers es clave.
  - La estrategia de snapshots evita locks en tiempo real.

### Subpaquete `view.core`

**Descripción:** núcleo de la vista y render loop. ([src/view/core/Renderer.java](https://github.com/unknown/MVCGameEngine/blob/work/src/view/core/Renderer.java))

**Grid resumen**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `view.core` | Core de View. | JFrame, Renderer, ControlPanel. |

**Análisis detallado**

- **Propósito:** crear UI y lanzar render loop. ([src/view/core/View.java](https://github.com/unknown/MVCGameEngine/blob/work/src/view/core/View.java))
- **Responsabilidades:** gestión de input, render y assets. ([src/view/core/View.java](https://github.com/unknown/MVCGameEngine/blob/work/src/view/core/View.java))
- **Concurrencia:** `Renderer` mantiene thread propio. ([src/view/core/Renderer.java](https://github.com/unknown/MVCGameEngine/blob/work/src/view/core/Renderer.java))

### Subpaquete `view.huds`

**Descripción:** implementación de HUDs y elementos UI. ([src/view/huds/implementations/SystemHUD.java](https://github.com/unknown/MVCGameEngine/blob/work/src/view/huds/implementations/SystemHUD.java))

**Grid resumen de subpaquetes**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `view.huds.core` | Widgets base. | Items, barras, textos. |
| `view.huds.implementations` | HUDs concretos. | HUD de sistema, jugador, grid. |

**Análisis detallado**

- **Propósito:** mostrar métricas y estado de juego. ([src/view/huds/implementations/SystemHUD.java](https://github.com/unknown/MVCGameEngine/blob/work/src/view/huds/implementations/SystemHUD.java))
- **Responsabilidades:** render de elementos del HUD en el canvas. ([src/view/huds/implementations/SystemHUD.java](https://github.com/unknown/MVCGameEngine/blob/work/src/view/huds/implementations/SystemHUD.java))

### Subpaquete `view.renderables`

**Descripción:** clases de renderables y DTOs de render. ([src/view/renderables/implementations/Renderable.java](https://github.com/unknown/MVCGameEngine/blob/work/src/view/renderables/implementations/Renderable.java))

**Grid resumen de subpaquetes**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `view.renderables.implementations` | Renderables concretos. | Render de entidades dinámicas/estáticas. |
| `view.renderables.ports` | DTOs de render. | Datos de render para view. |

**Análisis detallado**

- **Propósito:** materializar sprites en pantalla. ([src/view/renderables/implementations/Renderable.java](https://github.com/unknown/MVCGameEngine/blob/work/src/view/renderables/implementations/Renderable.java))
- **Responsabilidades:** actualización de posiciones y estado de render. ([src/view/renderables/implementations/DynamicRenderable.java](https://github.com/unknown/MVCGameEngine/blob/work/src/view/renderables/implementations/DynamicRenderable.java))
- **Interacción:** consume DTOs del `controller`. ([src/view/renderables/ports/RenderDTO.java](https://github.com/unknown/MVCGameEngine/blob/work/src/view/renderables/ports/RenderDTO.java))

---

## Paquete `world` (`src/world`)

Define el esquema de mundo y catálogos de ítems para inicializar escenarios. ([src/world/core/AbstractWorldDefinitionProvider.java](https://github.com/unknown/MVCGameEngine/blob/work/src/world/core/AbstractWorldDefinitionProvider.java))

### Grid resumen de subpaquetes

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `world.core` | Infraestructura de definición. | Registro de assets y armas. |
| `world.ports` | DTOs y contratos. | Definición del mundo y tipos de ítems. |

### Análisis detallado

- **Propósito:** centralizar definiciones de escenarios y assets. ([src/world/core/AbstractWorldDefinitionProvider.java](https://github.com/unknown/MVCGameEngine/blob/work/src/world/core/AbstractWorldDefinitionProvider.java))
- **Responsabilidades principales:**
  - Registrar assets y armas disponibles. ([src/world/core/WorldAssetsRegister.java](https://github.com/unknown/MVCGameEngine/blob/work/src/world/core/WorldAssetsRegister.java), [src/world/core/WeaponDefRegister.java](https://github.com/unknown/MVCGameEngine/blob/work/src/world/core/WeaponDefRegister.java))
  - Exponer listas de entidades y fondos. ([src/world/ports/WorldDefinition.java](https://github.com/unknown/MVCGameEngine/blob/work/src/world/ports/WorldDefinition.java))
- **Interacción:** consumido por `game` para inicialización y por `controller` para crear entidades. ([src/game/Main.java](https://github.com/unknown/MVCGameEngine/blob/work/src/game/Main.java))
- **Patrones:** Factory/Registry, DTOs.
- **Puntos de atención:** consistencia entre IDs de assets y recursos reales.

### Subpaquete `world.core`

**Descripción:** registro de assets y armas, y base de proveedores de mundos. ([src/world/core/AbstractWorldDefinitionProvider.java](https://github.com/unknown/MVCGameEngine/blob/work/src/world/core/AbstractWorldDefinitionProvider.java))

**Grid resumen**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `world.core` | Base de world definition. | Registro de assets, armas, builders. |

**Análisis detallado**

- **Propósito:** centralizar helpers de definición de mundo. ([src/world/core/AbstractWorldDefinitionProvider.java](https://github.com/unknown/MVCGameEngine/blob/work/src/world/core/AbstractWorldDefinitionProvider.java))
- **Responsabilidades:** creación de DTOs de ítems y armas. ([src/world/core/WeaponDefFactory.java](https://github.com/unknown/MVCGameEngine/blob/work/src/world/core/WeaponDefFactory.java))

### Subpaquete `world.ports`

**Descripción:** contratos y DTOs de definición de mundo. ([src/world/ports/WorldDefinition.java](https://github.com/unknown/MVCGameEngine/blob/work/src/world/ports/WorldDefinition.java))

**Grid resumen**

| Paquete | Descripción breve | Responsabilidades clave |
| --- | --- | --- |
| `world.ports` | DTOs de world definition. | Definiciones de ítems, armas, fondos. |

**Análisis detallado**

- **Propósito:** estructurar definiciones consumidas por generadores. ([src/world/ports/WorldDefinition.java](https://github.com/unknown/MVCGameEngine/blob/work/src/world/ports/WorldDefinition.java))
- **Responsabilidades:** tipos de ítems y DTOs de definición. ([src/world/ports/DefItemDTO.java](https://github.com/unknown/MVCGameEngine/blob/work/src/world/ports/DefItemDTO.java), [src/world/ports/DefWeaponDTO.java](https://github.com/unknown/MVCGameEngine/blob/work/src/world/ports/DefWeaponDTO.java))

---

## Observaciones finales

- No se encontraron issues referenciados en el repositorio que afecten la comprensión de los paquetes.
- No existe un paquete `_helper` en el árbol actual; el equivalente presente es `utils.helpers`.
- Se recomienda añadir pruebas de integración alrededor de `Model` y `Controller`, donde confluyen eventos, reglas y render.

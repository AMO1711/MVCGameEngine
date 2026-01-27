Informe jerárquico de paquetes para local/MVCGameEngine — rama: work — generado el 2026-01-27.

## Micro-resumen arquitectural
El proyecto sigue un esquema MVC clásico: `model` contiene la simulación y reglas de dominio, `view` se ocupa del rendering y el input, y `controller` coordina el flujo entre ambos. Encima de esa base hay un paquete `game` con configuraciones concretas (acciones, niveles, IA y definición de mundos) y un paquete `world` con DTOs y fábricas de definiciones. `utils` agrupa infraestructura transversal (eventos, assets, imágenes, helpers), mientras `resources` contiene activos no Java.

**Paquetes de primer nivel detectados:** `view`, `world`, `model`, `game`, `utils`, `resources`, `controller`.

## Paquetes de primer nivel (grid resumen)

| Paquete | Descripción breve | Subpaquetes internos (lista corta) |
| --- | --- | --- |
| `view` | Presentación Swing, render loop y HUDs. | `core`, `huds`, `renderables` |
| `world` | Definiciones y DTOs del mundo/juego. | `ports`, `core` |
| `model` | Simulación y estado del juego (física, cuerpos, armas). | `ports`, `physics`, `emitter`, `spatial`, `bodies`, `implementations`, `weapons` |
| `game` | Configuraciones y reglas concretas del juego. | `core`, `implementations` |
| `utils` | Infraestructura transversal (eventos, assets, helpers). | `actions`, `assets`, `events`, `fx`, `helpers`, `images` |
| `resources` | Activos no Java (imágenes). | `images` |
| `controller` | Orquestación MVC, mappers y puertos. | `mappers`, `ports`, `implementations` |

---

## Paquete: `view`
**Descripción:** Capa de presentación basada en Swing que administra el rendering activo, HUDs y DTOs de render. Coordina input del usuario y consume snapshots del `controller`.

### Resumen del paquete
| Subpaquetes | Responsabilidades clave (máx. 5) | Principales clases (máx. 8) |
| --- | --- | --- |
| `core`, `huds`, `renderables` | UI Swing, render loop activo, HUDs, DTOs visuales, sprites/cache de imágenes | [`View`](https://github.com/local/MVCGameEngine/blob/work/src/view/core/View.java), [`Renderer`](https://github.com/local/MVCGameEngine/blob/work/src/view/core/Renderer.java), [`ControlPanel`](https://github.com/local/MVCGameEngine/blob/work/src/view/core/ControlPanel.java), [`PlayerHUD`](https://github.com/local/MVCGameEngine/blob/work/src/view/huds/implementations/PlayerHUD.java), [`SystemHUD`](https://github.com/local/MVCGameEngine/blob/work/src/view/huds/implementations/SystemHUD.java), [`SpatialGridHUD`](https://github.com/local/MVCGameEngine/blob/work/src/view/huds/implementations/SpatialGridHUD.java), [`DynamicRenderDTO`](https://github.com/local/MVCGameEngine/blob/work/src/view/renderables/ports/DynamicRenderDTO.java), [`Renderable`](https://github.com/local/MVCGameEngine/blob/work/src/view/renderables/implementations/Renderable.java) |

### Análisis detallado
- **Propósito y responsabilidades:** Rendering activo con buffer triple, HUDs de diagnóstico y conversión de DTOs visuales. También traduce eventos de teclado en comandos del controlador.
- **Interacción con otras capas:** Consume snapshots del `controller` (e.g., `getDynamicRenderablesData`) y publica acciones de input al controlador. No toca el `model` directamente.
- **Concurrencia:** El render loop corre en su propio thread; usa colecciones concurrentes y estrategias de copy-on-write para renderables estáticos.
- **Patrones de diseño:** MVC, DTO, Strategy de renderizado por renderables, doble buffer/triple buffer.
- **Riesgos/puntos de atención:** La separación de snapshot dinámico vs. estático exige coherencia entre actualizaciones; potenciales costos de sincronización si se incrementa el número de entidades.
- **Recomendaciones:** Añadir métricas y pruebas de rendimiento para el loop de render; documentar la estrategia de snapshot estático para futuros cambios.

### Subpaquete: `view.core`
**Descripción:** Núcleo de la UI y el loop de render. Encapsula el canvas, la ventana principal y la interacción con el controlador.

| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| — | Ventana Swing, render loop, input de teclado, configuración de assets | [`View`](https://github.com/local/MVCGameEngine/blob/work/src/view/core/View.java), [`Renderer`](https://github.com/local/MVCGameEngine/blob/work/src/view/core/Renderer.java), [`ControlPanel`](https://github.com/local/MVCGameEngine/blob/work/src/view/core/ControlPanel.java), [`SystemDTO`](https://github.com/local/MVCGameEngine/blob/work/src/view/core/SystemDTO.java) |

**Análisis detallado**
- **Responsabilidades:** Arranque de ventana, activación del render loop, carga de imágenes, y manejo de input para comandos del jugador.
- **Interacciones:** `View` recibe un `Controller` inyectado y lo consulta para obtener DTOs de render. `Renderer` consulta `View` para snapshots y estado del motor.
- **Concurrencia:** `Renderer` corre en thread propio; usa `ConcurrentHashMap` y swaps atómicos para renderables estáticos.
- **Patrones:** MVC, Active Object (render thread), DTOs.
- **Puntos de atención:** La sincronización entre el render thread y el thread de simulación depende de snapshots consistentes; cambios aquí pueden impactar estabilidad visual.
- **Recomendaciones:** Agregar un modo de pausa explícito en `EngineState` para evitar render loop innecesario; documentar expectativas del frame pacing.

**Detalles críticos**
- Render loop de `Renderer` con BufferStrategy triple y VolatileImage para fondo; cambios deben probarse con diferentes GPUs.
- Estrategia de copy-on-write para renderables estáticos para evitar locks en el render thread.
- Acceso concurrente a `dynamicRenderables` desde el render thread y eventos de muerte necesita limpieza consistente.

### Subpaquete: `view.huds`
**Descripción:** Widgets de HUD y componentes básicos reutilizables para superposiciones de debugging y UI.

| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| `core`, `implementations` | Componentes base de HUD, render de texto/barras, HUDs concretos | [`GridHUD`](https://github.com/local/MVCGameEngine/blob/work/src/view/huds/core/GridHUD.java), [`DataHUD`](https://github.com/local/MVCGameEngine/blob/work/src/view/huds/core/DataHUD.java), [`Item`](https://github.com/local/MVCGameEngine/blob/work/src/view/huds/core/Item.java), [`PlayerHUD`](https://github.com/local/MVCGameEngine/blob/work/src/view/huds/implementations/PlayerHUD.java), [`SystemHUD`](https://github.com/local/MVCGameEngine/blob/work/src/view/huds/implementations/SystemHUD.java), [`SpatialGridHUD`](https://github.com/local/MVCGameEngine/blob/work/src/view/huds/implementations/SpatialGridHUD.java) |

**Análisis detallado**
- **Responsabilidades:** Composición de ítems UI (texto, iconos, barras) y HUDs concretos para jugador, sistema y grid espacial.
- **Interacciones:** Consumidos por `Renderer` para dibujar overlays; no interactúan con `model`.
- **Concurrencia:** Operan en el render thread, sin acceso concurrente explícito.
- **Patrones:** Composite/Renderer para elementos del HUD.
- **Riesgos:** HUDs podrían impactar performance si se incrementa la lógica por frame.
- **Recomendaciones:** Asegurar que el dibujo de HUD sea opcional en builds de producción.

### Subpaquete: `view.renderables`
**Descripción:** DTOs y clases de render que representan snapshots visuales de entidades.

| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| `ports`, `implementations` | DTOs de render, sprites dinámicos/estáticos | [`RenderDTO`](https://github.com/local/MVCGameEngine/blob/work/src/view/renderables/ports/RenderDTO.java), [`DynamicRenderDTO`](https://github.com/local/MVCGameEngine/blob/work/src/view/renderables/ports/DynamicRenderDTO.java), [`PlayerRenderDTO`](https://github.com/local/MVCGameEngine/blob/work/src/view/renderables/ports/PlayerRenderDTO.java), [`SpatialGridStatisticsRenderDTO`](https://github.com/local/MVCGameEngine/blob/work/src/view/renderables/ports/SpatialGridStatisticsRenderDTO.java), [`Renderable`](https://github.com/local/MVCGameEngine/blob/work/src/view/renderables/implementations/Renderable.java), [`DynamicRenderable`](https://github.com/local/MVCGameEngine/blob/work/src/view/renderables/implementations/DynamicRenderable.java) |

**Análisis detallado**
- **Responsabilidades:** Encapsular datos de render (posición, ángulo, assetId) y permitir actualización/paint de sprites.
- **Interacciones:** DTOs son producidos por el `controller` y consumidos por el `Renderer`.
- **Concurrencia:** Los DTOs se tratan como snapshots inmutables.
- **Patrones:** DTO, estrategia de render por tipo de entidad.
- **Recomendaciones:** Documentar contrato de inmutabilidad para evitar referencias compartidas.

---

## Paquete: `controller`
**Descripción:** Orquestador del MVC. Traduce eventos de UI a acciones del modelo y mapea DTOs de dominio a DTOs de render.

### Resumen del paquete
| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| `mappers`, `ports`, `implementations` | Coordinación MVC, mapping DTOs, control de estado del motor | [`Controller`](https://github.com/local/MVCGameEngine/blob/work/src/controller/implementations/Controller.java), [`ActionsGenerator`](https://github.com/local/MVCGameEngine/blob/work/src/controller/ports/ActionsGenerator.java), [`EngineState`](https://github.com/local/MVCGameEngine/blob/work/src/controller/ports/EngineState.java), [`WorldInitializer`](https://github.com/local/MVCGameEngine/blob/work/src/controller/ports/WorldInitializer.java), [`WorldEvolver`](https://github.com/local/MVCGameEngine/blob/work/src/controller/ports/WorldEvolver.java), [`RenderableMapper`](https://github.com/local/MVCGameEngine/blob/work/src/controller/mappers/RenderableMapper.java), [`DynamicRenderableMapper`](https://github.com/local/MVCGameEngine/blob/work/src/controller/mappers/DynamicRenderableMapper.java), [`PlayerRenderableMapper`](https://github.com/local/MVCGameEngine/blob/work/src/controller/mappers/PlayerRenderableMapper.java) |

### Análisis detallado
- **Propósito:** Coordinar la activación del motor, iniciar el mundo y mediar entre `model` y `view` mediante DTOs.
- **Interacción con capas:** Consume `Model` para snapshots y comandos; sirve a `View` con DTOs de render y estado.
- **Concurrencia:** Recibe calls desde render thread (pull de snapshots) y desde threads de simulación (notificaciones de entidades), evitando estado compartido mediante DTOs nuevos.
- **Patrones:** Facade, DTO Mapper, MVC.
- **Riesgos:** Cambio en contratos de DTOs puede romper mappers; revisiones deben mantenerse sincronizadas.
- **Recomendaciones:** Añadir tests unitarios para mappers y flujos de activación.

### Subpaquete: `controller.mappers`
**Descripción:** Transformadores entre DTOs de dominio (model/world) y DTOs de render (view).

| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| — | Mapping entre capas, separación de concerns | [`RenderableMapper`](https://github.com/local/MVCGameEngine/blob/work/src/controller/mappers/RenderableMapper.java), [`DynamicRenderableMapper`](https://github.com/local/MVCGameEngine/blob/work/src/controller/mappers/DynamicRenderableMapper.java), [`PlayerRenderableMapper`](https://github.com/local/MVCGameEngine/blob/work/src/controller/mappers/PlayerRenderableMapper.java), [`SpatialGridStatisticsMapper`](https://github.com/local/MVCGameEngine/blob/work/src/controller/mappers/SpatialGridStatisticsMapper.java), [`WeaponMapper`](https://github.com/local/MVCGameEngine/blob/work/src/controller/mappers/WeaponMapper.java), [`WeaponTypeMapper`](https://github.com/local/MVCGameEngine/blob/work/src/controller/mappers/WeaponTypeMapper.java), [`EmitterMapper`](https://github.com/local/MVCGameEngine/blob/work/src/controller/mappers/EmitterMapper.java) |

**Análisis detallado**
- **Responsabilidades:** Transformar DTOs de dominio a DTOs de render sin compartir estado mutable.
- **Interacciones:** Consumidos principalmente por `Controller`.
- **Patrones:** Mapper, DTO.
- **Riesgos:** Desalineación con cambios en DTOs; falta de tests.
- **Recomendaciones:** Asegurar tests de mappers con casos de borde.

### Subpaquete: `controller.ports`
**Descripción:** Interfaces y enums que definen contratos entre capas.

| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| — | Contratos del controlador y estado del engine | [`ActionsGenerator`](https://github.com/local/MVCGameEngine/blob/work/src/controller/ports/ActionsGenerator.java), [`EngineState`](https://github.com/local/MVCGameEngine/blob/work/src/controller/ports/EngineState.java), [`WorldInitializer`](https://github.com/local/MVCGameEngine/blob/work/src/controller/ports/WorldInitializer.java), [`WorldEvolver`](https://github.com/local/MVCGameEngine/blob/work/src/controller/ports/WorldEvolver.java) |

**Análisis detallado**
- **Responsabilidades:** Definir API mínima para inicialización y evolución del mundo.
- **Patrones:** Ports & Adapters.
- **Recomendaciones:** Documentar invariantes de `EngineState` para facilitar nuevos modos (pausa).

### Subpaquete: `controller.implementations`
**Descripción:** Implementación concreta del `Controller`.

| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| — | Coordinación MVC y flujo de juego | [`Controller`](https://github.com/local/MVCGameEngine/blob/work/src/controller/implementations/Controller.java) |

**Análisis detallado**
- **Responsabilidades:** Arranque del engine, configuración de world dimension, y puente entre capas.
- **Patrones:** Facade, Dependency Injection.
- **Recomendaciones:** Tests de integración mínimos para flujos `activate()`.

---

## Paquete: `model`
**Descripción:** Núcleo de simulación; gestiona cuerpos, física, armas y eventos de dominio.

### Resumen del paquete
| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| `ports`, `physics`, `emitter`, `spatial`, `bodies`, `implementations`, `weapons` | Simulación, física, colisiones, eventos de dominio, snapshots DTO | [`Model`](https://github.com/local/MVCGameEngine/blob/work/src/model/implementations/Model.java), [`AbstractBody`](https://github.com/local/MVCGameEngine/blob/work/src/model/bodies/core/AbstractBody.java), [`DynamicBody`](https://github.com/local/MVCGameEngine/blob/work/src/model/bodies/implementations/DynamicBody.java), [`PlayerBody`](https://github.com/local/MVCGameEngine/blob/work/src/model/bodies/implementations/PlayerBody.java), [`ProjectileBody`](https://github.com/local/MVCGameEngine/blob/work/src/model/bodies/implementations/ProjectileBody.java), [`SpatialGrid`](https://github.com/local/MVCGameEngine/blob/work/src/model/spatial/core/SpatialGrid.java), [`AbstractPhysicsEngine`](https://github.com/local/MVCGameEngine/blob/work/src/model/physics/core/AbstractPhysicsEngine.java), [`AbstractWeapon`](https://github.com/local/MVCGameEngine/blob/work/src/model/weapons/core/AbstractWeapon.java) |

> Nota: el paquete `model` contiene más de 15 clases; se listan las más relevantes. Otros archivos no listados — ver enlace al directorio.

### Análisis detallado
- **Propósito:** Ejecutar la simulación, manejar colisiones y generar eventos del dominio.
- **Interacción con otras capas:** Expone snapshots DTO al `controller` y recibe comandos del `controller` (e.g., thrust, fire).
- **Concurrencia:** Cada entidad dinámica puede tener su propio thread; uso intensivo de `ConcurrentHashMap` y snapshots inmutables.
- **Patrones:** Strategy (motores de física/armas), Factory (`BodyFactory`, `WeaponFactory`), DTO, Observer de eventos.
- **Riesgos:** Alto acoplamiento entre cuerpos, física y eventos; cambios en el modelo pueden impactar IA y render.
- **Recomendaciones:** Pruebas de colisiones y rendimiento; documentar parámetros de `SpatialGrid`.

**Detalles críticos**
- Física y colisiones se apoyan en `SpatialGrid` para reducir complejidad; cualquier cambio en tamaño de celda impacta performance.
- El pipeline de eventos → acciones es central para comportamiento; debe ser validado en escenarios de carga.
- Concurrencia por entidad requiere vigilancia en estados y sincronización para evitar inconsistencias.

### Subpaquete: `model.implementations`
**Descripción:** Implementación principal del modelo y estado general.

| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| — | Simulación central, snapshots, eventos | [`Model`](https://github.com/local/MVCGameEngine/blob/work/src/model/implementations/Model.java) |

**Análisis detallado**
- **Responsabilidades:** Administración de entidades, generación de snapshots, gestión del estado del motor.
- **Interacciones:** Consume `DomainEventProcessor` y publica eventos para acciones.
- **Concurrencia:** `ConcurrentHashMap` para entidades; buffers reutilizables para snapshots.
- **Recomendaciones:** Aislar la configuración del `SpatialGrid` para facilitar tuning.

### Subpaquete: `model.ports`
**Descripción:** Contratos del modelo y estado.

| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| — | Interfaces y enums para eventos y estado | [`DomainEventProcessor`](https://github.com/local/MVCGameEngine/blob/work/src/model/ports/DomainEventProcessor.java), [`ModelState`](https://github.com/local/MVCGameEngine/blob/work/src/model/ports/ModelState.java) |

**Análisis detallado**
- **Responsabilidades:** Formalizar cómo se procesan eventos y el ciclo de vida del modelo.
- **Recomendaciones:** Documentar transición de estados.

### Subpaquete: `model.physics`
**Descripción:** Motores de física y valores de estado físico.

| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| `ports`, `implementations`, `core` | Cálculo de física, rebotes, DTOs de valores físicos | [`PhysicsEngine`](https://github.com/local/MVCGameEngine/blob/work/src/model/physics/ports/PhysicsEngine.java), [`PhysicsValuesDTO`](https://github.com/local/MVCGameEngine/blob/work/src/model/physics/ports/PhysicsValuesDTO.java), [`AbstractPhysicsEngine`](https://github.com/local/MVCGameEngine/blob/work/src/model/physics/core/AbstractPhysicsEngine.java), [`BasicPhysicsEngine`](https://github.com/local/MVCGameEngine/blob/work/src/model/physics/implementations/BasicPhysicsEngine.java), [`NullPhysicsEngine`](https://github.com/local/MVCGameEngine/blob/work/src/model/physics/implementations/NullPhysicsEngine.java) |

**Análisis detallado**
- **Responsabilidades:** Mantener y actualizar posición, velocidad, aceleración, rebotes.
- **Interacciones:** Usado por cuerpos dinámicos y proyectiles.
- **Concurrencia:** Uso de `AtomicReference` para valores físicos en `AbstractPhysicsEngine`.
- **Patrones:** Strategy (motores de física), DTO.
- **Riesgos:** Cambios en cálculos pueden afectar determinismo; requiere pruebas de colisión.

### Subpaquete: `model.emitter`
**Descripción:** Emisores para trails o partículas.

| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| `ports`, `implementations`, `core` | Emisión de cuerpos o partículas | [`Emitter`](https://github.com/local/MVCGameEngine/blob/work/src/model/emitter/ports/Emitter.java), [`EmitterConfigDto`](https://github.com/local/MVCGameEngine/blob/work/src/model/emitter/ports/EmitterConfigDto.java), [`AbstractEmitter`](https://github.com/local/MVCGameEngine/blob/work/src/model/emitter/core/AbstractEmitter.java), [`BasicEmitter`](https://github.com/local/MVCGameEngine/blob/work/src/model/emitter/implementations/BasicEmitter.java), [`BurstEmitter`](https://github.com/local/MVCGameEngine/blob/work/src/model/emitter/implementations/BurstEmitter.java) |

**Análisis detallado**
- **Responsabilidades:** Generar eventos de emisión para efectos o proyectiles.
- **Interacciones:** Controlado por `Model` y `Controller` al equipar jugadores.
- **Patrones:** Strategy, Template Method en emisores abstractos.
- **Recomendaciones:** Documentar tasas de emisión y límites.

### Subpaquete: `model.spatial`
**Descripción:** Particionamiento espacial para colisiones.

| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| `ports`, `core` | Grid espacial, estadísticas de colisión | [`SpatialGrid`](https://github.com/local/MVCGameEngine/blob/work/src/model/spatial/core/SpatialGrid.java), [`Cells`](https://github.com/local/MVCGameEngine/blob/work/src/model/spatial/core/Cells.java), [`SpatialGridStatisticsDTO`](https://github.com/local/MVCGameEngine/blob/work/src/model/spatial/ports/SpatialGridStatisticsDTO.java) |

**Análisis detallado**
- **Responsabilidades:** Asignar cuerpos a celdas y proveer candidatos para colisiones.
- **Interacciones:** Consumido por `Model` y HUDs de `view` para estadísticas.
- **Concurrencia:** Buckets concurrentes con consistencia débil.
- **Patrones:** Spatial partitioning.
- **Recomendaciones:** Exponer métricas para tuning dinámico.

### Subpaquete: `model.bodies`
**Descripción:** Definición de cuerpos físicos y estados.

| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| `ports`, `implementations`, `core` | Entidades físicas, DTOs y estados | [`AbstractBody`](https://github.com/local/MVCGameEngine/blob/work/src/model/bodies/core/AbstractBody.java), [`DynamicBody`](https://github.com/local/MVCGameEngine/blob/work/src/model/bodies/implementations/DynamicBody.java), [`StaticBody`](https://github.com/local/MVCGameEngine/blob/work/src/model/bodies/implementations/StaticBody.java), [`ProjectileBody`](https://github.com/local/MVCGameEngine/blob/work/src/model/bodies/implementations/ProjectileBody.java), [`PlayerBody`](https://github.com/local/MVCGameEngine/blob/work/src/model/bodies/implementations/PlayerBody.java), [`BodyDTO`](https://github.com/local/MVCGameEngine/blob/work/src/model/bodies/ports/BodyDTO.java), [`PlayerDTO`](https://github.com/local/MVCGameEngine/blob/work/src/model/bodies/ports/PlayerDTO.java), [`BodyFactory`](https://github.com/local/MVCGameEngine/blob/work/src/model/bodies/ports/BodyFactory.java) |

**Análisis detallado**
- **Responsabilidades:** Modelar cuerpos dinámicos/estáticos y su estado.
- **Interacciones:** `Model` crea y gestiona instancias; `Controller` consume DTOs.
- **Concurrencia:** Los cuerpos dinámicos pueden correr en threads propios; estados compartidos deben sincronizarse.
- **Patrones:** Factory, State (BodyState).
- **Recomendaciones:** Tests de integridad para cuerpos y conversiones DTO.

### Subpaquete: `model.weapons`
**Descripción:** Armas y comportamiento de disparo.

| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| `ports`, `implementations`, `core` | Armas, estados, fábricas | [`Weapon`](https://github.com/local/MVCGameEngine/blob/work/src/model/weapons/ports/Weapon.java), [`WeaponFactory`](https://github.com/local/MVCGameEngine/blob/work/src/model/weapons/ports/WeaponFactory.java), [`WeaponDto`](https://github.com/local/MVCGameEngine/blob/work/src/model/weapons/ports/WeaponDto.java), [`WeaponState`](https://github.com/local/MVCGameEngine/blob/work/src/model/weapons/ports/WeaponState.java), [`AbstractWeapon`](https://github.com/local/MVCGameEngine/blob/work/src/model/weapons/core/AbstractWeapon.java), [`BasicWeapon`](https://github.com/local/MVCGameEngine/blob/work/src/model/weapons/implementations/BasicWeapon.java), [`BurstWeapon`](https://github.com/local/MVCGameEngine/blob/work/src/model/weapons/implementations/BurstWeapon.java), [`MissileLauncher`](https://github.com/local/MVCGameEngine/blob/work/src/model/weapons/implementations/MissileLauncher.java) |

**Análisis detallado**
- **Responsabilidades:** Encapsular lógica de fire rate, munición y generación de proyectiles.
- **Interacciones:** Armas equipadas en `PlayerBody`, configuradas desde `world` y `controller`.
- **Patrones:** Strategy, Factory.
- **Recomendaciones:** Tests para cada arma y condiciones de recarga.

---

## Paquete: `world`
**Descripción:** Definiciones estáticas de mundo, assets y armas.

### Resumen del paquete
| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| `ports`, `core` | DTOs de definiciones, fábricas de definiciones | [`WorldDefinition`](https://github.com/local/MVCGameEngine/blob/work/src/world/ports/WorldDefinition.java), [`WorldDefinitionProvider`](https://github.com/local/MVCGameEngine/blob/work/src/world/ports/WorldDefinitionProvider.java), [`DefItemDTO`](https://github.com/local/MVCGameEngine/blob/work/src/world/ports/DefItemDTO.java), [`DefWeaponDTO`](https://github.com/local/MVCGameEngine/blob/work/src/world/ports/DefWeaponDTO.java), [`DefWeaponType`](https://github.com/local/MVCGameEngine/blob/work/src/world/ports/DefWeaponType.java), [`DefEmitterDTO`](https://github.com/local/MVCGameEngine/blob/work/src/world/ports/DefEmitterDTO.java), [`WeaponDefFactory`](https://github.com/local/MVCGameEngine/blob/work/src/world/core/WeaponDefFactory.java), [`WorldAssetsRegister`](https://github.com/local/MVCGameEngine/blob/work/src/world/core/WorldAssetsRegister.java) |

### Análisis detallado
- **Propósito:** Encapsular definiciones de assets y prototipos del mundo, desacopladas del runtime.
- **Interacciones:** `game` aporta implementaciones de `WorldDefinitionProvider`; `controller` consume `WorldDefinition`.
- **Patrones:** Factory, DTO, Provider.
- **Puntos de atención:** Alinear assets registrados con catálogos de `utils.assets`.
- **Recomendaciones:** Validaciones más estrictas para definiciones inválidas.

### Subpaquete: `world.ports`
**Descripción:** Contratos y DTOs para la definición del mundo.

| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| — | DTOs de assets, items y armas | [`WorldDefinition`](https://github.com/local/MVCGameEngine/blob/work/src/world/ports/WorldDefinition.java), [`WorldDefinitionProvider`](https://github.com/local/MVCGameEngine/blob/work/src/world/ports/WorldDefinitionProvider.java), [`DefItem`](https://github.com/local/MVCGameEngine/blob/work/src/world/ports/DefItem.java), [`DefItemDTO`](https://github.com/local/MVCGameEngine/blob/work/src/world/ports/DefItemDTO.java), [`DefItemPrototypeDTO`](https://github.com/local/MVCGameEngine/blob/work/src/world/ports/DefItemPrototypeDTO.java), [`DefWeaponDTO`](https://github.com/local/MVCGameEngine/blob/work/src/world/ports/DefWeaponDTO.java), [`DefWeaponType`](https://github.com/local/MVCGameEngine/blob/work/src/world/ports/DefWeaponType.java), [`DefBackgroundDTO`](https://github.com/local/MVCGameEngine/blob/work/src/world/ports/DefBackgroundDTO.java) |

**Análisis detallado**
- **Responsabilidades:** Tipos y DTOs del mundo con serialización fácil.
- **Recomendaciones:** Añadir documentación de campos para integraciones externas.

### Subpaquete: `world.core`
**Descripción:** Fábricas y registros de definiciones.

| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| — | Crear presets de armas, registro de assets | [`WeaponDefFactory`](https://github.com/local/MVCGameEngine/blob/work/src/world/core/WeaponDefFactory.java), [`WeaponDefRegister`](https://github.com/local/MVCGameEngine/blob/work/src/world/core/WeaponDefRegister.java), [`WorldAssetsRegister`](https://github.com/local/MVCGameEngine/blob/work/src/world/core/WorldAssetsRegister.java), [`AbstractWorldDefinitionProvider`](https://github.com/local/MVCGameEngine/blob/work/src/world/core/AbstractWorldDefinitionProvider.java) |

**Análisis detallado**
- **Responsabilidades:** Centralizar definiciones preconfiguradas y registros.
- **Patrones:** Factory, Registry.
- **Recomendaciones:** Añadir pruebas de consistencia para presets.

---

## Paquete: `game`
**Descripción:** Configuraciones concretas (niveles, IA, acciones y mundos) que usan las APIs de `controller` y `world`.

### Resumen del paquete
| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| `core`, `implementations` | Arranque del juego, reglas concretas, IA y niveles | [`Main`](https://github.com/local/MVCGameEngine/blob/work/src/game/Main.java), [`AbstractLevelGenerator`](https://github.com/local/MVCGameEngine/blob/work/src/game/core/AbstractLevelGenerator.java), [`AbstractIAGenerator`](https://github.com/local/MVCGameEngine/blob/work/src/game/core/AbstractIAGenerator.java), [`LevelBasic`](https://github.com/local/MVCGameEngine/blob/work/src/game/implementations/level/LevelBasic.java), [`AIBasicSpawner`](https://github.com/local/MVCGameEngine/blob/work/src/game/implementations/ai/AIBasicSpawner.java), [`ActionsReboundCollisionPlayerImmunity`](https://github.com/local/MVCGameEngine/blob/work/src/game/implementations/actions/ActionsReboundCollisionPlayerImmunity.java), [`RandomWorldDefinitionProvider`](https://github.com/local/MVCGameEngine/blob/work/src/game/implementations/world/RandomWorldDefinitionProvider.java), [`EarthInCenterWorldDefinitionProvider`](https://github.com/local/MVCGameEngine/blob/work/src/game/implementations/world/EarthInCenterWorldDefinitionProvider.java) |

### Análisis detallado
- **Propósito:** Componer el juego con reglas concretas usando el motor.
- **Interacciones:** Invoca `controller` para configurar y poblar el mundo; consume `world` para definiciones.
- **Concurrencia:** IA se ejecuta en threads dedicados (`AbstractIAGenerator`).
- **Patrones:** Template Method (generadores), Strategy (reglas de acciones), MVC en el entry point.
- **Riesgos:** Ajustes de reglas impactan equilibrio y dificultad; deben testearse en escenarios de carga.
- **Recomendaciones:** Introducir configuración externa para facilitar tuning.

### Subpaquete: `game.core`
**Descripción:** Clases base para generadores de niveles e IA.

| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| — | Generadores base de IA y niveles | [`AbstractLevelGenerator`](https://github.com/local/MVCGameEngine/blob/work/src/game/core/AbstractLevelGenerator.java), [`AbstractIAGenerator`](https://github.com/local/MVCGameEngine/blob/work/src/game/core/AbstractIAGenerator.java) |

**Análisis detallado**
- **Responsabilidades:** Proveer plantillas para niveles y generación de entidades.
- **Patrones:** Template Method.
- **Recomendaciones:** Instrumentar logs y métricas por nivel.

### Subpaquete: `game.implementations`
**Descripción:** Implementaciones concretas de acciones, IA, niveles y mundos.

| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| `actions`, `ai`, `level`, `world` | Reglas de acción, spawners, niveles y definición de mundos | [`ActionsReboundCollisionPlayerImmunity`](https://github.com/local/MVCGameEngine/blob/work/src/game/implementations/actions/ActionsReboundCollisionPlayerImmunity.java), [`ActionsReboundAndCollision`](https://github.com/local/MVCGameEngine/blob/work/src/game/implementations/actions/ActionsReboundAndCollision.java), [`AIBasicSpawner`](https://github.com/local/MVCGameEngine/blob/work/src/game/implementations/ai/AIBasicSpawner.java), [`LevelBasic`](https://github.com/local/MVCGameEngine/blob/work/src/game/implementations/level/LevelBasic.java), [`RandomWorldDefinitionProvider`](https://github.com/local/MVCGameEngine/blob/work/src/game/implementations/world/RandomWorldDefinitionProvider.java), [`EarthInCenterWorldDefinitionProvider`](https://github.com/local/MVCGameEngine/blob/work/src/game/implementations/world/EarthInCenterWorldDefinitionProvider.java) |

**Análisis detallado**
- **Responsabilidades:** Implementar reglas específicas de gameplay.
- **Concurrencia:** Spawners y generadores usan threads independientes.
- **Patrones:** Strategy, Template Method.
- **Recomendaciones:** Añadir tests de reglas de colisión y emisión.

---

## Paquete: `utils`
**Descripción:** Utilidades y módulos transversales del motor.

### Resumen del paquete
| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| `actions`, `assets`, `events`, `fx`, `helpers`, `images` | Acciones, assets, eventos de dominio, imágenes, helpers matemáticos | [`Action`](https://github.com/local/MVCGameEngine/blob/work/src/utils/actions/Action.java), [`ActionDTO`](https://github.com/local/MVCGameEngine/blob/work/src/utils/actions/ActionDTO.java), [`AssetCatalog`](https://github.com/local/MVCGameEngine/blob/work/src/utils/assets/core/AssetCatalog.java), [`ProjectAssets`](https://github.com/local/MVCGameEngine/blob/work/src/utils/assets/implementations/ProjectAssets.java), [`Images`](https://github.com/local/MVCGameEngine/blob/work/src/utils/images/Images.java), [`ImageCache`](https://github.com/local/MVCGameEngine/blob/work/src/utils/images/ImageCache.java), [`DomainEvent`](https://github.com/local/MVCGameEngine/blob/work/src/utils/events/domain/ports/eventtype/DomainEvent.java), [`DoubleVector`](https://github.com/local/MVCGameEngine/blob/work/src/utils/helpers/DoubleVector.java) |

### Análisis detallado
- **Propósito:** Ofrecer módulos reutilizables para el core y el game.
- **Interacciones:** Consumido por `model`, `view`, `controller` y `game`.
- **Patrones:** DTO, Event objects, Cache.
- **Riesgos:** Cambios en eventos o assets afectan múltiples capas.
- **Recomendaciones:** Documentar contracts de eventos y asset IDs.

### Subpaquete: `utils.events.domain`
**Descripción:** Definiciones de eventos de dominio y payloads.

| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| `ports`, `core` | Eventos de colisión, límites, emisión | [`DomainEvent`](https://github.com/local/MVCGameEngine/blob/work/src/utils/events/domain/ports/eventtype/DomainEvent.java), [`CollisionEvent`](https://github.com/local/MVCGameEngine/blob/work/src/utils/events/domain/ports/eventtype/CollisionEvent.java), [`EmitEvent`](https://github.com/local/MVCGameEngine/blob/work/src/utils/events/domain/ports/eventtype/EmitEvent.java), [`LimitEvent`](https://github.com/local/MVCGameEngine/blob/work/src/utils/events/domain/ports/eventtype/LimitEvent.java), [`LifeOver`](https://github.com/local/MVCGameEngine/blob/work/src/utils/events/domain/ports/eventtype/LifeOver.java), [`AbstractDomainEvent`](https://github.com/local/MVCGameEngine/blob/work/src/utils/events/domain/core/AbstractDomainEvent.java), [`CollisionPayload`](https://github.com/local/MVCGameEngine/blob/work/src/utils/events/domain/ports/payloads/CollisionPayload.java), [`EmitPayloadDTO`](https://github.com/local/MVCGameEngine/blob/work/src/utils/events/domain/ports/payloads/EmitPayloadDTO.java) |

**Análisis detallado**
- **Responsabilidades:** Representar eventos del dominio con payloads específicos.
- **Interacciones:** `Model` genera eventos; `Controller` decide acciones.
- **Patrones:** Event Object, DTO.
- **Recomendaciones:** Definir catálogo de eventos con documentación pública.

### Subpaquete: `utils.assets`
**Descripción:** Catálogo de assets y metadatos.

| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| `ports`, `implementations`, `core` | Registro de assets y metadata | [`AssetCatalog`](https://github.com/local/MVCGameEngine/blob/work/src/utils/assets/core/AssetCatalog.java), [`ProjectAssets`](https://github.com/local/MVCGameEngine/blob/work/src/utils/assets/implementations/ProjectAssets.java), [`AssetInfoDTO`](https://github.com/local/MVCGameEngine/blob/work/src/utils/assets/ports/AssetInfoDTO.java), [`AssetType`](https://github.com/local/MVCGameEngine/blob/work/src/utils/assets/ports/AssetType.java), [`AssetIntensity`](https://github.com/local/MVCGameEngine/blob/work/src/utils/assets/ports/AssetIntensity.java) |

**Análisis detallado**
- **Responsabilidades:** Registrar assets y ofrecer selección aleatoria por tipo.
- **Interacciones:** Consumido por `view` y `world`.
- **Recomendaciones:** Validar existencia de archivos en tiempo de carga.

### Subpaquete: `utils.images`
**Descripción:** Carga y cacheo de imágenes.

| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| — | Cache de imágenes y DTOs | [`Images`](https://github.com/local/MVCGameEngine/blob/work/src/utils/images/Images.java), [`ImageCache`](https://github.com/local/MVCGameEngine/blob/work/src/utils/images/ImageCache.java), [`ImageDTO`](https://github.com/local/MVCGameEngine/blob/work/src/utils/images/ImageDTO.java), [`ImageCacheKeyDTO`](https://github.com/local/MVCGameEngine/blob/work/src/utils/images/ImageCacheKeyDTO.java) |

**Análisis detallado**
- **Responsabilidades:** Cargar imágenes y gestionar cache eficiente para render.
- **Interacciones:** Usado por `view.Renderer`.
- **Patrones:** Cache.
- **Recomendaciones:** Añadir métricas de hit rate y tamaño.

### Subpaquete: `utils.actions`
**Descripción:** Acciones del motor.

| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| — | Enumeración y DTO de acciones | [`Action`](https://github.com/local/MVCGameEngine/blob/work/src/utils/actions/Action.java), [`ActionDTO`](https://github.com/local/MVCGameEngine/blob/work/src/utils/actions/ActionDTO.java) |

**Análisis detallado**
- **Responsabilidades:** Representar acciones generadas por reglas.
- **Interacciones:** `Model` ejecuta acciones; `Controller` las genera.
- **Recomendaciones:** Documentar prioridad y orden de acciones.

### Subpaquete: `utils.helpers`
**Descripción:** Helpers matemáticos y de colecciones.

| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| — | Vectores, listas aleatorias | [`DoubleVector`](https://github.com/local/MVCGameEngine/blob/work/src/utils/helpers/DoubleVector.java), [`RandomArrayList`](https://github.com/local/MVCGameEngine/blob/work/src/utils/helpers/RandomArrayList.java) |

**Análisis detallado**
- **Responsabilidades:** Ayudas para cálculos y randomización.
- **Recomendaciones:** Añadir tests de consistencia.

### Subpaquete: `utils.fx`
**Descripción:** Efectos visuales básicos.

| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| — | Efectos y tipos de FX | [`Fx`](https://github.com/local/MVCGameEngine/blob/work/src/utils/fx/Fx.java), [`FxType`](https://github.com/local/MVCGameEngine/blob/work/src/utils/fx/FxType.java), [`FxImage`](https://github.com/local/MVCGameEngine/blob/work/src/utils/fx/FxImage.java), [`Spin`](https://github.com/local/MVCGameEngine/blob/work/src/utils/fx/Spin.java) |

**Análisis detallado**
- **Responsabilidades:** Tipos de efectos y transformaciones visuales.
- **Interacciones:** Potencialmente usado por renderables.
- **Recomendaciones:** Clarificar uso actual y futuro.

---

## Paquete: `resources`
**Descripción:** Contiene recursos estáticos (imágenes). No hay clases Java bajo este árbol.

### Resumen del paquete
| Subpaquetes | Responsabilidades clave | Principales clases |
| --- | --- | --- |
| `images` | Activos gráficos | — |

**Análisis detallado**
- **Responsabilidades:** Almacenar assets consumidos por `utils.assets` y `view`.
- **Interacciones:** Referenciado mediante rutas en `ProjectAssets`.
- **Recomendaciones:** Verificar naming consistente y estructura de carpetas.

---

## Explora más
- Árbol del repo: https://github.com/local/MVCGameEngine/tree/work/src

## Limitaciones
Este informe se basa en exploración automática del árbol `src/` y podría omitir archivos no Java u otros recursos fuera de `src/`. Se recomienda revisar manualmente el repositorio para confirmar cambios recientes y configuraciones externas.

## Siguientes pasos sugeridos
1. Revisar el pipeline de colisiones y eventos en `model` con tests de estrés y escenarios de alta densidad.
2. Añadir pruebas unitarias para mappers en `controller.mappers`.
3. Documentar contratos de DTOs y eventos en `utils.events.domain` para facilitar extensibilidad.

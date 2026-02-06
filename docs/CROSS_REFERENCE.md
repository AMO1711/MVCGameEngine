# Análisis de acoplamiento/coherencia por imports

## A) INVENTARIO

| Paquete | Archivos .java | Fan-out (imports) | Fan-in (imports recibidos) |
|---|---|---|---|
| ai | 1 | 7 | 0 |
| assets.core | 1 | 8 | 7 |
| assets.impl | 1 | 3 | 4 |
| assets.ports | 3 | 0 | 11 |
| engine.actions | 2 | 2 | 18 |
| engine.controller.impl | 1 | 27 | 1 |
| engine.controller.mappers | 7 | 16 | 6 |
| engine.controller.ports | 3 | 8 | 16 |
| engine.events.domain.core | 1 | 3 | 4 |
| engine.events.domain.ports | 3 | 2 | 29 |
| engine.events.domain.ports.eventtype | 5 | 16 | 40 |
| engine.events.domain.ports.payloads | 4 | 2 | 7 |
| engine.generators | 3 | 22 | 2 |
| engine.model.bodies.core | 1 | 17 | 6 |
| engine.model.bodies.impl | 4 | 31 | 7 |
| engine.model.bodies.ports | 6 | 13 | 37 |
| engine.model.emitter.core | 1 | 5 | 1 |
| engine.model.emitter.impl | 2 | 7 | 1 |
| engine.model.emitter.ports | 2 | 3 | 10 |
| engine.model.impl | 1 | 40 | 1 |
| engine.model.physics.core | 1 | 3 | 2 |
| engine.model.physics.implementations | 2 | 4 | 2 |
| engine.model.physics.ports | 2 | 1 | 17 |
| engine.model.ports | 2 | 4 | 3 |
| engine.model.weapons.core | 1 | 7 | 4 |
| engine.model.weapons.implementations | 4 | 12 | 4 |
| engine.model.weapons.ports | 5 | 7 | 21 |
| engine.utils.fx | 4 | 12 | 0 |
| engine.utils.helpers | 2 | 3 | 9 |
| engine.utils.images | 4 | 20 | 5 |
| engine.utils.spatial.core | 2 | 3 | 7 |
| engine.utils.spatial.ports | 1 | 0 | 3 |
| engine.view.core | 4 | 48 | 1 |
| engine.view.hud.core | 9 | 27 | 3 |
| engine.view.hud.impl | 3 | 9 | 3 |
| engine.view.renderables.impl | 2 | 11 | 4 |
| engine.view.renderables.ports | 4 | 0 | 18 |
| engine.worlddef.core | 3 | 21 | 2 |
| engine.worlddef.ports | 9 | 3 | 38 |
| level | 1 | 8 | 0 |
| rules | 6 | 63 | 0 |
| world | 2 | 11 | 0 |

## B) GRID 1: MATRIZ RESUMIDA (Top 15 fan-out vs Top 15 fan-in)

| Package | engine.events.domain.ports.eventtype | engine.worlddef.ports | engine.model.bodies.ports | engine.events.domain.ports | engine.model.weapons.ports | engine.actions | engine.view.renderables.ports | engine.model.physics.ports | engine.controller.ports | assets.ports | engine.model.emitter.ports | engine.utils.helpers | assets.core | engine.events.domain.ports.payloads | engine.model.bodies.impl | OTHER_INTERNAL | external |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| rules | 30 | 0 | 3 | 6 | 0 | 12 | 0 | 0 | 6 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 6 |
| engine.view.core | 0 | 0 | 0 | 0 | 0 | 0 | 8 | 0 | 2 | 1 | 0 | 2 | 1 | 0 | 0 | 5 | 25 |
| engine.model.impl | 5 | 0 | 6 | 3 | 3 | 2 | 0 | 1 | 0 | 0 | 2 | 1 | 0 | 2 | 3 | 5 | 6 |
| engine.model.bodies.impl | 0 | 0 | 12 | 1 | 2 | 0 | 0 | 6 | 0 | 0 | 1 | 0 | 0 | 0 | 0 | 5 | 1 |
| engine.controller.impl | 1 | 2 | 1 | 0 | 1 | 1 | 4 | 0 | 3 | 0 | 1 | 1 | 1 | 0 | 0 | 1 | 2 |
| engine.view.hud.core | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 27 |
| engine.generators | 0 | 13 | 0 | 0 | 0 | 0 | 0 | 0 | 3 | 0 | 0 | 1 | 0 | 0 | 0 | 0 | 5 |
| engine.worlddef.core | 0 | 10 | 1 | 0 | 0 | 0 | 0 | 0 | 0 | 3 | 0 | 1 | 2 | 0 | 0 | 2 | 2 |
| engine.utils.images | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 20 |
| engine.model.bodies.core | 1 | 0 | 3 | 1 | 0 | 1 | 0 | 2 | 0 | 0 | 1 | 0 | 0 | 0 | 0 | 1 | 7 |
| engine.controller.mappers | 0 | 3 | 3 | 0 | 2 | 0 | 4 | 0 | 0 | 0 | 1 | 0 | 0 | 0 | 0 | 1 | 2 |
| engine.events.domain.ports.eventtype | 0 | 0 | 0 | 8 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 4 | 0 | 4 | 0 |
| engine.model.bodies.ports | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 4 | 0 | 0 | 0 | 0 | 0 | 0 | 4 | 2 | 1 |
| engine.model.weapons.implementations | 0 | 0 | 0 | 0 | 8 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 4 | 0 |
| engine.utils.fx | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 2 | 10 |
| OTHER_INTERNAL | 1 | 8 | 2 | 4 | 5 | 1 | 2 | 4 | 2 | 4 | 4 | 2 | 1 | 1 | 0 | 6 | 29 |
| external | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |

## C) GRID 2: COHERENCIA POR CAPAS (HEURÍSTICAS)

Clasificación por sufijo: `*.ports`, `*.core`, `*.implementations`, otros=`misc`.

| Regla | ports | core | implementations | misc | Notas |
|---|---|---|---|---|---|
| R1 ports NO -> implementations | ❌ | — | — | — | ports→implementations=5 |
| R2 core NO -> implementations | — | ✅ | — | — | core→implementations=0 |
| R3 implementations MAY -> ports/core | — | — | ✅ | — |  |
| R4 ciclos directos A<->B | ⚠️ | ⚠️ | ⚠️ | ⚠️ | 4 pares |
| R5 hub excesivo (top 10% fan-in) | ⚠️ | ⚠️ | ⚠️ | ⚠️ | 5 hubs |
| R6 external >40% fan-out | ⚠️ | ⚠️ | ⚠️ | ⚠️ | 13 paquetes |

**Paquetes por capa**

- **ports** (11): assets.ports, engine.controller.ports, engine.events.domain.ports, engine.model.bodies.ports, engine.model.emitter.ports, engine.model.physics.ports, engine.model.ports, engine.model.weapons.ports, engine.utils.spatial.ports, engine.view.renderables.ports, engine.worlddef.ports

- **core** (10): assets.core, engine.events.domain.core, engine.model.bodies.core, engine.model.emitter.core, engine.model.physics.core, engine.model.weapons.core, engine.utils.spatial.core, engine.view.core, engine.view.hud.core, engine.worlddef.core

- **implementations** (2): engine.model.physics.implementations, engine.model.weapons.implementations

- **misc** (19): ai, assets.impl, engine.actions, engine.controller.impl, engine.controller.mappers, engine.events.domain.ports.eventtype, engine.events.domain.ports.payloads, engine.generators, engine.model.bodies.impl, engine.model.emitter.impl, engine.model.impl, engine.utils.fx, engine.utils.helpers, engine.utils.images, engine.view.hud.impl, engine.view.renderables.impl, level, rules, world


**Detalles de reglas**

- R1 violaciones: engine.model.weapons.ports→engine.model.weapons.implementations=4, engine.model.bodies.ports→engine.model.physics.implementations=1

- R2 sin violaciones.

- R4 ciclos directos: engine.controller.impl<->engine.view.core, engine.model.bodies.core<->engine.model.bodies.ports, engine.model.bodies.impl<->engine.model.bodies.ports, engine.model.weapons.implementations<->engine.model.weapons.ports

- R5 hubs (top 10% fan-in): engine.events.domain.ports.eventtype, engine.worlddef.ports, engine.model.bodies.ports, engine.events.domain.ports, engine.model.weapons.ports

- R6 external >40% fan-out: engine.utils.spatial.core, engine.model.ports, engine.utils.fx, engine.model.physics.ports, engine.utils.images, engine.utils.helpers, engine.model.weapons.core, assets.core, engine.view.renderables.impl, engine.model.bodies.core, engine.view.core, engine.view.hud.impl, engine.view.hud.core


## D) TOP LISTS PRIORIZADAS (ACCIÓN)

### D1) Top 10 fan-out (con % external)

| Paquete | Fan-out | External (N, %) |
|---|---|---|
| rules | 63 | 6 (9.5%) |
| engine.view.core | 48 | 25 (52.1%) |
| engine.model.impl | 40 | 6 (15.0%) |
| engine.model.bodies.impl | 31 | 1 (3.2%) |
| engine.controller.impl | 27 | 2 (7.4%) |
| engine.view.hud.core | 27 | 27 (100.0%) |
| engine.generators | 22 | 5 (22.7%) |
| engine.worlddef.core | 21 | 2 (9.5%) |
| engine.utils.images | 20 | 20 (100.0%) |
| engine.model.bodies.core | 17 | 7 (41.2%) |

### D2) Top 10 fan-in

| Paquete | Fan-in |
|---|---|
| engine.events.domain.ports.eventtype | 40 |
| engine.worlddef.ports | 38 |
| engine.model.bodies.ports | 37 |
| engine.events.domain.ports | 29 |
| engine.model.weapons.ports | 21 |
| engine.actions | 18 |
| engine.view.renderables.ports | 18 |
| engine.model.physics.ports | 17 |
| engine.controller.ports | 16 |
| assets.ports | 11 |

### D3) Top 15 pares A→B por volumen

| A (origen) | B (destino) | Imports |
|---|---|---|
| rules | engine.events.domain.ports.eventtype | 30 |
| engine.view.hud.core | external | 27 |
| engine.view.core | external | 25 |
| engine.utils.images | external | 20 |
| engine.generators | engine.worlddef.ports | 13 |
| engine.model.bodies.impl | engine.model.bodies.ports | 12 |
| rules | engine.actions | 12 |
| engine.utils.fx | external | 10 |
| engine.worlddef.core | engine.worlddef.ports | 10 |
| engine.events.domain.ports.eventtype | engine.events.domain.ports | 8 |
| engine.model.weapons.implementations | engine.model.weapons.ports | 8 |
| engine.view.core | engine.view.renderables.ports | 8 |
| engine.model.bodies.core | external | 7 |
| engine.view.renderables.impl | external | 7 |
| engine.controller.impl | engine.controller.mappers | 6 |

### D4) Top 15 asimetrías A→B vs B→A

| A | B | A→B | B→A | |Δ| |
|---|---|---|---|---|
| rules | engine.events.domain.ports.eventtype | 30 | 0 | 30 |
| engine.view.hud.core | external | 27 | 0 | 27 |
| engine.view.core | external | 25 | 0 | 25 |
| engine.utils.images | external | 20 | 0 | 20 |
| engine.generators | engine.worlddef.ports | 13 | 0 | 13 |
| rules | engine.actions | 12 | 0 | 12 |
| engine.utils.fx | external | 10 | 0 | 10 |
| engine.worlddef.core | engine.worlddef.ports | 10 | 0 | 10 |
| engine.model.bodies.impl | engine.model.bodies.ports | 12 | 4 | 8 |
| engine.events.domain.ports.eventtype | engine.events.domain.ports | 8 | 0 | 8 |
| engine.view.core | engine.view.renderables.ports | 8 | 0 | 8 |
| engine.model.bodies.core | external | 7 | 0 | 7 |
| engine.view.renderables.impl | external | 7 | 0 | 7 |
| engine.controller.impl | engine.controller.mappers | 6 | 0 | 6 |
| engine.model.bodies.impl | engine.model.physics.ports | 6 | 0 | 6 |

### D5) Ciclos (SCCs)

- engine.actions, engine.events.domain.core, engine.events.domain.ports, engine.events.domain.ports.eventtype, engine.events.domain.ports.payloads, engine.model.bodies.core, engine.model.bodies.impl, engine.model.bodies.ports, engine.model.emitter.ports, engine.model.weapons.core, engine.model.weapons.implementations, engine.model.weapons.ports

- engine.controller.impl, engine.view.core


## E) DIAGNÓSTICO (NO GENÉRICO)

**Arquitectura inferida por patrones de imports**

- Predominan paquetes `*.ports` con alto fan-in (p.ej., `engine.events.domain.ports.eventtype`, `engine.worlddef.ports`) sugiriendo contratos/API consumidos por capas superiores.

- Paquetes `*.core` aparecen como centros de lógica/estado (p.ej., `engine.view.core`, `engine.view.hud.core`) con alto fan-out hacia external y otros paquetes, actuando como coordinadores.

- Paquetes `*.implementations` existen en física y armas, con imports a `*.ports`/`*.core`, lo que sugiere adaptadores concretos para contratos del dominio.


**Problemas más relevantes (evidencia por imports)**

- Violación de puertos→implementaciones: `engine.model.bodies.ports` y `engine.model.weapons.ports` importan `engine.model.physics.implementations`/`engine.model.weapons.implementations` (5 imports en total).

- Dependencias externas altas en capa core/view: `engine.view.core` 25/48 (52.1%) y `engine.view.hud.core` 27/27 (100%) hacia external.

- Paquetes hub con fan-in elevado: `engine.events.domain.ports.eventtype` (40), `engine.worlddef.ports` (38), `engine.model.bodies.ports` (37).

- SCC grande entre eventos/modelos/acciones (`engine.actions`, `engine.events.domain.*`, `engine.model.*`), lo que indica acoplamiento circular a nivel de paquetes.

- Ciclo directo `engine.controller.impl` <-> `engine.view.core` (mutuas importaciones).

- Asimetrías pronunciadas: `rules`→`engine.events.domain.ports.eventtype` (30) sin retorno; indica dependencia unidireccional concentrada.

- `engine.model.bodies.impl` con fan-out alto (31) y múltiples dependencias a puertos, física y utilidades, sugiriendo implementación con demasiadas responsabilidades.

- `engine.view.renderables.impl` depende de `engine.view.renderables.ports` y `engine.utils.images` además de external, mezclando roles de render + infraestructura.


**Puntos buenos (coherencia)**

- `engine.model.physics.implementations` depende de `engine.model.physics.core` y `engine.model.physics.ports`, alineado con el patrón de implementación que consume contratos del dominio.

- `engine.view.renderables.ports` y `engine.controller.ports` concentran fan-in alto, indicando APIs reutilizadas por varias capas.

- `engine.worlddef.ports` es utilizado por generadores, controller y world, lo que sugiere un punto de contrato estable para definición de mundos.

- `engine.model.weapons.implementations` importan `engine.model.weapons.core` y `engine.model.weapons.ports`, coherente con separación core/ports/impl.

- `assets.core` y `assets.ports` presentan dependencias externas moderadas y direccionalidad clara hacia puertos.

- Paquetes utilitarios (`engine.utils.*`) son principalmente consumidores de external y pocos internos, minimizando acoplamiento cruzado entre dominios.

- `engine.events.domain.ports.payloads` se mantiene como sub-API (fan-in 7) con dependencias limitadas.


## F) RECOMENDACIONES CONCRETAS (MÍNIMO 8)

| # | Qué cambiar | Por qué (datos de imports) | Impacto esperado | Riesgo |
|---|---|---|---|---|
| 1 | Eliminar dependencias de `*.ports` hacia `*.implementations` | Actualmente `engine.model.bodies.ports`→`engine.model.physics.implementations` (1) y `engine.model.weapons.ports`→`engine.model.weapons.implementations` (4). | Reduce acoplamiento y permite reemplazar implementaciones sin tocar contratos. | Puede requerir extraer interfaces/mover tipos usados en firmas. |
| 2 | Introducir interfaces en `engine.model.physics.ports` para usos desde puertos de cuerpos | Se observa import directo a implementaciones (1). | Aisla la capa de puertos y favorece testability con mocks. | Cambios en firmas públicas y posibles adaptadores. |
| 3 | Separar responsabilidades en `engine.model.bodies.impl` | Tiene fan-out 31 con dependencias a múltiples puertos y utilidades. | Mejora cohesión y reduce cascadas de cambios. | Refactor puede afectar comportamiento en runtime. |
| 4 | Romper el ciclo `engine.controller.impl` <-> `engine.view.core` | Ciclo directo detectado (imports en ambos sentidos). | Facilita pruebas unitarias y reduce dependencia mutua. | Requiere introducir DTOs o puertos adicionales. |
| 5 | Reducir dependencia externa en `engine.view.hud.core` | 100% de sus 27 imports son external. | Menor lock-in a frameworks y mejora portabilidad. | Riesgo de rework en capas de UI si se abstraen librerías. |
| 6 | Crear facade para `engine.events.domain.ports.eventtype` | Fan-in 40 y alto uso desde `rules` (30). | Simplifica API y reduce dispersión de imports. | Puede requerir migración gradual de consumidores. |
| 7 | Definir módulo de eventos como dependencia unidireccional | SCC grande incluye `engine.actions`, `engine.events.domain.*`, `engine.model.*`. | Aclara direcciones de dependencia y reduce ciclos. | Puede exigir mover tipos compartidos a `*.ports` o `*.core`. |
| 8 | Limitar el rol de `engine.view.core` a coordinación | Fan-out 48 con 25 external y dependencias a renderables/hud. | Disminuye acoplamiento con render y facilita pruebas. | Refactor puede requerir split en submódulos. |
| 9 | Aislar `engine.worlddef.ports` de dependencias de assets | `engine.worlddef.ports` importa `assets.core` (1) y `engine.model.bodies.ports` (1). | Mantiene puertos como contratos puros sin dependencias de infraestructura. | Puede requerir mover tipos a `assets.ports` o DTOs. |

## Validación final

- Análisis realizado exclusivamente con sentencias `package` e `import` de archivos `.java` bajo `src/`.
